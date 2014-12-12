package org.d3.monitor.warrior;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.d3.monitor.listener.LaunchBigdataListener;
import org.d3.monitor.listener.LaunchBigdataListener.BigdataUpdater;
import org.d3.monitor.service.BigdataClientWatcher;
import org.d3.monitor.service.Report;
import org.d3.monitor.util.EventListener;
import org.d3.monitor.util.NetUtil;
import org.d3.monitor.vo.Usage;
import org.d3.monitor.warrior.usage.CPU;
import org.d3.monitor.warrior.usage.IO;
import org.d3.monitor.warrior.usage.Mem;
import org.d3.monitor.warrior.usage.Net;
import org.d3.rpc.net.node.client.SimpleClient;
import org.d3.rpc.net.proxy.Proxies;
import org.d3.rpc.util.Closer;
import org.d3.rpc.util.Invokable;
import org.d3.rpc.util.ThreadPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Warrior {
	
	private SimpleClient client;
	
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
	};
	
	public void start(){
		client.connect(new InetSocketAddress("172.16.140.47", SERVER_PORT));
		onStart();
		doReport();
		doWatch();
		listenShutdown();
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
	
	private static void doWatch(){
		System.out.println("doWatch");
		ThreadPools.defaultEventLoopGroup()
			.scheduleAtFixedRate(new WatchTask(), 0, 1, TimeUnit.SECONDS);
	}
	
	private static void doReport(){
		ThreadPools.defaultEventLoopGroup()
					.scheduleAtFixedRate(new ReportTask(), 0, 5, TimeUnit.SECONDS);
	}

	private void listenShutdown() {
		Closer.listen(new Invokable() {
			@Override
			public void invoke() {
				client.shutDown();
				ThreadPools.closeDefaultEventLoopGroup();
			}
		});
	}
	
	private static Usage getCurrUsage(){
//		Usage u = new Usage(
//				NetUtil.hostName(),
//				Net.getInstance().get(),
//				Mem.getInstance().get(),
//				IO.getInstance().get(),
//				CPU.getInstance().get());
		
		return new Usage(NetUtil.hostName(), 1, 2, 3, 4);
	}
	
	private static class ReportTask implements Runnable{
		private Report report;
		public ReportTask(){
			report = Proxies.getProxy(Report.class);
		}
		@Override
		public void run() {
			report.report(getCurrUsage());
		}
	}

	private static class WatchTask implements Runnable{
		private BigdataClientWatcher watcher;
		
		
		public WatchTask(){
			watcher = Proxies.getProxy(BigdataClientWatcher.class);
		}
		@Override
		public void run() {
			System.out.println("12312312");
			try{
			System.out.print(Thread.currentThread().getName() + " : ");
			long len = watcher.vlength();
			System.out.println(len);
			File version = new File("/home/deploy/bigdata/conf/version");
			long thislen = version.length();
			if(len != thislen){
//				BigdataUpdater.updateAndRestart();
				System.out.println("changed: " + len);
			}
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
	}
}
