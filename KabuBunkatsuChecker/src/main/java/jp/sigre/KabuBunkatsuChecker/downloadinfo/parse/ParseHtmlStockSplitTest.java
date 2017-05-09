package jp.sigre.KabuBunkatsuChecker.downloadinfo.parse;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ParseHtmlStockSplitTest {

	ParseHtmlStockSplit target = new ParseHtmlStockSplit();

	String strFolder = "C:\\pleiades\\workspace\\KabuBunkatsuChecker\\target";
	String strNoWrite = "C:\\pleiades\\workspace\\KabuBunkatsuChecker\\target_no_write";
	String strPath = "C:\\pleiades\\workspace\\KabuBunkatsuChecker\\target\\2017-06-06.csv";
	String strDate = "2017-06-06";
	@Before
	public void setUp() throws Exception {

		new File(strPath).delete();
	}

	@Test
	public void mergetest_正常系1() {

		int result = target.makeMergeCsv(strFolder, strDate);

		assertThat(result, is(0));
	}

	@Test
	public void mergetest_正常系2() {

		int result = target.makeMergeCsv(strFolder + "\\", strDate);

		assertThat(result, is(0));
	}

	@Test
	public void splittest_正常系1() {

		int result = target.makeSplitCsv(strFolder, strDate);

		assertThat(result, is(0));
	}

	@Test
	public void splittest_正常系2() {

		int result = target.makeSplitCsv(strFolder + "\\", strDate);

		assertThat(result, is(0));
	}

	@Test
	public void splittest_異常系1() throws IOException {
		new File(strPath).createNewFile();
		int result = target.makeSplitCsv(strFolder, strDate);

		assertThat(result, is(1));
		assertThat(new File(strPath).length(), is(Long.valueOf("0")));
	}

	@Test
	public void mergetest_異常系1() throws IOException {
		new File(strPath).createNewFile();
		int result = target.makeMergeCsv(strFolder, strDate);

		assertThat(result, is(1));
		assertThat(new File(strPath).length(), is(Long.valueOf("0")));
	}

	@Test
	public void splittest_異常系2() throws IOException {
		int result = target.makeSplitCsv("C:\\nasi", strDate);

		assertThat(result, is(2));
		assertThat(new File("C:\\nasi\\" + strDate + ".csv").exists(), is(false));
	}

	@Test
	public void mergetest_異常系2() throws IOException {
		int result = target.makeMergeCsv("C:\\nasi", strDate);

		assertThat(result, is(2));
		assertThat(new File("C:\\nasi\\" + strDate + ".csv").exists(), is(false));
	}

	@Test
	public void splittest_異常系3() throws IOException {
		int result = target.makeSplitCsv(strNoWrite, strDate);

		assertThat(result, is(3));
		assertThat(new File(strNoWrite + "\\" + strDate + ".csv").exists(), is(false));
	}

	@Test
	public void mergetest_異常系3() throws IOException {
		int result = target.makeMergeCsv(strNoWrite, strDate);

		assertThat(result, is(3));
		assertThat(new File(strNoWrite + "\\" + strDate + ".csv").exists(), is(false));
	}

	@Test
	public void getMergeDatatest_正常系() throws Exception {
		List<SplitMergeInfo> result = target.getMergeData("http://kabu.com/process/gensi.js");

		assertThat(result.size(), is(not(0)));
		for (SplitMergeInfo info : result ) {
			if (!target.checkData(info)) System.out.println(info);
			assertThat(target.checkData(info), is(true));
		}
	}

	@Test
	public void getSplitDatatest_正常系() throws Exception {
		List<SplitMergeInfo> result = target.getSplitData("http://kabu.com/process/bunkatu.js");

		assertThat(result.size(), is(not(0)));
		for (SplitMergeInfo info : result ) {
			assertThat(target.checkData(info), is(true));
		}
	}

	@Test
	public void getMergeDatatest_異常系1() throws Exception {
		List<SplitMergeInfo> result = target.getMergeData("http://kabu.com/process/bunkatu.js");

		assertThat(result.size(), is(not(0)));
		for (SplitMergeInfo info : result ) {
			assertThat(target.checkData(info), is(false));
		}
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void getSplitDatatest_異常系1() throws Exception {
		List<SplitMergeInfo> result = target.getSplitData("http://kabu.com/process/gensi.js");

	}


	@Test(expected = NoDataException.class)
	public void getMergeDatatest_異常系() throws Exception {
		@SuppressWarnings("unused")
		List<SplitMergeInfo> result = target.getMergeData("http://google.com");

	}

	@Test
	public void getUpdateDatetest_正常系1() throws Exception {
		@SuppressWarnings("unused")
		String result = target.getUpdateDate("KoushinDate_Bunkatsu");

		assertThat(result, is("2017/05/02"));

	}

	@Test
	public void getUpdateDatetest_正常系2() throws Exception {
		@SuppressWarnings("unused")
		String result = target.getUpdateDate("KoushinDate_Gappei");

		assertThat(result, is("2017/04/28"));

	}


	@Test(expected=java.text.ParseException.class)
	public void getUpdateDatetest_正常系3() throws Exception {
		@SuppressWarnings("unused")
		String result = target.getUpdateDate("");

	}

	@Test(expected=java.text.ParseException.class)
	public void getUpdateDatetest_正常系4() throws Exception {
		@SuppressWarnings("unused")
		String result = target.getUpdateDate("Test");

	}
}
