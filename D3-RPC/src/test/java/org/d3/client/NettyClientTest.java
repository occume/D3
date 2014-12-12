package org.d3.client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.d3.rpc.manage.World;
import org.d3.rpc.net.node.client.SimpleClient;
import org.d3.rpc.net.proxy.Proxies;
import org.d3.server.TestService;

import com.google.common.collect.Lists;

public class NettyClientTest {

	public static void main(String[] args) throws InterruptedException {
		
		SimpleClient client = new SimpleClient();
//		client.connect(new InetSocketAddress("127.0.0.1", 8082), new InetSocketAddress("127.0.0.1", 8083));
		client.connect(new InetSocketAddress("127.0.0.1", 28256));
		client.registerService(new TestService());
		
//		Thread.sleep(1000);
//		System.out.println(World.defaultChannelGroup().size());
//		System.out.println(World.defaultChannelGroup().next());
//		
		Test test = Proxies.getProxy(Test.class);
////		String result = test.testString();
////		System.out.println("client result: " + result);
		List<String> list = Lists.newArrayList("b", "c", "a");
		List<String> afterSorted = test.order(list);
//		afterSorted = test.order(list);
//		afterSorted = test.order(list);
		System.out.println(afterSorted);
	}

}
