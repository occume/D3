package org.d3.rpc.util;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Reflections {
	
	public static Method getByParameter(Class<?> clazz, String paramType){
		Method[] methods = clazz.getMethods();
		for(Method method: methods){
			if(paramType2String(method).equals(paramType)){
				return method;
			}
		}
		return null;
	}
	
	public static String paramType2String(Method method){
		return Arrays.toString(method.getParameterTypes());
	}
	
}
