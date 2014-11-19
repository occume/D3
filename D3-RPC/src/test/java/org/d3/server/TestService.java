package org.d3.server;

import org.d3.client.Test;

public class TestService implements Test {

	@Override
	public void test() {
		System.out.println("TestService on server");
	}

	@Override
	public String testString() {
		return "server time: " + System.currentTimeMillis();
	}

}
