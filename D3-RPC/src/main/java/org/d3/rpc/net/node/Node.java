package org.d3.rpc.net.node;

import io.netty.util.concurrent.Promise;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import org.d3.rpc.net.bean.ServiceEntry;
import org.d3.rpc.net.channel.D3Channel;
import org.d3.rpc.net.channel.DefaultD3ChannelGroup;
import org.d3.rpc.net.node.SimpleNode.NodeType;

public interface Node {
	
	public void shutDown();
	
	public ServiceEntry getService(String serviceName);
	
	public void registerService(Object service);
	
	public Map<String, ServiceEntry> getAllServices();
	
	public Method getMethod(String name);
	
	public void putMethod(String name, Method method);
	
	public String nodeName();
	/**
	 * add a channel to special group
	 * NOTE: remove is auto
	 * @param groupName
	 * @param channel
	 */
	public void addChannel(String groupName, D3Channel channel);
	
	public NodeType type();
	
	public boolean isClient();
	
	public boolean ready();
	
	public void aquire();
	
	public void release();
	
	public void releaseAll();
	
	public int	remain();
	
	public void addPromise(long id, Promise<Object> promise);
	
	public void removePromise(long id);
	
	public void receiveOkIncrement();
	
	public long getReceiveOk();

	public void sendOkIncrement();
	
	public long getSendOk();
	
	public void failCountIncrement();
	
	public long getFailCount();
	
	public long reqestIndex();
	
	public ConcurrentHashMap<String, DefaultD3ChannelGroup> getGroups();
}
