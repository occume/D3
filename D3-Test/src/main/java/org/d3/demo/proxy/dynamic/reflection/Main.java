package org.d3.demo.proxy.dynamic.reflection;

public class Main {

	public static void main(String[] args) {
		
//		BookFacadeProxy proxy = new BookFacadeProxy();
//		BookFacade book = (BookFacade) proxy.bind(new BookFacadeImpl());
		BookFacade book = Proxies.getProxy(BookFacade.class);
		book.addBook();
		int count = book.getBookCount();
		System.out.println("total book count: " + count);
//		System.out.println(proxy);
		
	}

}
