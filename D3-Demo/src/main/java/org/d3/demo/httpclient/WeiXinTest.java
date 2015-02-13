package org.d3.demo.httpclient;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WeiXinTest {
	
	private static Set<String> accounts = new HashSet<>();

	public static void main(String...strings) throws UnsupportedEncodingException, InterruptedException{
		
		int loop = 1;
		for(int i = 0; i < loop; i++){
			query(i);
			Thread.sleep(5000);
		}
		
		System.out.println("total: " + accounts.size());
		for(String acc: accounts){
			System.out.println(acc);
		}
	}
	
	public static void query(int page){
		
		String url = "http://weixin.sogou.com/weixin?"
        		+ "type=2&"
        		+ "query=%E5%85%B0%E5%B7%9E&"
        		+ "ie=utf8&"
        		+ "_ast=1420441472&"
        		+ "_asf=null&w=01019900&"
        		+ "p=40040100&dp=1&"
        		+ "cid=null&"
        		+ "sut=5372&"
        		+ "sst0=1420441488584&"
        		+ "page="+ page +"&"
        		+ "lkt=0%2C0%2C0";
		url = "http://weixin.sogou.com/weixin?type=2&query=%E5%85%B0%E5%B7%9E&fr=sgsearch&ie=utf8&_ast=1421141632&_asf=null&w=01029901&p=40040100&dp=1&cid=null";

        String content = HttpClientUtil.doGet(url);
        System.out.println(content);
        content = HttpClientUtil.doGet(url);
//        CookieSpec spec = CookiePolicy.ACCEPT_ORIGINAL_SERVER.
//        System.out.println(url);
        Document doc = Jsoup.parse(content);
//        System.out.println(content);
        Elements elems = doc.select("a[id=weixin_account]");
        
        int size = elems.size();
        for(int i = 0; i < size; i++){
//        	System.out.println(elems.get(i).attr("title"));
        	String account = elems.get(i).attr("title");
//        	System.out.println(account);
        	accounts.add(account);
        }
	}
}
