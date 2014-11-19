package org.d3.demo.proxy.static_;

public class CountProxy implements Count {
	
	private Count impl;

	public CountProxy(Count real){
		impl = real;
	}
	
	@Override
	public int total() {
		System.out.println("before real total()");
		int total = impl.total();
		System.out.println("after real total()");
		return total;
	}

}
