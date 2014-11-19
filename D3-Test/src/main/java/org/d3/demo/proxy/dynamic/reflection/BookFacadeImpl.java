package org.d3.demo.proxy.dynamic.reflection;

public class BookFacadeImpl implements BookFacade {

	@Override
	public void addBook() {
		System.out.println("addBood();");
	}

	@Override
	public int getBookCount() {
		return 0;
	}

}
