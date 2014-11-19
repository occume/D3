package org.d3.rpc.net.server;

import java.util.HashMap;
import java.util.Map;
import org.d3.rpc.annotion.RemoteService;
import org.d3.rpc.net.bean.ServiceEntry;

public class Box {
	
	private NettyServer server;
	
	private static Box instance = new Box();
	
	public static Box instance(){
		return instance;
	}
	
	private Map<String, ServiceEntry> services = new HashMap<String, ServiceEntry>();
	
	private Box(){
		this.server = new NettyServer();
	}
	
	public void start(){
		server.start();
	};
	
	public ServiceEntry getService(String name){
		return services.get(name);
	}
	
	public void register(Object service){
		Class<?>[] interfaceList = service.getClass().getInterfaces();
		if (interfaceList != null) {
			for (Class<?> clazz : interfaceList) {
				RemoteService remoteServiceContract = clazz.getAnnotation(RemoteService.class);
				if (remoteServiceContract == null) {
					continue;
				}

//				String name = remoteServiceContract.value();
				
				registerService(clazz, service);
			}
		}
	}
	
	public void registerService(Class<?> serviceInterface, Object service) {
		ServiceEntry serviceEntry = new ServiceEntry(serviceInterface.getSimpleName(), service);
		this.services.put(serviceEntry.getName(), serviceEntry);
	}
	
	public Map<String, ServiceEntry> getAllServices(){
		return services;
	}
}
