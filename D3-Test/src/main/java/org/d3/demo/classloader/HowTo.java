package org.d3.demo.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class HowTo {

	public static void main(String[] args) throws ClassNotFoundException {
		
		ClassLoader classLoader = new URLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader().getParent());
		ClassLoader classLoader1 = new URLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader().getParent());
		
		System.out.println(classLoader.loadClass("java.lang.Integer") == classLoader1.loadClass("java.lang.Integer"));
		System.out.println(classLoader1.loadClass("java.lang.Integer"));
		
	}

}