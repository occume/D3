package org.d3.demo.proxy.dynamic.reflection;

import java.util.Random;

public class BookFacadeImpl implements BookFacade {

	final Random random = new Random();
	
	@Override
	public void addBook() {
		System.out.println("addBood();");
	}

	@Override
	public int getBookCount() {
		return random.nextInt(100);
	}

}
