package org.d3.rpc.net.node.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.d3.rpc.net.codec.LengthBasedDecoder;
import org.d3.rpc.net.codec.LengthBasedEncoder;
import org.d3.rpc.net.handler.ChannelClosedHandler;
import org.d3.rpc.net.handler.ExceptionHandler;
import org.d3.rpc.net.handler.JoinGroupHandler;
import org.d3.rpc.net.node.SimpleNode;
import org.d3.rpc.util.ThreadPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class SimpleClient extends SimpleNode implements Client{
	
	public static final int CONNECTED = 1;
	
	public static final int RECONNECTING = 2;
	
	public static final int CLOSED = 3;
	
	/**
	 *  client 当前的状态
	 */
	volatile int statu = 0;
	
	private Map<ChannelOption<? extends Object>, Object> options = new HashMap<ChannelOption<? extends Object>, Object>();
	
	private Bootstrap bootstrap;
	
	private static final int CHANNEL_PER_NODE = 10;
	
	private static final int CONNECT_TIME_OUT = 3000;
	
	private CountDownLatch wait4Start = new CountDownLatch(CHANNEL_PER_NODE);
	
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
		options.put(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
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
		
		bootstrap.group(getWorker());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(initor());
		
		doConnect(addressSet);
	
		/**
		 * 启动定时重连任务
		 */
		doReconnect(addressSet);
	}
	
	private void doConnect(Set<SocketAddress> addressSet){
		for(SocketAddress socketAddress: addressSet){
			final InetSocketAddress isa = (InetSocketAddress) socketAddress;
			
			for(int i = 0; i < CHANNEL_PER_NODE; i++){
				ChannelFuture future = bootstrap.connect(socketAddress);
				future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if(future.isSuccess()){
//							AttributeKey<String> address = AttributeKey.valueOf(isa.getHostString());
//							future.channel().attr(address);
							LOG.info("connect success");
						}
					}
				});
			}
			/**
			 *  连接成功 或 超时
			 *  
			 */
			try {
				wait4Start.await(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS);
				
				if(wait4Start.getCount() < CHANNEL_PER_NODE){
					this.statu = CONNECTED;
					wait4Start = new CountDownLatch(CHANNEL_PER_NODE);
					LOG.info("Client connet to port: {}", isa.getPort());
					
					releaseAll();
				}
				else{
					this.statu = CLOSED;
				}
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}
		}
	}
	
	private class ReconnectTask implements Runnable{
		private Set<SocketAddress> addressSet;
		public ReconnectTask(Set<SocketAddress> addressSet){
			this.addressSet = addressSet;
		}
		@Override
		public void run() {
			if(SimpleClient.this.statu == CONNECTED)return;
			if(SimpleClient.this.statu == RECONNECTING)return;

			LOG.info("do reconnect..., statu: {}", SimpleClient.this.ready());

			SimpleClient.this.statu = RECONNECTING;
			doConnect(addressSet);
		}
	}
	
	private void doReconnect(Set<SocketAddress> addressSet){
		LOG.info("start reconnect task");
		ThreadPools.defaultPool10()
			.scheduleAtFixedRate(new ReconnectTask(addressSet), 3000, 5000, TimeUnit.MILLISECONDS);
	}
	
	private ChannelInitializer<SocketChannel> initor(){
		return new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new LengthBasedEncoder());
				ch.pipeline().addLast(new LengthBasedDecoder());
				ch.pipeline().addLast(new ExceptionHandler());
				ch.pipeline().addLast(new ChannelClosedHandler(SimpleClient.this));
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

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void shutDown() {
		System.out.println("---shutdown");
		Future f = getWorker().shutdownGracefully();
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

	@Override
	public void reconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean ready() {
		return statu == CONNECTED;
	}

	@Override
	public void statu(int statu) {
		this.statu = statu;
	}

}
