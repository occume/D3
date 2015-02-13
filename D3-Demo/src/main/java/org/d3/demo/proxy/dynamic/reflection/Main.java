package org.d3.demo.proxy.dynamic.reflection;

import org.d3.std.Stopwatch;

public class Main {

	public static void main(String[] args) {
		
//		BookFacadeProxy proxy = new BookFacadeProxy();
//		BookFacade book = (BookFacade) proxy.bind(new BookFacadeImpl());
//		BookFacade book = Proxies.getProxy(BookFacade.class);
//		book.addBook();
//		int count = book.getBookCount();
//		System.out.println("total book count: " + count);
//		System.out.println(proxy);
		for(int i = 0; i < 10; i++){
			test();
		}
		
	}
	
	public static void test(){
		
		BookFacade book = Proxies.getProxy(BookFacade.class);
		int loops = 1000000;
		int result = 0;
		Stopwatch sw = Stopwatch.newStopwatch();
		for(int i = 0; i < loops; i++){
			result += book.getBookCount();
		}
		System.out.println("result: " + result);
		System.out.println(sw.longTime());
	}

}
