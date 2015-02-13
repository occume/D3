package org.d3.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.d3.context.SpringConfig;
import org.d3.mybatis.domain.PublicAccount;
import org.d3.mybatis.service.AccountService;
import org.d3.util.CookiedHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.base.Strings;

public class WeiXinTest {
	
	private static Set<String> accounts = new HashSet<>();
	static ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
	static AccountService accountService =  (AccountService) context.getBean("accountService");

	public static void main(String...strings) throws UnsupportedEncodingException, InterruptedException{
		
		int loop = 1000;
		for(int i = 0; i < loop; i++){
			query(i);
			Thread.sleep(5000);
		}
//		ArrayList<E>
		System.out.println("total: " + CookiedHttpClient.getTotalCount());
		for(String acc: accounts){
			System.out.println(acc);
		}
	}
	
	public static void query(int page) throws InterruptedException{
		
		String url = "http://weixin.sogou.com/weixin?"
        		+ "type=2&"
        		+ "query=兰州&"
        		+ "ie=utf8&"
        		+ "_ast=1420441472&"
        		+ "_asf=null&w=01019900&"
        		+ "p=40040100&dp=1&"
        		+ "cid=null&"
        		+ "sut=5372&"
        		+ "sst0=1420441488584&"
        		+ "page="+ page +"&"
        		+ "lkt=0%2C0%2C0";

//      String content = HttpClientUtil.doGet(url);
//		url = "http://weixin.sogou.com/weixin?type=1&query=%E5%85%B0%E5%B7%9E&fr=sgsearch&ie=utf8&_ast=1421217024&_asf=null&w=01029901&cid=null";
        String content = null;
		content = CookiedHttpClient.doGet(url);
        
//        CookieSpec spec = CookiePolicy.ACCEPT_ORIGINAL_SERVER.
//        System.out.println(url);
        if(Strings.isNullOrEmpty(content)) return;
        
        Document doc = Jsoup.parse(content);
//        System.out.println(content);
        Elements elems = doc.select("a[id=weixin_account]");
        
        int size = elems.size();
        for(int i = 0; i < size; i++){
        	try{
        	Element elem = elems.get(i);
        	String account = elem.attr("title");
        	String href = elem.attr("href");
        	if(!Strings.isNullOrEmpty(href)){
        		href = "http://weixin.sogou.com" + href;
        		content = CookiedHttpClient.doGet(href);
        		doc = Jsoup.parse(content);
        		Element info = doc.getElementById("sogou_vr__box_0");
        		Elements txtBoxs = info.getElementsByClass("txt-box");
        		Element txtBox = txtBoxs.first();
        		
        		Element imgBox = info.getElementsByClass("img-box").first();
        		String img = imgBox.select("a img").first().attr("src");
        		
        		String cnName = txtBox.getElementById("weixinname").text();
//        		System.out.println(cnName);
        		String enName = txtBox.getElementsByTag("h4").first().getElementsByTag("span").first().text();
        		if(!Strings.isNullOrEmpty(enName)){
        			enName = enName.substring(4);
        		}
//        		System.out.println(enName);
        		Elements sp2s = txtBox.getElementsByClass("s-p2");
        		Element fun = sp2s.first();
        		String intro = fun.getElementsByClass("sp-txt").first().text();
        		
        		Element vBox = info.getElementsByClass("v-box").first();
        		String qrcode = vBox.child(0).attr("src");
        		
        		PublicAccount acc = new PublicAccount();
        		acc.setCnName(cnName);
        		acc.setEnName(enName);
        		acc.setIntro(intro);
        		acc.setUrl(href);
        		acc.setImg(img);
        		acc.setQrcode(qrcode);
        		System.out.println(acc);
        		
//        		AccountService as = (AccountService) D3Context.getBean("accountService");
        		if(!accountService.exist(enName))
        			accountService.addAccount(acc);
        		
        		Thread.sleep(5000);
        	}
//        	System.out.println(href);
        	accounts.add(account);
        	}catch(Throwable e){
        		CookiedHttpClient.changeUserAgent();
        		System.err.println(e.getMessage());
        	}
        }
	}
}
