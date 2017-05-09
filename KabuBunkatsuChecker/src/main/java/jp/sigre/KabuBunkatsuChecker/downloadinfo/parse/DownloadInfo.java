/**
 *
 */
package jp.sigre.KabuBunkatsuChecker.downloadinfo.parse;

import jp.sigre.KabuBunkatsuChecker.ProductInfoBean;

/**
 * @author sigre
 *
 */
public class DownloadInfo {

	public ProductInfoBean get(String url) {
		ParseHtmlProduct parser = null;

		int siteNum = checkWebSite(url);

		switch (siteNum) {
		case 1:
			parser = new ParseHtmlAmazon(url);
			break;
		case 2:
			parser = new ParseHtmlDlsite(url);
			break;
		default:
			return null;
		}

		String title = parser.getProductName();
		String maker = parser.getProductMaker();
		int    rDate = parser.getProductRelease();

		System.out.println("aaa"+title);

		return new ProductInfoBean(-1, title, maker, url, rDate, 0);
	}

	private int checkWebSite(String url) {
		String amazon = "amazon";
		String dlsite = "dlsite";
		//String dmm    = "dmm";
		if (url.matches(".*" + amazon + ".*")) {
			return 1;
		}
		if (url.matches(".*" + dlsite + ".*")) {
			return 2;
		}
		return 0;
	}
}
