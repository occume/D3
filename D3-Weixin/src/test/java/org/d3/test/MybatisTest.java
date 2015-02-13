package org.d3.test;


import java.util.Random;

import org.d3.context.SpringConfig;
import org.d3.mybatis.domain.Article;
import org.d3.mybatis.service.AccountService;
import org.d3.mybatis.service.ArticleService;
import org.d3.wx.collector.Collector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MybatisTest {

	public static void main(String[] args) {
		
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
		System.out.println(context);
		
//		AccountService accountService = context.getBean(AccountService.class);
//		System.out.println(accountService.getAllAccounts().get(0));
		ArticleService articleService = context.getBean(ArticleService.class);
		boolean have = articleService.exist("123");
		System.out.println(have);
	}

}
