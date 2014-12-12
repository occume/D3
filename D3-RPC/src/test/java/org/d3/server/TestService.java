package org.d3.server;

import java.util.Collections;
import java.util.List;

import org.d3.client.Test;

public class TestService implements Test {

	@Override
	public void test() {
		System.out.println("TestService on client");
	}

	@Override
	public String testString() {
		return "server time: " + System.currentTimeMillis();
	}

	@Override
	public List<String> order(List<String> list) {
		Collections.sort(list);
		System.out.println("after server sorted: " + list);
		return list;
	}
	
}
