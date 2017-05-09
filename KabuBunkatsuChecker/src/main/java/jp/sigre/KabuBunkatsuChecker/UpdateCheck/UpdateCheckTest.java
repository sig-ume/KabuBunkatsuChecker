package jp.sigre.KabuBunkatsuChecker.UpdateCheck;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.sigre.KabuBunkatsuChecker.downloadinfo.parse.SplitMergeInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import au.com.bytecode.opencsv.CSVWriter;


public class UpdateCheckTest {

	public UpdateCheckTest() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void getUpdateData () {

	}

	public void makeCsvFile (String outputFilePath, List<SplitMergeInfo> infoList) {
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
						//TODO x.0をxに変換
						removePointZero(String.valueOf(Double.parseDouble(tt.getStrWariateRate2()))),
						tt.getStrLastDate(),
						"0",
						"0",
						""
				};

				writer.writeNext(entries);

			}
			writer.close();

		}catch (Exception e) {
			e.printStackTrace();
		}finally{

			try{
				iowriter.close();
			}catch(Exception e){
				e.printStackTrace();
			}

		}

	}

	public String removePointZero(String str) {
		if (str.endsWith(".0")) return str.replace(".0", "");

		return str;
	}

	public List<SplitMergeInfo> getDocumentWrite() {
		Document document = null;
		String url = "http://kabu.com/process/bunkatu.js";
		List<SplitMergeInfo> groupList = new ArrayList<>();
		SplitMergeInfo info = new SplitMergeInfo();
		boolean halfFlag = true;

		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//System.out.println(document.html());

		String strSite = document.html().replace("\n", "");

		String regex = "document.write\\(\\'.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>.*?\\'\\);";

		System.out.println(strSite);
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

		regex = "BRatioW\\s=\\s&quot;(.*?)&quot;;";
		p = Pattern.compile(regex);

		System.out.println(regex);
		int count = 0;

		m = p.matcher(strSite);
		while (m.find()){
			groupList.get(count).setIntWariateRate1(m.group(1));
			count += 1;
		}


		regex = "ARatioW\\s=\\s&quot;(.*?)&quot;;";
		p = Pattern.compile(regex);

		System.out.println(regex);
		count = 0;

		m = p.matcher(strSite);
		while (m.find()){
			groupList.get(count).setIntWariateRate2(m.group(1));
			count += 1;
		}

		for (SplitMergeInfo group : groupList) {
			System.out.println(group.toString());
		}

		return groupList;
	}

	public Date getUpdateDate (String dateType) {
		String strSite = getLastUpdateSite();
		String strDate = "";

		String regex = dateType + "\\s=\\s&quot;(\\d+/\\d+/\\d+)&quot";

		System.out.println(strSite);
		System.out.println(regex);
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(strSite);
		if (m.find()){
			strDate = m.group(1);
			System.out.println("date: "+strDate);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		// Date型変換
		Date formatDate = null;
		try {
			formatDate = sdf.parse(strDate);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return formatDate;
	}

	public String getLastUpdateSite () {
		Document document = null;
		String url = "http://kabu.com/process/LastUpdate.js";

		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//System.out.println(document.html());

		return document.html();

	}
}
