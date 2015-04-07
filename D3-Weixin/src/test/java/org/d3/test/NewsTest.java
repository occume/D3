package org.d3.test;

import org.d3.util.CookiedHttpClient;
import org.d3.util.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NewsTest {

	public static void main(String[] args) {
		String content = HttpClientUtil.doGet("http://news.baidu.com/ns?word=兰州&rn=100");
		
		Document doc = Jsoup.parse(content);
		Element elem = doc.getElementById("content_left");
		
		Elements elems = elem.select(".result");
		System.out.println(elems.size());
	}

}
