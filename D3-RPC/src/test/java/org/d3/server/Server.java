package org.d3.server;

import org.d3.client.Test;
import org.d3.rpc.net.node.server.SimpleServer;
import org.d3.rpc.net.proxy.Proxies;

public class Server {

	public static void main(String[] args) throws InterruptedException {
		
		SimpleServer server = new SimpleServer();
		server.start();
		server.registerService(new TestService());
		
		System.out.println(server.getAllServices());
		System.out.println(server.getGroups());
		
		Thread.sleep(10000);
		Test test = Proxies.getProxy(Test.class);
		test.test();
	}

}
