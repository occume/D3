package org.d3.demo.concurrent.lock;

import java.util.concurrent.atomic.AtomicReference;

import org.d3.std.Stopwatch;

public class CLHLock {
	
	private static class Node{
		volatile boolean locked;
	}
	
	private final AtomicReference<Node> tail;
	private final ThreadLocal<Node> prev;
	private final ThreadLocal<Node> self;
	
	public CLHLock(){
		tail = new AtomicReference<CLHLock.Node>(new Node());
		self = new ThreadLocal<Node>(){
			@Override
			protected Node initialValue() {
				return new Node();
			}
		};
		prev = new ThreadLocal<>();
	}
	
	public void lock(){
		Node node = self.get();
		node.locked = true;
//		Node p = prev.get();
		Node p = tail.getAndSet(node);
		prev.set(p);
		
		while(p.locked){}
	}
	
	public void unlock(){
		Node node = self.get();
		node.locked = false;
		self.set(prev.get());
	}

	public static void main(String[] args) {
		
		Thread[] threads = new Thread[5];
		CLHLock lock = new CLHLock();
		Task task = new Task(lock);
		
		for(int i = 0; i < threads.length; i++){
			threads[i] = new Thread(task);
		}
		
		for(int i = 0; i < threads.length; i++){
			threads[i].start();
		}
	}
	
	private static class Task implements Runnable{
		
		private CLHLock lock;
		private int count;
		
		public Task(CLHLock lock){
			this.lock = lock;
		}
		@Override
		public void run() {
			Stopwatch sw = Stopwatch.newStopwatch();
			lock.lock();
			
			for(int i = 0; i < 100000; i++){
				count++;
			}
			System.out.println(Thread.currentThread().getName() + " " + count + "; cost " + sw.longTime());
			lock.unlock();
		}
	}

}
