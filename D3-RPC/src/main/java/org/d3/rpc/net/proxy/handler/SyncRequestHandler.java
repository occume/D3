package org.d3.rpc.net.proxy.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.dispatcher.sync.SyncDispatchStrategy;

public class SyncRequestHandler implements InvocationHandler {

	private String serviceName;
	
	public SyncRequestHandler(String serviceName){
		this.serviceName = serviceName;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Request request = new Request(serviceName, method.getName(), args);
		Object result = SyncDispatchStrategy.ROUND.dispatch(request);
		return result;
	}

}
