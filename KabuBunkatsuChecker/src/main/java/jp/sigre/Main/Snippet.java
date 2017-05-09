package jp.sigre.Main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Snippet {
	public static void main(String[] args) {

		String str = "2009year";
		String regex = "(\\d+)(y)";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(str);
		if (m.find()){
		  String matchstr = m.group();
		  System.out.println(matchstr + "の部分にマッチしました");

		  System.out.println("group1:" + m.group(1));
		  System.out.println("group2:" + m.group(2));
		}
		else System.out.println("なし");
	}

}

