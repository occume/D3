package org.d3.bootstrap;

import java.util.Set;

import org.d3.context.D3Context;
import org.d3.context.SpringConfig;
import org.d3.wx.collector.ArticleCollector;
import org.d3.wx.collector.Collector;
import org.d3.wx.collector.GZHCollector;
import org.d3.wx.collector.ZhihuCollector;
import org.d3.wx.writer.Storage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.common.util.concurrent.ServiceManager.Listener;

@Component
public class Bootstrap {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
		Bootstrap launcher = context.getBean(Bootstrap.class);
		launcher.start();
	}

	public void start(){
		Set<Service> services = Sets.newLinkedHashSet();
//		services.add(new GZHCollectorService());
//		services.add(new ArticleCollectorService());
		services.add(new ZhihuCollectorService());
		
		ServiceManager serviceManager = new ServiceManager(services);
		serviceManager.addListener(new StartListener());
		serviceManager.startAsync();
	}
	/**
	 * 公众号 采集服务
	 * @author d_jin
	 *
	 */
	private static class GZHCollectorService extends AbstractService{

		@Override
		protected void doStart() {
			Collector c = new GZHCollector("游戏", 1);
			Storage storage = (Storage) D3Context.getBean("storage");
			c.storage(storage);
			c.execute();
		}

		@Override
		protected void doStop() {
			
		}
		
	}
	/**
	 * 文章 采集服务
	 * @author d_jin
	 *
	 */
	private static class ArticleCollectorService extends AbstractService{

		@Override
		protected void doStart() {
			Collector c = new ArticleCollector();
			Storage storage = (Storage) D3Context.getBean("storage");
			c.storage(storage);
			c.execute();
		}

		@Override
		protected void doStop() {
			
		}
	}
	
	/**
	 * 知乎  问答 采集服务
	 * @author d_jin
	 *
	 */
	private static class ZhihuCollectorService extends AbstractService{

		@Override
		protected void doStart() {
			Collector c = new ZhihuCollector();
			Storage storage = (Storage) D3Context.getBean("storage");
			c.storage(storage);
			c.execute();
		}

		@Override
		protected void doStop() {
			
		}
	}
	
	private static class StartListener extends Listener{
		@Override
		public void stopped() {
			System.out.println("all Service has been stopped");
		}
	}
}
