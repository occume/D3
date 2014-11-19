package org.d3.client;

import java.net.InetSocketAddress;

import org.d3.rpc.manage.World;
import org.d3.rpc.net.client.NettyClient;
import org.d3.rpc.net.proxy.Proxies;

public class NettyClientTest {

	public static void main(String[] args) throws InterruptedException {
		
		NettyClient client = new NettyClient();
//		client.connect(new InetSocketAddress("127.0.0.1", 8082), new InetSocketAddress("127.0.0.1", 8083));
		client.connect(new InetSocketAddress("127.0.0.1", 10086));
		
		Thread.sleep(1000);
		System.out.println(World.defaultChannelGroup().size());
		System.out.println(World.defaultChannelGroup().next());
		
		Test test = Proxies.getProxy(Test.class);
		String result = test.testString();
		System.out.println("client result: " + result);
	}

}
