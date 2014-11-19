package org.d3.rpc.net.bean;

public class ServiceEntry {
	
	private String name;
	private Object service;
	
	public ServiceEntry(String name, Object service) {
		this.name = name;
		this.service = service;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getService() {
		return service;
	}
	public void setService(Object service) {
		this.service = service;
	}
	
}
