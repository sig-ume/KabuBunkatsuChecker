/**
 *
 */
package jp.sigre.KabuBunkatsuChecker.downloadinfo.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author sigre
 *
 */
public class ParseHtmlStockSplit {

	final int NORMAL_END = 0;
	final int ERROR_SAMEFILENAME = 1;
	final int ERROR_NOFOLDEREXIST = 2;
	final int ERROR_IOERROR = 3;
	final int ERROR_WEBCONNECT = 4;
	final int ERROR_OTHER = 5;
	final int ERROR_DATAINCOLLECT = 6;

	/**
	 *
	 */
	public ParseHtmlStockSplit() {
	}

	public int makeSplitCsv(String strFolder, String strDate) {
		String url = "http://kabu.com/process/bunkatu.js";

		File folder = new File(strFolder);
		if (!folder.exists()) return ERROR_NOFOLDEREXIST;

		String outputFilePath = strFolder;
		if (!strFolder.endsWith("\\")) outputFilePath += "\\";
		outputFilePath += strDate + ".csv";

		File file = new File(outputFilePath);
		if (file.exists()) return ERROR_SAMEFILENAME;

		List<SplitMergeInfo> infoList = null;
		try {
			infoList = getSplitData(url);
		} catch (UnknownHostException e) {
			return ERROR_WEBCONNECT;
		} catch (IOException e) {
			return ERROR_OTHER;
		} catch (NoDataException e) {
			return ERROR_DATAINCOLLECT;
		} catch (Exception e) {
			return ERROR_OTHER;
		}

		for (SplitMergeInfo info : infoList) {
			if (!checkData(info)) return ERROR_DATAINCOLLECT;
		}

		return makeCsvFile(outputFilePath, infoList);

	}

	public int makeMergeCsv(String strFolder, String strDate) {
		String url = "http://kabu.com/process/gensi.js";

		File folder = new File(strFolder);
		if (!folder.exists()) return ERROR_NOFOLDEREXIST;

		String outputFilePath = strFolder;
		if (!strFolder.endsWith("\\")) outputFilePath += "\\";
		outputFilePath += strDate + ".csv";

		File file = new File(outputFilePath);
		if (file.exists()) return ERROR_SAMEFILENAME;


		List<SplitMergeInfo> infoList = null;
		try {
			infoList = getMergeData(url);
		} catch (UnknownHostException e) {
			return ERROR_WEBCONNECT;
		} catch (IOException e) {
			return ERROR_OTHER;
		} catch (NoDataException e) {
			return ERROR_DATAINCOLLECT;
		} catch (Exception e) {
			return ERROR_OTHER;
		}


		for (SplitMergeInfo info : infoList) {
			if (!checkData(info)) {
				return ERROR_DATAINCOLLECT;
			}
		}

		return makeCsvFile(outputFilePath, infoList);

	}

	private int makeCsvFile (String outputFilePath, List<SplitMergeInfo> infoList) {
		Writer iowriter = null;
		CSVWriter writer = null;
		try {

			iowriter = new OutputStreamWriter(
					new FileOutputStream(outputFilePath),"UTF-8");
			writer = new CSVWriter(iowriter, ',', CSVWriter.NO_QUOTE_CHARACTER);

			Iterator<SplitMergeInfo> ite = infoList.iterator();
			while(ite.hasNext()){

				SplitMergeInfo tt = ite.next();

				//効力発生日、銘柄コード、比率、権利付最終日、フラグ、
				//セパフラグ（0で固定）、備忘列
				String[] entries = new String[]{tt.getStrStartDate(),
						tt.getStrStockCode(),
						removePointZero(String.valueOf(Double.parseDouble(tt.getStrWariateRate2()))),
						tt.getStrLastDate(),
						String.valueOf(tt.isSplit),
						"0",
						""
				};

				writer.writeNext(entries);

			}
			writer.close();

		}catch (Exception e) {
			return ERROR_IOERROR;
		}finally{

			try{
				iowriter.close();
			}catch(Exception e){
				return ERROR_IOERROR;
			}

		}

		return NORMAL_END;
	}

	private String removePointZero(String str) {
		if (str.endsWith(".0")) return str.replace(".0", "");

		return str;
	}

	public List<SplitMergeInfo> getMergeData(String url)
			throws UnknownHostException, IOException, NoDataException, Exception{

		Document document = null;
		List<SplitMergeInfo> groupList = new ArrayList<>();
		SplitMergeInfo info = new SplitMergeInfo();
		//boolean halfFlag = true;

		document = Jsoup.connect(url).get();

		//System.out.println(document.html());

		String strSite = document.html().replace("\n", "");

		String regex = "document.write\\(\\'\\s*<tr>\\s*\\\\\\s*<td>(.*?)</td>\\\\\\s*<td>(.*?)</td>.*?<td>(.*?)</td>.*?\\<td>'\\);";

		//System.out.println(strSite);
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(strSite);
		while (m.find()){
			info = new SplitMergeInfo();
			info.setStrStartDate(m.group(1));
			info.setStrStockCode(m.group(2));
			info.setStrStockName(m.group(3));
			info.setIsSplit(0);

			groupList.add(info);

		}

		if (groupList.size()==0) throw new NoDataException();

		regex = "document.write\\(\\'</td>\\\\\\s*?<td>(.*?)</td>\\\\\\s*?</tr>\\'\\);";
		p = Pattern.compile(regex);

		//System.out.println(regex);
		int count = 0;

		m = p.matcher(strSite);
		while (m.find()){
			//			System.out.println("group:" + m.group());
			//			System.out.println(m.group(1));
			groupList.get(count).setStrLastDate(m.group(1));
			count += 1;
		}

		regex = "BRatioW\\s=\\s&quot;(.*?)&quot;;";
		p = Pattern.compile(regex);

		count = 0;

		m = p.matcher(strSite);
		while (m.find()){
			groupList.get(count).setIntWariateRate1(m.group(1));
			count += 1;
		}

		regex = "ARatioW\\s=\\s&quot;(.*?)&quot;;";
		p = Pattern.compile(regex);

		count = 0;

		m = p.matcher(strSite);
		while (m.find()){
			groupList.get(count).setIntWariateRate2(m.group(1));
			count += 1;
		}
		//
		//		for (SplitMergeInfo group : groupList) {
		//			System.out.println(group.toString());
		//		}

		return groupList;
	}

	public List<SplitMergeInfo> getSplitData(String url)
			throws UnknownHostException, IOException, NoDataException, Exception{
		Document document = null;
		//String url = "http://kabu.com/process/bunkatu.js";
		List<SplitMergeInfo> groupList = new ArrayList<>();
		SplitMergeInfo info = new SplitMergeInfo();
		boolean halfFlag = true;

		document = Jsoup.connect(url).get();

		//System.out.println(document.html());

		String strSite = document.html().replace("\n", "");

		String regex = "document.write\\(\\'.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?\\'\\);";

		//System.out.println(strSite);
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(strSite);
		while (m.find()){
			//System.out.println("groupcount:" + m.groupCount());
			//System.out.println("group1:" + m.group(1) + m.group(2) + m.group(3));
			if (halfFlag) {
				info = new SplitMergeInfo();
				info.setStrWariateDate(m.group(1));
				info.setStrStockCode(m.group(2));
				info.setStrStockName(m.group(3));
				halfFlag = !halfFlag;
			} else {
				info.setStrLastDate(m.group(1));
				info.setStrStartDate(m.group(2));
				info.setStrSalableDate(m.group(3));
				info.setIsSplit(1);
				//test
				groupList.add(info);
				halfFlag = !halfFlag;

			}
		}

		if (groupList.size()==0) throw new NoDataException();

		regex = "BRatioW\\s=\\s&quot;(.*?)&quot;;";
		p = Pattern.compile(regex);

		//System.out.println(regex);
		int count = 0;

		m = p.matcher(strSite);
		while (m.find()){
			groupList.get(count).setIntWariateRate1(m.group(1));
			count += 1;
		}


		regex = "ARatioW\\s=\\s&quot;(.*?)&quot;;";
		p = Pattern.compile(regex);

		//System.out.println(regex);
		count = 0;

		m = p.matcher(strSite);
		while (m.find()){
			groupList.get(count).setIntWariateRate2(m.group(1));
			count += 1;
		}

		//		for (SplitMergeInfo group : groupList) {
		//			System.out.println(group.toString());
		//		}

		return groupList;
	}

	public boolean checkData (SplitMergeInfo info) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		if (info.getIsSplit()==0) {
			if (!info.getStrWariateDate().equals("")) return false;
			if (!info.getStrSalableDate().equals("")) return false;
		} else if (info.getIsSplit()==1) {
			if (info.strWariateDate.length()!=10) return false;
			if (info.strSalableDate.length()!=10) return false;
		} else return false;

		if (info.strLastDate.length()!=10) return false;
		if (info.strStartDate.length()!=10) return false;
		if (info.strStockCode.length()>=6) return false;

		try {
			Date formatDate = null;
			//割当日
			if (info.getIsSplit()==1) formatDate = sdf.parse(info.strWariateDate);
			//権利付き最終日
			formatDate = sdf.parse(info.strLastDate);
			//効力発生日
			formatDate = sdf.parse(info.strStartDate);
			//売却可能予定日
			if (info.getIsSplit()==1) formatDate = sdf.parse(info.strSalableDate);
			//銘柄コード
			Integer.parseInt(info.strStockCode);
			//割当比率1
			Double.parseDouble(info.strWariateRate1);
			//割当比率2
			Double.parseDouble(info.strWariateRate2);

		} catch (Exception e) {
			return false;
		}

		//銘柄名
		if (info.strStockName.length()==0) return false;

		return true;
	}

	public String getUpdateDate(String name)
			throws UnknownHostException, IOException, ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		String strUpdate = "";
		Date dateUpdate = null;

		Document document = Jsoup.connect("http://kabu.com/process/LastUpdate.js").get();

		for (String line : document.html().split("; ")) {
			if (line.startsWith(name)) {
				int intLength = line.length();
				strUpdate = line.substring(intLength-15, intLength-5);
			}
		}

		dateUpdate = sdf.parse(strUpdate);

		return strUpdate;
	}
}

class NoDataException extends Exception {
	public NoDataException() {
		super();
	}
}
