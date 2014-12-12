package org.d3.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Closer {
	
	private static Logger LOG = LoggerFactory.getLogger(Closer.class);
	private static final int SHUTDOWN_PORT = 28257;
	
	public static void listen(Invokable invoker, EventLoopGroup worker, int port){
		Thread t = new Thread(new CloseTask(invoker, worker, port));
		t.setDaemon(true);
		t.start();
	}
	
	public static void listen(Invokable invoker){
		listen(invoker, ThreadPools.singleEventLoopGroup(), SHUTDOWN_PORT);
	}
	
	public static void close(){
		Bootstrap b = new Bootstrap();
		b.group(ThreadPools.singleEventLoopGroup())
		 .channel(NioSocketChannel.class)
		 .handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				
			}
		});
		
		ChannelFuture future = b.connect("127.0.0.1", SHUTDOWN_PORT);
		future.addListener(new ChannelFutureListener() {
	        @Override
	        public void operationComplete(ChannelFuture future) {
	        	ThreadPools.closeDefaultSingleEventLoopGroup();
	        }
	    });
	}
	
	private static class CloseTask implements Runnable{
		
		private Invokable invoker;
		private EventLoopGroup worker;
		private int port;
		
		public CloseTask(Invokable invoker, EventLoopGroup worker, int port){
			this.invoker = invoker;
			this.worker = worker;
			this.port = port;
		}
		
		@Override
		public void run() {
			try {
				
				ServerBootstrap b = new ServerBootstrap();
				b.group(worker)
				 .channel(NioServerSocketChannel.class)
				 .childHandler(new ChannelInitializer<Channel>() {
					protected void initChannel(Channel ch) throws Exception {
						LOG.info("close command from: {}", ch);
						invoker.invoke();
						ThreadPools.closeDefaultSingleEventLoopGroup();
					}
				});
				
				b.bind(port);

				LOG.info("Listen shutdown port: {}", port);
			} catch (Exception e) {
				LOG.error("shutdown is interrupted");
			}
		}
	} 
}
