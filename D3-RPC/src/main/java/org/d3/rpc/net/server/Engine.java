package org.d3.rpc.net.server;

import java.lang.reflect.Method;
import java.util.Map;

import org.d3.rpc.net.bean.ServiceEntry;

public interface Engine {

	public void launch();
	
	public void registerService(Object service);
	
	public Map<String, ServiceEntry> getAllServices();
	
	public Method getMethod(String name);
	
	public void putMethod(String name, Method method);
}
