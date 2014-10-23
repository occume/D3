package org.d3.spider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class CnblogsProcessor implements PageProcessor{
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

	public static void main(String[] args) {
		Spider.create(new CnblogsProcessor()).addUrl("http://www.cnblogs.com/").thread(5).run();
	}

	@Override
	public void process(Page page) {
		System.out.println(page);
	}

	@Override
	public Site getSite() {
		return site;
	}

}
