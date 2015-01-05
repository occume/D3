package org.d3.demo.performance;

import org.d3.std.Stopwatch;

public class LocalAndInstance {

	public static void main(String[] args) {
		
		long loop = 1000000000;
		
		Stopwatch sw = Stopwatch.newStopwatch();
		for(int i = 0; i < loop; i++){
			test();
		}
		System.out.println(sw.longTime());
		
		Stopwatch sw1 = Stopwatch.newStopwatch();
		for(int i = 0; i < loop; i++){
			test1();
		}
		System.out.println(sw1.longTime());
	}
	
	static int[] a = {1, 2, 3};
	
	static void test(){
		int[] a1 = a;
		
		int temp = 0;
		for(int i = 0; i < a1.length; i++){
//			System.out.println(a1[i]);
			temp = a1[i];
		}
		temp = temp + 1;
	}

	static void test1(){
		int temp = 0;
		for(int i = 0; i < a.length; i++){
//			System.out.println(a[i]);
			temp = a[i];
		}
		temp = temp + 1;
	}
}
