package org.d3.demo.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class HowTo {

	public static void main(String[] args) throws ClassNotFoundException {
		
		ClassLoader classLoader = new URLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());
		ClassLoader classLoader1 = new URLClassLoader(new URL[]{}, Thread.currentThread().getContextClassLoader());
		
		System.out.println(
				classLoader.loadClass("org.d3.demo.classloader.HowTo") == classLoader1.loadClass("org.d3.demo.classloader.HowTo"));
		System.out.println(classLoader1.loadClass("java.lang.Integer"));
		
	}

}
