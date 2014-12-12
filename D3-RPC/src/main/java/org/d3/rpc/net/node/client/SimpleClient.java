package org.d3.rpc.net.node.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.d3.rpc.net.codec.LengthBasedDecoder;
import org.d3.rpc.net.codec.LengthBasedEncoder;
import org.d3.rpc.net.handler.JoinGroupHandler;
import org.d3.rpc.net.handler.RequestEncoder;
import org.d3.rpc.net.handler.ResponseDecoder;
import org.d3.rpc.net.handler.exception.ExceptionHandler;
import org.d3.rpc.net.node.SimpleNode;
import org.d3.rpc.util.ThreadPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class SimpleClient extends SimpleNode implements Client{
	
	private Map<ChannelOption<? extends Object>, Object> options = new HashMap<ChannelOption<? extends Object>, Object>();
	
	private Bootstrap bootstrap;
	
	private EventLoopGroup worker = ThreadPools.cpuCountEventLoopGroup("D3-RPC-WORKER");
	
	private static final int CHANNEL_PER_NODE = 5;
	
	private CountDownLatch wait4Start = new CountDownLatch(5);
	
	private Logger LOG = LoggerFactory.getLogger(SimpleClient.class);
	
	public SimpleClient(){
		
		init();
		
		bootstrap = new Bootstrap();
	}
	
	private void init(){
		options.put(ChannelOption.TCP_NODELAY, true);
		options.put(ChannelOption.SO_KEEPALIVE, true);
		options.put(ChannelOption.SO_REUSEADDR, true);
//		options.put(HsfOptions.WRITE_IDLE_TIME, 10);
//		options.put(HsfOptions.READ_IDLE_TIME, 60);
//		options.put(HsfOptions.SYNC_INVOKE_TIMEOUT, 60000);
		options.put(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
//		options.put(HsfOptions.FLOW_LIMIT, 2000000);
//		options.put(HsfOptions.TIMEOUT_WHEN_FLOW_EXCEEDED, 0);
//		options.put(HsfOptions.MAX_THREAD_NUM_OF_DISPATCHER, 150);
//		options.put(HsfOptions.OPEN_SERVICE_INVOKE_STATISTIC, false);
//		options.put(HsfOptions.EVENT_EXECUTOR_QUEUE_CAPACITY, 1000000);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void connect(SocketAddress...addressArray){
		
		if(addressArray == null || addressArray.length == 0){
			throw new IllegalArgumentException("you should provide at least one address");
		}
		
		Set<SocketAddress> addressSet = new HashSet<>();
		for(SocketAddress address: addressArray){
			addressSet.add(address);
		}
		
		if(addressSet.size() == 0){
			throw new IllegalArgumentException("0 address");
		}
		
		Map<ChannelOption<? extends Object>, Object> options = getOptions();
		for( ChannelOption key: options.keySet()){
			bootstrap.option(key, options.get(key));
		}
		
		bootstrap.group(worker);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(initor());
		
		for(SocketAddress socketAddress: addressSet){
			InetSocketAddress isa = (InetSocketAddress) socketAddress;
			System.out.println("port: " + isa.getPort());
			for(int i = 0; i < CHANNEL_PER_NODE; i++){
				ChannelFuture future = bootstrap.connect(socketAddress);
				future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if(future.isSuccess()){
							System.out.println("connect success");
						}
					}
				});
//				try {
//					future.await();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
			try {
				wait4Start.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private ChannelInitializer<SocketChannel> initor(){
		return new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
//				ch.pipeline().addLast(new RequestEncoder());
//				ch.pipeline().addLast(new ResponseDecoder());
				ch.pipeline().addLast(new LengthBasedEncoder());
				ch.pipeline().addLast(new LengthBasedDecoder());
				ch.pipeline().addLast(new ExceptionHandler());
				ch.pipeline().addLast(new JoinGroupHandler(wait4Start, SimpleClient.this));
			}
		};
	}

	public Map<ChannelOption<? extends Object>, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<ChannelOption<? extends Object>, Object> options) {
		this.options = options;
	}
	
	public EventLoopGroup getWorker(){
		return worker;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void shutDown() {
		Future f = worker.shutdownGracefully();
		f.addListener(new FutureListener() {
			@Override
			public void operationComplete( Future future) throws Exception {
				if(future.isSuccess()){
					LOG.info("Client is shutdown");
				}
			}
		});
	}

	@Override
	public NodeType type() {
		return NodeType.CLIENT;
	}

}
