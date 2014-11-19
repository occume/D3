package org.d3.rpc.net.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.d3.rpc.net.proxy.handler.SyncRequestHandler;

public class Proxies {
	@SuppressWarnings("unchecked")
	public static <T> T getProxy(Class<T> clazz){
		
		InvocationHandler handler = new SyncRequestHandler(clazz.getSimpleName());
		
		T ret = (T) Proxy.newProxyInstance(Proxies.class.getClassLoader(), new Class[] {clazz}, handler);
		return ret;
	}
}
