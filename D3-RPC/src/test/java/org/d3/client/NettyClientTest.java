package org.d3.client;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.d3.rpc.manage.World;
import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.channel.D3ChannelGroup;
import org.d3.rpc.net.node.client.SimpleClient;
import org.d3.rpc.net.service.ServiceBuilder;
import org.d3.rpc.util.Invokable;
import org.d3.rpc.util.NetUtil;
import org.d3.server.TestService;
import org.d3.std.Stopwatch;

import com.google.common.collect.Lists;

public class NettyClientTest {

	public static void main(String[] args) throws InterruptedException, NoSuchMethodException, SecurityException {
		
		final SimpleClient client = new SimpleClient();
//		client.connect(new InetSocketAddress("127.0.0.1", 8082), new InetSocketAddress("127.0.0.1", 8083));
		client.connect(new InetSocketAddress("127.0.0.1", 28256));
		System.out.println(client.ready());
		if(!client.ready()) return;
		client.registerService(new TestService());
		
		D3ChannelGroup group = client.getGroups().get(NetUtil.localHostName());
		Request request = new Request("Test", Test.class.getMethod("test", null), null);
//		Test test = Proxies
//					.asyncStrategy(Test.class)
//					.node(client)
//					.callback(new Invokable() {
//						
//						@Override
//						public void invoke(Object result) {
//							System.out.println("over");
//						}
//						
//					})
//					.asyncProxy();
//		test.test();
//		Thread.sleep(1000);
//		System.out.println(World.defaultChannelGroup().size());
//		System.out.println(World.defaultChannelGroup().next());
		
//		for(int i = 0; i < 10; i++){
//			Thread t = new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						asyncMetric(client);
//					} catch (NoSuchMethodException e) {
//						e.printStackTrace();
//					} catch (SecurityException e) {
//						e.printStackTrace();
//					}
//				}
//			});
//			t.start();
//		}
		
//		test.testString();
		for(int i = 0; i < 10000; i++){
			asyncMetric(client);

		}

//		List<String> list = Lists.newArrayList("b", "c", "a");
//		List<String> afterSorted = test.order(list);
//		System.out.println(afterSorted);
	}
	
	private static void asyncMetric(SimpleClient client) throws NoSuchMethodException, SecurityException, InterruptedException{
		int loop = 10000;
		final CountDownLatch latch = new CountDownLatch(loop);
		
//		Test test = Proxies
//				.asyncStrategy(Test.class)
//				.node(client)
//				.callback(new Invokable() {
//					
//					@Override
//					public void invoke(Object result) {
////						System.out.println("over");
//						latch.countDown();
//					}
//					
//				})
//				.asyncProxy();
//		D3ChannelGroup group = client.getGroups().get(NetUtil.localHostName());
//		Request request = new Request("Test", Test.class.getMethod("test", null), null);
//		Test test oxies.getAsyncProxy(Test.class, client);
//		Test test = Proxies.getProxy(Test.class, client, new Invokable() {
//			@Override
//			public void invoke(Object result) {
//				latch.countDown();
//			}
//		});
		Test test = ServiceBuilder.async(Test.class, new Invokable() {
			@Override
			public void invoke(Object result) {
				latch.countDown();
			}
		}, client);
//		Test test = ServiceBuilder.sync(Test.class, client);
		Stopwatch sw = Stopwatch.newStopwatch();
		for(int i = 0; i < loop; i++){
//			System.out.println(test);
			test.testString();
//			group.next().send(request);
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(sw.longTime());
	}
	
	private static void metric(Test test){
		int loop = 10000;
		long length = 0;
		Stopwatch sw = Stopwatch.newStopwatch();
		for(int i = 0; i < loop; i++){
			test.test();
		}
		System.out.println(sw.longTime());
	}

}
