package org.d3.wx.collector;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.d3.context.D3Context;
import org.d3.mybatis.domain.Article;
import org.d3.mybatis.domain.PublicAccount;
import org.d3.mybatis.service.AccountService;
import org.d3.util.CookiedHttpClient;
import org.d3.util.HttpClientUtil;
import org.d3.wx.writer.Storage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Strings;

public class ZhihuCollector implements Runnable, Collector{
	
	public static final int MAX_PAGE_COUNT = 10000;
	
	private Storage storage;
	
	private String name;
	
	private String content;
	
	public ZhihuCollector(){
		this.name = "ZHIHU-COLLECTOR";
	}

	public void execute(){
		Thread t = new Thread(this, name);
		t.start();
	}
	
	@Override
	public void run(){
		for(;;){
			collect();
		}
	}

	public void collect(){
		request();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void request(){
		String url = "http://www.zhihu.com/topic/19638453/questions";
		System.out.println(url);
		content = HttpClientUtil.doGet(url);
//		System.out.println(content);
		parseList();
	}
	public void parseList(){
		try{
			Document doc = Jsoup.parse(content);
			Element list = doc.getElementById("zh-topic-questions-list");
			Elements subs = list.children();
			Iterator<Element> it = subs.iterator();
			while(it.hasNext()){
				Element elem = it.next();
				Element ele = elem.getElementsByTag("h2").first().child(1);
				String title = ele.text();
				String href = ele.attr("href");
//				String result = HttpClientUtil.doGet("http://www.zhihu.com" + href);
//				Document doc1 = Jsoup.parse(result);
				Article article = new Article();
				article.setCommunity("ZHIHU");
				article.setGzh("zhihu");
				
				article.setDocid(href.substring(10));
				article.setTitle1(title);
				article.setUrl(href);
				
				storage.writeArticle(article);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private boolean isToday(long time){
		Calendar clr = Calendar.getInstance();
		Calendar that = Calendar.getInstance();
		that.setTimeInMillis(time);
        return clr.get(Calendar.YEAR) == that.get(Calendar.YEAR)
                && clr.get(Calendar.MONTH) == that.get(Calendar.MONTH)
                && clr.get(Calendar.DAY_OF_MONTH) == that.get(Calendar.DAY_OF_MONTH); 
	}
	
	private String today(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		return sdf.format(calendar.getTime());
	}
	
	private void fillArticle(String filed, Object fieldValue, Article article){
		Method[] methods = Article.class.getMethods();
		for(Method m: methods){
			if(m.getName().startsWith("set") && 
					m.getName().toLowerCase().contains(filed)){
				try {
					m.invoke(article, fieldValue);
				} catch (Exception e) {
					System.out.println(m.getName());
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	public void storage(Storage storage){
		this.storage = storage;
	}
	
	public String status(){
		return toString();
	}
	
	@Override
	public String toString() {
		return "ARTICLE-Collector [name=" + name + "]";
	}

	public static void main(String...strings){
//		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
		new ZhihuCollector().run();
	}
}
