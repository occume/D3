package org.d3.demo.concurrent.thread;

public class InterruptTest {

	public static void main(String[] args) {
		
		Thread.interrupted();
		System.out.println(Thread.currentThread().isInterrupted());
		Thread.currentThread().interrupt();
		Thread.interrupted();
		System.out.println(Thread.currentThread().isInterrupted());
	}

}
