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
	
	private AtomicLong requestIndex = new AtomicLong(1);
	
	public D3Channel(int id, Channel channel){
		this.id = id;
		this.channel = channel;
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
	
	public long promiseSize(){
		return promises.size();
	}
	
	public long generateRequestIndex(){
		return requestIndex.getAndIncrement();
	}
}
