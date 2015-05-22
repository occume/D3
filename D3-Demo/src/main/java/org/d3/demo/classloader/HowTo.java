package org.d3.demo.classloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import sun.reflect.Reflection;

public class HowTo {

	public static void main(String[] args) throws ClassNotFoundException, MalformedURLException {
		
//		ClassLoader classLoader = 
//				new URLClassLoader(new URL[]{new URL("file:D:/Workspaces2014/D3/D3-Tomcat/target/D3-Tomcat-0.0.1.jar")}, ClassLoader.getSystemClassLoader());
//		ClassLoader classLoader1 = new URLClassLoader(new URL[]{}, Thread.currentThread().getContextClassLoader());
//		
//		System.out.println(Thread.currentThread().getContextClassLoader());
//		System.out.println(classLoader);
//		System.out.println(classLoader.getParent());
////		System.out.println(classLoader.loadClass("org.d3.valves.TestValve"));
//		Thread.currentThread().setContextClassLoader(classLoader);
//		new Thread(new Runnable(){
//			
//			@Override
//			public void run() {
//				ClassLoader c = Thread.currentThread().getContextClassLoader();
//				System.out.println(c);
//			}
//		})
//		.start();
		
		ClassLoader j = String.class.getClassLoader();
        System.out.println(j);
	}
}
