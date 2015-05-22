package test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.d3.http.Http;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;

public class ResultContrast {
	
	private static final String BASE_PATH = "D:/Users/d_Jin/Downloads/engine.access.log.2015-";
	private static final int THREAD_NUM = 20;
	private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);
	private static CountDownLatch waitMe;
	private static int totalRequest;
	private static int diverseRequest;

	public static void main(String[] args) throws IOException {
		String[] dates = {
				"04-25",
				"04-26",
				"04-27",
				"04-28",
				"04-29",
				"05-20",
				};
		
		for(String date: dates){
			contrast(date);
		}
		
		System.out.println("total request: " + totalRequest);
		System.out.println("diverse request: " + diverseRequest);
	}

	public static void contrast(String date) throws IOException{
		waitMe = new CountDownLatch(THREAD_NUM);
		parseLog(date);
	}
	
	public static void parseLog(String dateString) throws IOException{
		Path path = Paths.get(BASE_PATH + dateString);
		List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
		
		System.out.println("All request: " + lines.size());
		
		Collection<String> transformed = Collections2.transform(lines, new Function<String, String>() {
			@Override
			public String apply(String input) {
				return Splitter.on(CharMatcher.BREAKING_WHITESPACE).splitToList(input).get(12);
			}
		});
		
		Collection<String> validLines = Collections2.filter(transformed, new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.contains("/markland/search");
			}
		});
		
		List<String> validList = new ArrayList<>(validLines);
		
		int total = validLines.size();
		totalRequest += total;
		int pace = total / THREAD_NUM;

		for(int i = 0; i < THREAD_NUM; i++){
			int start = i * pace;
//			int end = (i + 1) * pace;
			executor.submit(new ContrastTask(validList.subList(start, start + 1000)));
		}
		
		System.out.println("valid request: " + total);
		try {
			waitMe.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(dateString + " finished...");
	}
	
	private static class ContrastTask implements Runnable{
		
		private List<String> urls;
		
		public ContrastTask(List<String> urls){
			this.urls = urls;
		}

		@Override
		public void run(){
			for(String uri: urls){
				doContrast(uri);
			}
			System.out.println(Thread.currentThread().getName() + " complete " + urls.size());
			waitMe.countDown();
		}
		
	}
	
	public static void doContrast(String queryString){
		try {
			String result0 = Http.doGet("http://10.2.24.152:8070" + queryString);
			Document doc0 = Jsoup.parse(result0);
			
			String count0 = doc0.getElementsByTag("total").first().text();
			
			String result1 = Http.doGet("http://10.2.25.26:8070" + queryString);
			Document doc1 = Jsoup.parse(result1);
			String count1 = doc1.getElementsByTag("total").first().text();
			
			if(!count0.equals(count1)){
				System.err.println(queryString);
			}
			
			if(Integer.valueOf(count0) > 0){
				String r0 = doc0.getElementsByTag("result").first().text();
				String r1 = doc1.getElementsByTag("result").first().text();
				if(!r0.equals(r1)){
					System.out.println(queryString);
					System.out.println(r0);
					System.out.println(r1);
					System.out.println();
					diverseRequest++;
				}
				else{
//					System.out.println("ok...");
				}
			}
			
		} catch (Exception e) {
		}
	}
}
