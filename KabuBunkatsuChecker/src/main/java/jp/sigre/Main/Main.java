/**
 *
 */
package jp.sigre.Main;

import java.io.File;
import java.text.ParseException;

import jp.sigre.KabuBunkatsuChecker.UpdateCheck.UpdateCheckTest;
import jp.sigre.KabuBunkatsuChecker.downloadinfo.parse.ParseHtmlStockSplit;

/**
 * @author sigre
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {

		//フォルダパス
		String strPath = "C:\\pleiades\\workspace\\KabuBunkatsuChecker\\target\\2017-00-00.csv";
		new File(strPath).delete();
		strPath = "C:\\pleiades\\workspace\\KabuBunkatsuChecker\\target\\2017-99-99.csv";
		new File(strPath).delete();

		// TODO 自動生成されたメソッド・スタブ
		UpdateCheckTest updateCheck = new UpdateCheckTest();

		//System.out.println(updateCheck.getUpdateDate("KoushinDate_Bunkatsu"));

		//System.out.println(updateCheck.getDocumentWrite());

		//updateCheck.makeCsvFile(strPath, updateCheck.getDocumentWrite());



		//new ParseHtmlStockSplit().makeSplitCsv("C:\\pleiades\\workspace\\KabuBunkatsuChecker\\target", "2017-99-99");

		new ParseHtmlStockSplit().makeMergeCsv("C:\\pleiades\\workspace\\KabuBunkatsuChecker\\target", "2017-00-00");

	}

}
