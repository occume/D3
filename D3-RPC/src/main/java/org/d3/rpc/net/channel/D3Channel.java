package org.d3.rpc.net.channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Promise;

public class D3Channel {
	
	private int id;
	
	private Channel channel;
	
	private ConcurrentHashMap<Long, Promise<Object>> promises = new ConcurrentHashMap<>();
	
	private ConcurrentHashMap<Long, InvokeFuture<Object>> futures = new ConcurrentHashMap<>();
	
	private AtomicLong requestIndex = new AtomicLong(1);
	
	public static ChannelFuture CANNOT_WRITE_NOW = null;
	
	public D3Channel(int id, Channel channel){
		this.id = id;
		this.channel = channel;
	}
	
	public ChannelFuture send(Object req){
//		if(!channel.isWritable())
//			return CANNOT_WRITE_NOW;
		/**
		 * TODO 
		 * if we can move the reconnect() here?
		 */
		ChannelFuture f = channel.writeAndFlush(req);
		return f;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public ChannelFuture close(){
		return this.channel.close();
	}
	
	public void addPromise(long key, Promise<Object> value){
		promises.putIfAbsent(key, value);
	}
	
	public Promise<Object> getPromise(long key){
		return promises.remove(key);
	}
	
	public void addFuture(long key, InvokeFuture<Object> future){
		futures.putIfAbsent(key, future);
	}
	
	public InvokeFuture<Object> getFuture(long key){
		return futures.get(key);
	}
	
	public long promiseSize(){
		return promises.size();
	}
	
	public long generateRequestIndex(){
		return requestIndex.getAndIncrement();
	}
}
