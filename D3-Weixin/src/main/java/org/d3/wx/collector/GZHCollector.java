package org.d3.wx.collector;

import org.d3.mybatis.domain.PublicAccount;
import org.d3.util.CookiedHttpClient;
import org.d3.util.URLBuilder;
import org.d3.wx.writer.Storage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Strings;

public class GZHCollector implements Runnable, Collector{
	
	private static enum Step{
		REQUEST, PARSE_LIST, PARSE_DETAIL, STORAGE, BREAK
	}
	
	public static final int MAX_PAGE_COUNT = 10000;
	
	private Storage writer;
	
	private String listUrl;
	
	private String detailUrl;
	
	private String name;
	
	private int type;
	
	private int currPage = 1;
	
	private Step currStep = Step.REQUEST;
	
	private String content;
	
	public GZHCollector(String keyWord, int type){
		this.name = "GZH-COLLECTOR";
		this.type = type;
		this.listUrl = URLBuilder.gzhQueryURL(keyWord, type);
	}
	
	@Override
	public void run(){
		for(;;){
			if(currPage >= MAX_PAGE_COUNT){
				currPage = 1;
			}
			collect(currPage++);
		}
	}

	public void execute(){
		Thread thread = new Thread(this, name);
		thread.start();
	}
	
	public void collect(int page){
		switch(currStep){
			case REQUEST:
				request(page);
			case PARSE_LIST:
				parseList();
			case BREAK:
			default:
				break;
		}
	}
	
	public void request(int page){
		String url = listUrl + "&page=" + page;
		System.out.println(url);
		content = CookiedHttpClient.doGet(url);
		currStep = Step.PARSE_LIST;
//      System.out.println(content);
        if(Strings.isNullOrEmpty(content)){
        	currStep = Step.BREAK;
        }
	}
	public void parseList(){
		Document doc = Jsoup.parse(content);
        
        Elements elems = null;
        if(type == 1){
        	elems = doc.select(".wx-rb_v1");
        }
        else if(type == 2){
        	elems = doc.select("a[id=weixin_account]");
        }
        
        int size = elems.size();
        for(int i = 0; i < size; i++){
        	Element elem = elems.get(i);
        	detailUrl = elem.attr("href");
        	
        	parseDetail();
        }
        currStep = Step.REQUEST;
	}
	
	public void parseDetail(){
		if(Strings.isNullOrEmpty(detailUrl)) return;
		
		String href = "http://weixin.sogou.com" + detailUrl;
		content = CookiedHttpClient.doGet(href);
		
		try{
			Document doc = Jsoup.parse(content);
			Element info = doc.getElementById("sogou_vr__box_0");
			Elements txtBoxs = info.getElementsByClass("txt-box");
			Element txtBox = txtBoxs.first();
			
			Element imgBox = info.getElementsByClass("img-box").first();
			String img = imgBox.select("a img").first().attr("src");
			
			String cnName = txtBox.getElementById("weixinname").text();
			String enName = txtBox.getElementsByTag("h4").first().getElementsByTag("span").first().text();
			if(!Strings.isNullOrEmpty(enName)){
				enName = enName.substring(4);
			}

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
			acc.setType(2);
			System.out.println(acc);
			
			writer.writeAccount(acc);
			Thread.sleep(5000);
		}catch(Exception e){
			CookiedHttpClient.changeUserAgent();
			e.printStackTrace();
		}
	}
	
	public void storage(Storage storage){
		this.writer = storage;
	}
	
	public String status(){
		return toString();
	}
	
	@Override
	public String toString() {
		return "GZHCollector [name=" + name + ", type=" + type + ", currPage="
				+ currPage + "]";
	}

	public static void main(String...strings){
//		new GZHCollector("gzh_worker", url, "").collect(1);
	}
}
