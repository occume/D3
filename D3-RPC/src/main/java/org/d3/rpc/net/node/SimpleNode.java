package org.d3.rpc.net.node;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.d3.rpc.annotion.RemoteService;
import org.d3.rpc.net.bean.ServiceEntry;
import org.d3.rpc.net.channel.D3Channel;
import org.d3.rpc.net.channel.DefaultD3ChannelGroup;
import org.d3.rpc.util.NetUtil;

public abstract class SimpleNode implements Node{
	
	private Map<String, ServiceEntry> services = new HashMap<String, ServiceEntry>();
	
	private ConcurrentHashMap<String, Method> methods = new ConcurrentHashMap<>();
	
	private ConcurrentHashMap<String, DefaultD3ChannelGroup> groups;
	
	private String nodeName;
	
	public SimpleNode(){
		nodeName = NetUtil.localHostName();
		groups = new ConcurrentHashMap<>(10);
	}
	
	public ServiceEntry getService(String name){
		return services.get(name);
	}
	
	public void registerService(Object service){
		Class<?>[] interfaceList = service.getClass().getInterfaces();
		if (interfaceList != null) {
			for (Class<?> clazz : interfaceList) {
				RemoteService remoteService = clazz.getAnnotation(RemoteService.class);
				if (remoteService == null) {
					continue;
				}
				
				registerService(clazz, service);
			}
		}
	}
	
	private void registerService(Class<?> serviceInterface, Object service) {
		ServiceEntry serviceEntry = new ServiceEntry(serviceInterface.getSimpleName(), service);
		this.services.put(serviceEntry.getName(), serviceEntry);
	}
	
	public Map<String, ServiceEntry> getAllServices(){
		return services;
	}
	
	public Method getMethod(String name){
		return methods.get(name);
	}

	@Override
	public void putMethod(String name, Method method) {
		methods.putIfAbsent(name, method);
	}

	@Override
	public String nodeName() {
		return nodeName;
	}
	
	@Override
	public void add2Group(String groupName, D3Channel channel){
		DefaultD3ChannelGroup group = groups.get(groupName);
		if(group == null){
			group = new DefaultD3ChannelGroup(groupName);
			DefaultD3ChannelGroup prev = groups.putIfAbsent(groupName, group);
			if(prev != null){
				group = prev;
			}
		}
		group.add(channel);
	};
	
	public ConcurrentHashMap<String, DefaultD3ChannelGroup> getGroups(){
		return groups;
	};
	
	public static enum NodeType{
		CLIENT,
		SERVER
	}
}
