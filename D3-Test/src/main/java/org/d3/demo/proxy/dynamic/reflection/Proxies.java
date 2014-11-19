package org.d3.demo.proxy.dynamic.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Proxies {

	@SuppressWarnings("unchecked")
	public static <T> T getProxy(Class<T> clazz){
		
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				System.out.println("who do the thing?");
				return 1066;
			}
		};
		
		T ret = (T) Proxy.newProxyInstance(Proxies.class.getClassLoader(), new Class[] {clazz}, handler);
		return ret;
	}
	
}
