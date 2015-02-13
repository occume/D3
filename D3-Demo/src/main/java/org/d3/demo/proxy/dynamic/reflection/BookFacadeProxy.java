package org.d3.demo.proxy.dynamic.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class BookFacadeProxy implements InvocationHandler {
	
	private Object target;
	
	public Object bind(Object target){
		this.target = target;
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		String name = method.getName();
		Object result = null;
		System.out.println("before " + name + "();");
		result = method.invoke(target, args);
		System.out.println("after " + name + "();");
		return result;
	}

}
