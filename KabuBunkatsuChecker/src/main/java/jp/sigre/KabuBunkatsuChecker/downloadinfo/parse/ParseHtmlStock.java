package jp.sigre.KabuBunkatsuChecker.downloadinfo.parse;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ParseHtmlStock {

	//割当日
	String strWariateDate = "";
	//銘柄コード
	String strStockCode = "";
	//銘柄名
	String strStockName = "";
	//割当比率1
	int intWariateRate1 = 0;
	//割当比率2
	int intWariateRate2 = 0;
	//権利付き最終日
	String strLastDate = "";
	//効力発生日
	String strStartDate = "";
	//売却可能予定日
	String strSalableDate = "";
	//分割：True、併合：False
	Boolean isSplit = null;
	//分割併合データ

	//更新日付
	String strUpdatedate = "";

	Document document;

    public ParseHtmlStock(String url){
    	try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

	/**
	 * @return strWariateDate
	 */
	public String getStrWariateDate() {
		return strWariateDate;
	}

	/**
	 * @return strStockCode
	 */
	public String getStrStockCode() {
		return strStockCode;
	}

	/**
	 * @return strStockName
	 */
	public String getStrStockName() {
		return strStockName;
	}

	/**
	 * @return intWariateRate1
	 */
	public int getIntWariateRate1() {
		return intWariateRate1;
	}

	/**
	 * @return intWariateRate2
	 */
	public int getIntWariateRate2() {
		return intWariateRate2;
	}

	/**
	 * @return strLastDate
	 */
	public String getStrLastDate() {
		return strLastDate;
	}

	/**
	 * @return strStartDate
	 */
	public String getStrStartDate() {
		return strStartDate;
	}

	/**
	 * @return strSalableDate
	 */
	public String getStrSalableDate() {
		return strSalableDate;
	}

	/**
	 * @return isSplit
	 */
	public Boolean getIsSplit() {
		return isSplit;
	}

	/**
	 * @return document
	 */
	public Document getDocument() {
		return document;
	}

	public void printHtml() {
		System.out.println(document.html());
	}
}
