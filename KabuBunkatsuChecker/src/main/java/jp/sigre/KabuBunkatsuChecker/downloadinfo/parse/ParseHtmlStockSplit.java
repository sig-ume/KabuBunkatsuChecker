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

	public final static int NORMAL_END = 0;
	public final static int ERROR_SAMEFILENAME = 1;
	public final static int ERROR_NOFOLDEREXIST = 2;
	public final static int ERROR_IOERROR = 3;
	public final static int ERROR_WEBCONNECT = 4;
	public final static int ERROR_OTHER = 5;
	public final static int ERROR_DATAINCOLLECT = 6;
	public final static int NO_UPDATE = 7;
	public final static int ERROR_SQL = 8;

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
		if (!strFolder.endsWith(File.separator)) outputFilePath += File.separator;
		outputFilePath += strDate;

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
		if (!strFolder.endsWith(File.separator)) outputFilePath += File.separator;
		outputFilePath += strDate;

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

	List<SplitMergeInfo> getMergeData(String url)
			throws UnknownHostException, IOException, NoDataException, Exception{

		Document document = null;
		List<SplitMergeInfo> groupList = new ArrayList<>();
		SplitMergeInfo info = new SplitMergeInfo();
		//boolean halfFlag = true;

		document = Jsoup.connect(url).ignoreContentType(true).get();

		String strSite = document.html().replace("\n", "");

		groupList = getMergeBase(strSite);

		if (groupList.size()==0) throw new NoDataException();

		List<String> lastDateList = getMergeLastDateList(strSite);
		List<String> aRatioList = getARatioList(strSite);
		List<String> bRatioList = getBRatioList(strSite);

		int lstDateCount = lastDateList.size();
		int aRatioCount = aRatioList.size();
		int bRatioCount = bRatioList.size();
		int ratioCount = aRatioCount < bRatioCount ? aRatioCount : bRatioCount;
		ratioCount = ratioCount < lstDateCount ? ratioCount : lstDateCount;

		int count = groupList.size() > ratioCount ? ratioCount : groupList.size();

		int i;
		for (i = 0; i < count; i++) {
			SplitMergeInfo info1 = groupList.get(i);
			String aRatio = aRatioList.get(i);
			String bRatio = bRatioList.get(i);
			String lstDate = lastDateList.get(i);
			info1.setIntWariateRate2(bRatio);
			info1.setIntWariateRate1(aRatio);
			info1.setStrLastDate(lstDate);
		}

		for (; i < groupList.size(); i++) {
			groupList.remove(i);
		}

		return groupList;
	}

	private List<SplitMergeInfo> getMergeBase(String strSite) {

		List<SplitMergeInfo> groupList = new ArrayList<>();

		String regex = "document.write\\(\\'\\s*<tr>\\s*\\\\\\s*<td>(.*?)</td>\\\\\\s*<td>(.*?)</td>.*?<td>(.*?)</td>.*?\\<td>'\\);";

		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(strSite);
		while (m.find()){
			SplitMergeInfo info = new SplitMergeInfo();
			info.setStrStartDate(m.group(1));
			info.setStrStockCode(m.group(2));
			info.setStrStockName(m.group(3));
			info.setIsSplit(0);

			groupList.add(info);

		}

		return groupList;
	}

	List<String> getMergeLastDateList(String strSite) {

		List<String> result = new ArrayList<>();

		String regex = "document.write\\(\\'</td>\\\\\\s*?<td>(.*?)</td>\\\\\\s*?</tr>\\'\\);";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(strSite);
		while (m.find()){
			result.add(m.group(1));
		}

		return result;
	}

	List<SplitMergeInfo> getSplitData(String url)
			throws UnknownHostException, IOException, NoDataException, Exception{
		Document document = null;

		boolean halfFlag = true;
		String regex = "";

		document = Jsoup.connect(url).ignoreContentType(true).get();

		String strSite = document.html().replace("\n", "");

		List<SplitMergeInfo> groupList = getSplitBase(strSite);

		List<String> aRatioList = getARatioList(strSite);
		List<String> bRatioList = getBRatioList(strSite);

		int aRatioCount = aRatioList.size();
		int bRatioCount = bRatioList.size();
		int ratioCount = aRatioCount < bRatioCount ? aRatioCount : bRatioCount;

		int count = groupList.size() > ratioCount ? ratioCount : groupList.size();

		int i;
		for (i = 0; i < count; i++) {
			SplitMergeInfo info = groupList.get(i);
			String aRatio = aRatioList.get(i);
			String bRatio = bRatioList.get(i);
			info.setIntWariateRate1(bRatio);
			info.setIntWariateRate2(aRatio);
		}

		for (; i < groupList.size(); i++) {
			groupList.remove(i);
		}

		return groupList;
	}

	private List<SplitMergeInfo> getSplitBase(String strSite) throws NoDataException {

		List<SplitMergeInfo> groupList = new ArrayList<>();
		SplitMergeInfo info = new SplitMergeInfo();
		boolean halfFlag = true;

		String regex = "document.write\\(\\'.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?\\'\\);";

		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(strSite);
		while (m.find()){

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

		return groupList;
	}

	private List<String> getARatioList(String strSite) {

		List<String> intList = new ArrayList<>();

		String regex = "ARatioW\\s=\\s&quot;(.*?)&quot;;";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(strSite);
		while (m.find()){
			intList.add(m.group(1));
		}

		return intList;

	}

	private List<String> getBRatioList(String strSite) {

		List<String> intList = new ArrayList<>();

		String regex = "BRatioW\\s=\\s&quot;(.*?)&quot;;";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(strSite);
		while (m.find()){
			intList.add(m.group(1));
		}

		return intList;
	}

	boolean checkData (SplitMergeInfo info) {

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
			Date formatDate1 = null;
			Date formatDate2 = null;
			//割当日
			if (info.getIsSplit()==1) formatDate = sdf.parse(info.strWariateDate);
			//権利付き最終日
			formatDate1 = sdf.parse(info.strLastDate);
			//効力発生日
			formatDate2 = sdf.parse(info.strStartDate);
			//売却可能予定日
			if (info.getIsSplit()==1) formatDate = sdf.parse(info.strSalableDate);
			//銘柄コード
			Integer.parseInt(info.strStockCode);
			//割当比率1
			Double.parseDouble(info.strWariateRate1);
			//割当比率2
			Double.parseDouble(info.strWariateRate2);

			//効力発生日は権利付き最終日より必ずあとになるはず
			if (formatDate1.after(formatDate2)){
				return false;
			}
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

		Document document = Jsoup.connect("http://kabu.com/process/LastUpdate.js").ignoreContentType(true).get();

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
