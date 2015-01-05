package org.d3.rpc.net.channel;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.d3.rpc.net.node.Node;
import org.d3.rpc.net.node.client.Client;
import org.d3.rpc.net.node.client.SimpleClient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class DefaultD3ChannelGroup implements D3ChannelGroup{
	
	private final String name;
	
//	private final ConcurrentSet<D3Channel> channels = new ConcurrentSet<D3Channel>();
	private AtomicInteger index = new AtomicInteger(-1);
	
	private AtomicInteger round = new AtomicInteger(0);
	
	private ConcurrentHashMap<Integer, D3Channel> channels = new ConcurrentHashMap<>();
	
	private final ChannelFutureListener remover = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            remove(future.channel());
        }
    };
    
    private Node node;
	
	public DefaultD3ChannelGroup(String name, Node node){
		this.name = name;
		this.node = node;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public void close() {
		
        for (D3Channel c: channels.values()) {
        	c.close();
        }

	}

	@Override
	public int size() {
		return channels.size();
	}

	@Override
	public D3Channel next() {
		
		for(;;){
			
			int idx = round.getAndIncrement();
			int size = channels.size();
			
			if(size == 0){
				return null;
			}
			
			try{
				D3Channel channel = channels.get(idx % size);
				return channel;
			} catch (IndexOutOfBoundsException e) {
				
			}
			
		}
	}

	@Override
	public boolean add(D3Channel e) {
		for(;;){
			
			int curr = index.get();
			int add1 = curr + 1;
			if(index.compareAndSet(curr, add1)){
				
				e.getChannel().closeFuture().addListener(remover);
				channels.putIfAbsent(add1, e);
				return true;
			}
		}
	}
	
	private int getKey(Channel channel){
		for(Entry<Integer, D3Channel> c: channels.entrySet()){
			if(channel == c.getValue().getChannel()){
				return c.getKey();
			}
		}
		return -1;
	}

	public void remove(Channel channel){
		
		int key = getKey(channel);
		
		if(channels.remove(key) != null){
			index.getAndDecrement();
			channel.closeFuture().removeListener(remover);
			
			if(index.get() == 0){
				if(node.isClient()){
					Client client = (Client) node;
					client.statu(SimpleClient.CLOSED);
				}
			}
		}
	}
}
