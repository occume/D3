package org.d3.rpc.net.client;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.d3.rpc.manage.EventLoopGroups;
import org.d3.rpc.net.handler.RequestDecoder;
import org.d3.rpc.net.handler.RequestEncoder;
import org.d3.rpc.net.handler.ResponseDecoder;
import org.d3.rpc.net.handler.ResponseHandler;
import org.d3.rpc.net.handler.StringRequestHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
	
	private Map<ChannelOption<? extends Object>, Object> options = new HashMap<ChannelOption<? extends Object>, Object>();
	
	private Bootstrap bootstrap;
	
	private EventLoopGroup worker = EventLoopGroups.cpuCountEventLoopGroup("D3-RPC-WORKER");
	
	public NettyClient(){
		
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
		bootstrap.handler(getInitor());
		
		for(SocketAddress socketAddress: addressSet){
			ChannelFuture future = bootstrap.connect(socketAddress);
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(!future.isSuccess()){
						System.out.println("connect not success");
					}
					else{
						System.out.println("connect success");
					}
				}
			});
			try {
				future.sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private ChannelInitializer<SocketChannel> getInitor(){
		return new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new RequestEncoder());
				ch.pipeline().addLast(new ResponseDecoder());
				ch.pipeline().addLast(new ResponseHandler());
			}
		};
	}

	public Map<ChannelOption<? extends Object>, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<ChannelOption<? extends Object>, Object> options) {
		this.options = options;
	}

}
