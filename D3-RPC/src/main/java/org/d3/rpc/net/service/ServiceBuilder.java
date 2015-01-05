package org.d3.rpc.net.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.node.Node;
import org.d3.rpc.util.Invokable;

public class ServiceBuilder {

	@SuppressWarnings("unchecked")
	public static <T> T async(Class<T> clazz, final Invokable invoke, Node node){
		
		final Accessor accessor = new AsyncAccessor(node);
		final String serviceName = clazz.getSimpleName();
		
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {

				final Request request = new Request(serviceName, method, args);
				accessor.accessAsync(request, invoke);
				return null;
			}
		};
		
		T ret = (T) Proxy.newProxyInstance(ServiceBuilder.class.getClassLoader(), new Class[] {clazz}, handler);
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T sync(Class<T> clazz, Node node){
		
		final Accessor accessor = new SyncAccessor(node);
		final String serviceName = clazz.getSimpleName();
		
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {

				final Request request = new Request(serviceName, method, args);
				
				Object result = accessor.accessSync(request);
				return result;
			}
		};
		
		T ret = (T) Proxy.newProxyInstance(ServiceBuilder.class.getClassLoader(), new Class[] {clazz}, handler);
		return ret;
	}
}
