package org.d3.rpc.net.node;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Promise;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.d3.rpc.annotion.RemoteService;
import org.d3.rpc.net.bean.ServiceEntry;
import org.d3.rpc.net.channel.D3Channel;
import org.d3.rpc.net.channel.DefaultD3ChannelGroup;
import org.d3.rpc.util.NetUtil;
import org.d3.rpc.util.ThreadPools;

public abstract class SimpleNode implements Node{
	
	private Map<String, ServiceEntry> services = new HashMap<String, ServiceEntry>();
	
	private ConcurrentHashMap<String, Method> methods = new ConcurrentHashMap<>();
	
	private ConcurrentHashMap<String, DefaultD3ChannelGroup> groups;
	
	private ConcurrentHashMap<Long, Promise<Object>> promises = new ConcurrentHashMap<>();
	
	private EventLoopGroup worker = ThreadPools.cpuCountPool("D3-RPC-WORKER");
	
	private String nodeName;
	
	public SimpleNode(){
		nodeName = NetUtil.localHostName();
		groups = new ConcurrentHashMap<>(10);
	}
	
	public EventLoopGroup getWorker() {
		return worker;
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
	public void addChannel(String groupName, D3Channel channel){
		DefaultD3ChannelGroup group = groups.get(groupName);
		if(group == null){
			group = new DefaultD3ChannelGroup(groupName, this);
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

	@Override
	public boolean isClient() {
		return type() == NodeType.CLIENT;
	}

	Semaphore parker = new Semaphore(10000);
	AtomicInteger parkings = new AtomicInteger(0);
	
	public void unPark(){
		parker.release(parkings.get());
		parkings.set(0);
	}
	
	@Override
	public void aquire() {
		try {
			parkings.getAndIncrement();
			parker.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void release(){
		parker.release(1);
	}
	
	public void releaseAll(){
		int r = 10000 - remain();
		parker.release(r);
	}
	
	public int	remain(){
		return parker.availablePermits();
	}
	
	public void addPromise(long id, Promise<Object> promise){
		promises.put(id, promise);
	}
	
	public void removePromise(long id){
		promises.remove(id);
	}
	
	public Collection<Promise<Object>> getAllPromise(){
		return promises.values();
	}
	
	private AtomicLong receiveOk = new AtomicLong(0);
	private AtomicLong sendOk = new AtomicLong(0);
	
	public void receiveOkIncrement(){
		receiveOk.incrementAndGet();
	}
	
	public long getReceiveOk(){
		return receiveOk.get();
	}
	
	public void sendOkIncrement(){
		sendOk.incrementAndGet();
	}
	
	public long getSendOk(){
		return sendOk.get();
	}

	private AtomicLong failCount = new AtomicLong(0);
	
	public void failCountIncrement(){
		failCount.incrementAndGet();
	}
	
	public long getFailCount(){
		return failCount.get();
	}
	
	private AtomicLong requestIndex = new AtomicLong(1);
	public long reqestIndex(){
		return requestIndex.getAndIncrement();
	}

	public static enum NodeType{
		CLIENT,
		SERVER
	}
}
