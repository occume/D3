package org.d3.rpc.net.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.d3.rpc.annotion.RemoteService;
import org.d3.rpc.net.bean.ServiceEntry;

public class SimpleEngine implements Engine{
	
	private NettyServer server;
	
	private static SimpleEngine instance = new SimpleEngine();
	
	public static SimpleEngine instance(){
		return instance;
	}
	
	private Map<String, ServiceEntry> services = new HashMap<String, ServiceEntry>();
	
	private ConcurrentHashMap<String, Method> methods = new ConcurrentHashMap<>();
	
	private SimpleEngine(){
		this.server = new NettyServer();
	}
	
	public void launch(){
		server.start();
	};
	
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
}
