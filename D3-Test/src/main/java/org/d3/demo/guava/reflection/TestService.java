package org.d3.demo.guava.reflection;

import java.util.Collections;
import java.util.List;

public class TestService implements Test {

	@Override
	public void test() {
		System.out.println("TestService on server");
	}

	@Override
	public String testString() {
		return "server time: " + System.currentTimeMillis();
	}

	@Override
	public List<String> order(List<String> list, String str, Object obj, byte[] bytes) {
		Collections.sort(list);
		return list;
	}
	
}
