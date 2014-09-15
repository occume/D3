package org.d3.gateway;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.d3.gateway.bean.ClientState;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class D3Context implements ApplicationContextAware{
	
	private static ApplicationContext applicationContext;
	private ConcurrentHashMap<String, ClientState> nodes = new ConcurrentHashMap<>(16);

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		D3Context.applicationContext = applicationContext;
	}
	
	public static Object getBean(String beanName){
		if (null == beanName){
			return null;
		}
		return applicationContext.getBean(beanName);
	}
	
	//@PostConstruct
	public void start() {
//		initGames();
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
}