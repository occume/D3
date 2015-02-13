package org.d3.wx.collector;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.d3.context.D3Context;
import org.d3.mybatis.domain.Article;
import org.d3.mybatis.domain.PublicAccount;
import org.d3.mybatis.service.AccountService;
import org.d3.util.CookiedHttpClient;
import org.d3.wx.writer.Storage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Strings;

public class ArticleCollector implements Runnable, Collector{
	
	private static enum Step{
		REQUEST, PARSE_LIST, PARSE_DETAIL, STORAGE, BREAK
	}
	
	public static final int MAX_PAGE_COUNT = 10000;
	
	private Storage storage;
	
	private String name;
	
	private Step currStep = Step.REQUEST;
	
	private String content;
	
	public ArticleCollector(){
		this.name = "ARTICLE-COLLECTOR";
	}

	public void execute(){
		Thread t = new Thread(this, name);
		
		UncaughtExceptionHandler eh = new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println(Thread.currentThread().getName());
				e.printStackTrace();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				execute();
			}
		};
		t.setUncaughtExceptionHandler(eh);
		t.start();
//		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//		executor.scheduleAtFixedRate(this, 0, 3600, TimeUnit.SECONDS);
	}
	
	@Override
	public void run(){
		for(;;){
			AccountService accountService = (AccountService) D3Context.getBean("accountService");
			List<PublicAccount> accounts = accountService.getAllAccounts();
			if(accounts == null || accounts.size() == 0) return;
			
			for(PublicAccount account: accounts){
				collect(account);
//				break;
			}
		}
	}

	public void collect(PublicAccount account){
		switch(currStep){
			case REQUEST:
				request(account);
			case PARSE_LIST:
				parseList(account);
			case BREAK:
			default:
				break;
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void request(PublicAccount account){
		String url = account.getUrl();
		url = url.replace("/gzh", "/gzhjs");
		System.out.println(url);
		content = CookiedHttpClient.doArticleGet(url);
		currStep = Step.PARSE_LIST;
        if(Strings.isNullOrEmpty(content)){
        	currStep = Step.BREAK;
        }
	}
	public void parseList(PublicAccount account){
		System.out.println(content);
		content = content.substring(5, content.indexOf("})") + 1);
		
		try {
			JSONObject jobj = new JSONObject(content);
			JSONArray items = jobj.getJSONArray("items");
			
			int len = items.length();
			for(int i = 0; i < len; i++){
				String item = items.getString(i);
				Document doc = Jsoup.parse(item);
				Elements elems = doc.getElementsByTag("display");
				Element display = elems.first();
				
				String[] fields = {"docid", "title1", "url", "imglink", "sourcename", 
						"openid", "content", "date"};
				
				Article article = new Article();
				
				for(String filed: fields){
					Elements fieldElment = display.getElementsByTag(filed);
					String fieldValue = fieldElment.first().text();
					fillArticle(filed, fieldValue, article);
				}

				Elements epageSize = display.getElementsByTag("pagesize");
				String pageSize = epageSize.first().text();
				Elements elastModified = display.getElementsByTag("lastmodified");
				String lastModified = elastModified.first().text();
				long lm = Long.valueOf(lastModified + "000");
				
				article.setPagesize(Integer.valueOf(pageSize.substring(0, pageSize.length() - 1)));
				article.setLastmodified(lm);
//				System.out.println(article);
				if(isToday(lm)){
					article.setGzh(account.getEnName());
					storage.writeArticle(article);
				}
			}
		} catch (JSONException e) {
			currStep = Step.BREAK;
			e.printStackTrace();
			return;
		}
        currStep = Step.REQUEST;
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
//		new ArticleCollector().run();
		Date date = new Date(1422934429000l);
		System.out.println(date);
	}
}
