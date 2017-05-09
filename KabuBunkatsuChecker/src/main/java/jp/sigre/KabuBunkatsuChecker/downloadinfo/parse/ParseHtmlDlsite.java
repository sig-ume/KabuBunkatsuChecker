package jp.sigre.KabuBunkatsuChecker.downloadinfo.parse;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ParseHtmlDlsite implements ParseHtmlProduct{

	Document document;

    public ParseHtmlDlsite(String url){
    	try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

	public String getProductName() {
		Element element1 = document.getElementById("work_name");
        String workName = element1.getElementsByTag("h1").get(0).text();

        return workName;
	}

	public String getProductMaker() {
		Element element2 = document.getElementsByClass("maker_name").get(0);
        String workMaker = element2.getElementsByTag("span").get(0).text();

        return workMaker;
	}

	public int getProductRelease() {
		Element element3 = document.getElementById("work_outline");
        String workRelease = element3.getElementsByTag("td").get(0).text();

        return getDateInt(workRelease);
	}

	private int getDateInt(String workRelease) {
		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = null;
		try {
			date = df.parse(workRelease);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(date);
        int dateInt = Integer.parseInt(dateStr);

        return dateInt;
	}

}
