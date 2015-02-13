package org.d3.context;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class D3Context implements ApplicationContextAware{
	
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		D3Context.context = applicationContext;
	}

	private static ApplicationContext context;
	
	public static Object getBean(String beanName){
		if (null == beanName){
			return null;
		}
		return context.getBean(beanName);
	}
	
	@PostConstruct
	public void onStart() {

	}
	
	private static D3Context world = new D3Context();
	
	private static final Logger LOG = LoggerFactory.getLogger(D3Context.class);
	
	private D3Context(){}
	
	public static D3Context instance(){
		return world;
	}
	
}
