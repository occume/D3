package org.d3.demo.concurrent.thread;

public class PrimitiveTest {

	public static void main(String[] args) throws InterruptedException {
		Thread myThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Hello from new Thread");
			}
		});
		
		myThread.start();
		Thread.yield();
//		Thread.sleep(1);
		System.out.println("Hello from main Thread");
		myThread.join();
	}

}
