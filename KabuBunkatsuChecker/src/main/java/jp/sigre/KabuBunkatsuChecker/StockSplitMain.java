package jp.sigre.KabuBunkatsuChecker;

import jp.sigre.KabuBunkatsuChecker.downloadinfo.parse.ParseHtmlStock;

import org.jsoup.nodes.Document;

public class StockSplitMain {

	Document document;
	String url = "http://kabu.com/investment/meigara/bunkatu.html";

	public StockSplitMain() {

	}

	public void execute() {

		ParseHtmlStock parseStock = new ParseHtmlStock(url);

		parseStock.printHtml();
	}

}
