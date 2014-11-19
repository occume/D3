package org.d3.rpc.net.bean;

public class Request {
	
	private long id;
	private String serviceName;
	private String methodName;
	private Object[] args;
	
	public Request(String serviceName, String methodName, Object[] args) {
		this.serviceName = serviceName;
		this.methodName = methodName;
		this.args = args;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	@Override
	public String toString() {
		return "Request [id=" + id + ", serviceName=" + serviceName
				+ ", methodName=" + methodName + "]";
	}
	
}
