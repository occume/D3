package org.d3.demo.proxy.static_;

public class CountMain {

	public static void main(String[] args) {
		Count count = new CountProxy(new CountImpl());
		int total = count.total();
		System.out.println(total);
	}

}
