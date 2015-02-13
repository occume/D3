package org.d3.demo.pattern;

public class Singleton {
	
	private static class Holder{
//		static Singleton instance = new Singleton();
	}
	
	private static Singleton instance = new Singleton(); 
	
	public static Singleton instance(){
		return instance;
	}
	
	private Singleton(){
		System.out.println("constructor");
	}

	public static void main(String[] args) {
//		Singleton.instance();
	}

}
