package org.d3.monitor.warrior;

import io.netty.util.concurrent.Promise;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.d3.monitor.listener.LaunchBigdataListener;
import org.d3.monitor.service.BigdataClientWatcher;
import org.d3.monitor.service.LoadTest;
import org.d3.monitor.service.Report;
import org.d3.monitor.util.EventListener;
import org.d3.monitor.util.NetUtil;
import org.d3.monitor.vo.Usage;
import org.d3.monitor.warrior.usage.CPU;
import org.d3.monitor.warrior.usage.IO;
import org.d3.monitor.warrior.usage.Mem;
import org.d3.monitor.warrior.usage.Net;
import org.d3.rpc.net.node.client.SimpleClient;
import org.d3.rpc.net.service.ServiceBuilder;
import org.d3.rpc.util.Closer;
import org.d3.rpc.util.Invokable;
import org.d3.rpc.util.ThreadPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Warrior {
	
	private final SimpleClient client;
	
	private static final int SERVER_PORT = 28256;
	
	private List<EventListener> listeners;
	
	public static enum Event{
		START,
		SHUTDOWN
	}
	
	private static Logger LOG = LoggerFactory.getLogger(Warrior.class);
	
	public Warrior(){
		client = new SimpleClient();
		listeners = new ArrayList<>(10);
		listeners.add(new LaunchBigdataListener());
	}
	
	public void start(){
		
		client.connect(new InetSocketAddress("172.16.140.47", SERVER_PORT));
		client.ready();
		/**
		 * 开启关闭端口
		 * 在其他任务启动之前开启
		 * 避免影响关闭任务
		 */
		listenShutdown();
//		onStart();
//		doReport();
//		doWatch();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(;;){
					metric();
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(;;){
					metric();
				}
			}
		}).start();
		
		ThreadPools.defaultPool10()
			.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run() {
				int success = 0;
				int fail = 0;
				Collection<Promise<Object>> promises = client.getAllPromise();
				for(Promise<Object> promise: promises){
					if(promise.isSuccess()){
						success++;
					}
					else{
						fail++;
					}
				}
//				System.out.println("all promises: " + client.getAllPromise().size());
				System.out.println("suucess promises: " + success);
				System.out.println("fail promises: " + fail);
				System.out.println("receive ok: " + client.getReceiveOk());
//				System.out.println("send ok: " + client.getSendOk());
//				System.out.println("send fail: " + client.getFailCount());
				System.out.println("remain: " + client.remain());
			}}, 0, 2, TimeUnit.SECONDS);
	}
	
	private void metric(){
		int loop = 10000;
//		final CountDownLatch latch = new CountDownLatch(loop);

		LoadTest load = ServiceBuilder.async(LoadTest.class, new Invokable() {
			@Override
			public void invoke(Object result) {
//				latch.countDown();
			}
		}, client);
//		Stopwatch sw = Stopwatch.newStopwatch();
		for(int i = 0; i < loop; i++){
//			System.out.println(client.ready());
			
			load.test();
		}
//		try {
//			latch.await();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
//		System.out.println(sw.longTime());
	}

	private void onStart() {
		for(EventListener listener: listeners){
			listener.onEvent(Event.START);
		}
	}

	public static void main(String[] args) {
		Warrior warrior = new Warrior();
		warrior.start();
	}
	
	private  void doWatch(){
		System.out.println("doWatch");
		ThreadPools.defaultPool10()
			.scheduleAtFixedRate(new WatchTask(), 0, 1, TimeUnit.SECONDS);
		
//		Executors.newSingleThreadScheduledExecutor().
//			scheduleAtFixedRate(new WatchTask(), 0, 1, TimeUnit.SECONDS);
	}
	
	private  void doReport(){
		ThreadPools.defaultPool10()
					.scheduleAtFixedRate(new ReportTask(), 0, 5, TimeUnit.SECONDS);
	}

	private void listenShutdown() {
		System.out.println("listen shutDown");
		Closer.listen(new Invokable() {
			@Override
			public void invoke(Object obj) {
				client.shutDown();
				ThreadPools.shutDown();
			}
		});
	}
	
	private static Usage getCurrUsage(){
		Usage u = new Usage(
				NetUtil.hostName(),
				Net.getInstance().get(),
				Mem.getInstance().get(),
				IO.getInstance().get(),
				CPU.getInstance().get());
//		u =  new Usage(NetUtil.hostName(), 1, 2, 3, 4);
		return u;
	}
	
	private class ReportTask implements Runnable{
		private Report report;
		public ReportTask(){
			report = ServiceBuilder.sync(Report.class, Warrior.this.client);
		}
		@Override
		public void run() {
			report.report(getCurrUsage());
		}
	}

	private class WatchTask implements Runnable{
		private BigdataClientWatcher watcher;
		
		public WatchTask(){
			watcher = ServiceBuilder.sync(BigdataClientWatcher.class, Warrior.this.client);
		}
		@Override
		public void run() {
		
			try{
			
			long len = watcher.vlength();
//			System.out.println(len);
			File version = new File("/home/deploy/bigdata/conf/version");
			long thislen = version.length();
			System.out.println(Thread.currentThread().getName() + " : " + thislen);
			if(len != thislen){
//				BigdataUpdater.updateAndRestart();
				System.out.println("changed: " + len);
//				Thread.sleep(5000);
			}
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
	}
}
