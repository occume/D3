package org.d3.demo.guava.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.d3.std.Stopwatch;

import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

public class TypeTokenMain {

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
		
		List<String> stringList = Lists.newArrayList();
		List<Integer> intList = Lists.newArrayList();
		
		System.out.println(intList.getClass().isAssignableFrom(stringList.getClass()));
		
		Test service = new TestService();
		TypeToken<?> tt = TypeToken.of(service.getClass());
//		Invokable invok = tt.method(service.getClass().getMethod("order", List.class, String.class, Object.class));
		Method method = service.getClass().getMethod("order", List.class, String.class, Object.class, byte[].class);
		
		System.out.println(Arrays.asList(method.getParameterTypes()));
		
		Stopwatch sw = Stopwatch.newStopwatch();
		for(int i = 0; i < 10000000; i++){
//			Object result = method.invoke(service, Lists.newArrayList("2", "3", "1"), "", null, null);
//			Object result = service.order(Lists.newArrayList("2", "3", "1"));
			Arrays.asList(method.getParameterTypes());
			if(i > 9999900){
				System.out.println(Arrays.toString(method.getParameterTypes()));
			}
		}
		
		System.out.println(sw.longTime());
	}

}
