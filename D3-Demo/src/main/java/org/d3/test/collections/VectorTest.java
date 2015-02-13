package org.d3.test.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.d3.std.Generator;
import org.d3.std.Stopwatch;

public class VectorTest {
	static volatile  int number = 0;
	public static void main(String[] args) {
		
//		Vector<String> v = new Vector<>(Generator.stringCollection(1000000));
		List<String> v = new ArrayList<>(Generator.stringCollection(1000000));
		
		for(int i = 0; i < 10; i++){
			volatileTest();
		}
		
	}
	
	private static void volatileTest(){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Stopwatch sw = Stopwatch.newStopwatch();
				
				for(int i = 0; i < 10000000; i++){
					number = i;
				}
				
				System.out.print(Thread.currentThread().getName() + " cost:");
				System.out.println(sw.longTime());
			}
		});
		t.start();
	}
	
	private static void newThread(final List<String> v){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Stopwatch sw = Stopwatch.newStopwatch();
				synchronized(v) {
				for(int i = 0; i < v.size(); i++){
					process(v.get(i));
				}
				}
				System.out.print(Thread.currentThread().getName() + " cost:");
				System.out.println(sw.longTime());
			}
		});
		t.start();
	}

	private static void process(String string) {
		
	}

}
