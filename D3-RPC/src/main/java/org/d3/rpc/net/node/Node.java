package org.d3.rpc.net.node;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	
	public void add2Group(String groupName, D3Channel channel);
	
	public NodeType type();
	
	public ConcurrentHashMap<String, DefaultD3ChannelGroup> getGroups();
}
