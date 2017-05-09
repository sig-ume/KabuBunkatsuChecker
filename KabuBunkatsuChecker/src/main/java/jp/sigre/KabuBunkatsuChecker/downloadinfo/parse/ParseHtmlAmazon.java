package jp.sigre.KabuBunkatsuChecker.downloadinfo.parse;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ParseHtmlAmazon implements ParseHtmlProduct{

	Document document;

	public ParseHtmlAmazon(String url) {
		try {
			document = Jsoup.connect(url).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public String getProductName() {
		Element element1 = document.getElementById("productTitle");
		if (element1 == null) {
			//Kindle版
			element1 = document.getElementById("btAsinTitle");
		}
		String workName = element1.text();

		return workName;
	}

	public String getProductMaker() {
		Element element2 = document.getElementById("brand");
		String workMaker;
		if (element2 == null) {
			//Kindle版
			workMaker = "";
		} else {
			workMaker = element2.text();
		}

		return workMaker;
	}

	public int getProductRelease() {
		Element element3 = document.getElementById("detail_bullets_id");
		String workRelease = element3.getElementsByTag("li").get(4).text();

		return getDateInt(workRelease);
	}

	private int getDateInt(String workRelease) {
		DateFormat df = new SimpleDateFormat("発売日： yyyy/MM/dd");
		Date date = null;
		try {
			date = df.parse(workRelease);
		} catch (ParseException e) {
			date = new Date();
		}
		String dateStr = new SimpleDateFormat("yyyyMMdd").format(date);
		int dateInt = Integer.parseInt(dateStr);

		return dateInt;
	}
}
