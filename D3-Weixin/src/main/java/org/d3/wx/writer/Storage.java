package org.d3.wx.writer;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.d3.mybatis.domain.Article;
import org.d3.mybatis.domain.PublicAccount;
import org.d3.mybatis.service.AccountService;
import org.d3.mybatis.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Storage {
	
	private BlockingQueue<PublicAccount> tasks;
	private BlockingQueue<Article> articles;
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private ArticleService articleService;
	
	public Storage(){
		tasks = new LinkedBlockingQueue<>();
		articles = new LinkedBlockingQueue<>();
	}
	
	public void writeAccount(PublicAccount acc){
		try {
			tasks.put(acc);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void writeArticle(Article article){
		try {
			articles.put(article);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void saveGZH(){
		try {
			PublicAccount acc = tasks.take();
			if(!accountService.exist(acc.getEnName()))
				accountService.addAccount(acc);
			else
				System.out.println("repeat key");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void saveArticle(){
		try {
			Article article = articles.take();
			System.out.println(article);
			if(!articleService.exist(article.getDocid())){
				try{
				articleService.addArticle(article);
				}catch(Throwable e){
					e.printStackTrace();
				}
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@PostConstruct
	public void write0(){
		startGZHWorker();
		startArticleWorker();
	}
	
	private void startGZHWorker(){
		Thread t = new Thread(new GZHWorker(), "GZH-DB-WORKER");
		UncaughtExceptionHandler eh = new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Thread.dumpStack();
				startGZHWorker();
			}
		};
		t.setUncaughtExceptionHandler(eh);
		t.setDaemon(true);
		t.start();
	}
	
	private void startArticleWorker(){
		Thread t = new Thread(new ArticleWorker(), "ARTICLE-DB-WORKER");
		UncaughtExceptionHandler eh = new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				startArticleWorker();
			}
		};
		t.setUncaughtExceptionHandler(eh);
		t.setDaemon(true);
		t.start();
	}
	
	private class GZHWorker implements Runnable{
		@Override
		public void run() {
			for(;;){
				saveGZH();
			}
		}
	}
	
	private class ArticleWorker implements Runnable{
		@Override
		public void run() {
			for(;;){
				saveArticle();
			}
		}
	}
}