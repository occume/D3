package org.d3.gateway;


import java.util.Arrays;

import org.d3.gateway.bootstrap.Bootstrap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Launcher {

	public static void main(String[] args) {
		
		ApplicationContext context = new AnnotationConfigApplicationContext(D3SpringConfig.class);
//		Arrayi
//		System.out.println(context.getBeanDefinitionNames());
//		for(String name: context.getBeanDefinitionNames()){
//			System.out.println(name);
//		}
		Bootstrap b = (Bootstrap) context.getBean("bootstrap");
		b.lanucher();
		
	}

}

