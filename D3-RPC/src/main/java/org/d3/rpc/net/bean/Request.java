package org.d3.rpc.net.bean;

import java.lang.reflect.Method;

public class Request extends Message{
	
	private String serviceName;
	private MethodEntry methodEntry;
	
	public Request(String serviceName, String methodName, Object[] args) {
		this.serviceName = serviceName;
	}
	
	public Request(String serviceName, Method method, Object[] args) {
		this.serviceName = serviceName;
		this.methodEntry = new MethodEntry(method, args);
	}
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public MethodEntry getMethodEntry() {
		return methodEntry;
	}

	public void setMethodEntry(MethodEntry methodEntry) {
		this.methodEntry = methodEntry;
	}

	@Override
	public String toString() {
		return "Request [id=" + id + ", serviceName=" + serviceName
				+ ", methodEntry=" + methodEntry + "]";
	}

}
