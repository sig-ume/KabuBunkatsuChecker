package jp.sigre.KabuBunkatsuChecker.downloadinfo.parse;

public class SplitMergeInfo {

	//割当日
	String strWariateDate = "";
	//銘柄コード
	String strStockCode = "";
	//銘柄名
	String strStockName = "";
	//割当比率1
	String strWariateRate1 = "";
	//割当比率2
	String strWariateRate2 = "";
	//権利付き最終日
	String strLastDate = "";
	//効力発生日
	String strStartDate = "";
	//売却可能予定日
	String strSalableDate = "";
	//分割：True、併合：False
	int isSplit = -1;

	public SplitMergeInfo() {
	}

	/**
	 * 割当日
	 * @return strWariateDate
	 */
	public String getStrWariateDate() {
		return strWariateDate;
	}

	/**
	 * 割当日
	 * @param strWariateDate セットする strWariateDate
	 */
	public void setStrWariateDate(String strWariateDate) {
		this.strWariateDate = strWariateDate;
	}

	/**
	 * 銘柄コード
	 * @return strStockCode
	 */
	public String getStrStockCode() {
		return strStockCode;
	}

	/**
	 * 銘柄コード
	 * @param strStockCode セットする strStockCode
	 */
	public void setStrStockCode(String strStockCode) {
		this.strStockCode = strStockCode;
	}

	/**
	 * 銘柄名
	 * @return strStockName
	 */
	public String getStrStockName() {
		return strStockName;
	}

	/**
	 * 銘柄名
	 * @param strStockName セットする strStockName
	 */
	public void setStrStockName(String strStockName) {
		this.strStockName = strStockName;
	}

	/**
	 * 割当比率1
	 * @return intWariateRate1
	 */
	public String getStrWariateRate1() {
		return strWariateRate1;
	}

	/**
	 * 割当比率1
	 * @param string セットする intWariateRate1
	 */
	public void setIntWariateRate1(String string) {
		this.strWariateRate1 = string;
	}

	/**
	 * 割当比率2
	 * @return intWariateRate2
	 */
	public String getStrWariateRate2() {
		return strWariateRate2;
	}

	/**
	 * 割当比率2
	 * @param intWariateRate2 セットする intWariateRate2
	 */
	public void setIntWariateRate2(String strWariateRate2) {
		this.strWariateRate2 = strWariateRate2;
	}

	/**
	 * 権利付き最終日
	 * @return strLastDate
	 */
	public String getStrLastDate() {
		return strLastDate;
	}

	/**
	 * 権利付き最終日
	 * @param strLastDate セットする strLastDate
	 */
	public void setStrLastDate(String strLastDate) {
		this.strLastDate = strLastDate;
	}

	/**
	 * 効力発生日
	 * @return strStartDate
	 */
	public String getStrStartDate() {
		return strStartDate;
	}

	/**
	 * 効力発生日
	 * @param strStartDate セットする strStartDate
	 */
	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	/**
	 * 売却可能予定日
	 * @return strSalableDate
	 */
	public String getStrSalableDate() {
		return strSalableDate;
	}

	/**
	 * 売却可能予定日
	 * @param strSalableDate セットする strSalableDate
	 */
	public void setStrSalableDate(String strSalableDate) {
		this.strSalableDate = strSalableDate;
	}

	/**
	 * 分割：True、併合：False
	 * @return isSplit
	 */
	public int getIsSplit() {
		return isSplit;
	}

	/**
	 * 分割：True、併合：False
	 * @param isSplit セットする isSplit
	 */
	public void setIsSplit(int isSplit) {
		this.isSplit = isSplit;
	}

	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SplitMergeInfo [strWariateDate=" + strWariateDate
				+ ", strStockCode=" + strStockCode + ", strStockName="
				+ strStockName + ", intWariateRate1=" + strWariateRate1
				+ ", intWariateRate2=" + strWariateRate2 + ", strLastDate="
				+ strLastDate + ", strStartDate=" + strStartDate
				+ ", strSalableDate=" + strSalableDate + ", isSplit=" + isSplit
				+ "]";
	}

}
