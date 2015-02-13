package org.d3.demo.concurrent.lock;

import java.util.concurrent.atomic.AtomicReference;
/**
 * 	CAS操作需要硬件的配合
	保证各个CPU的缓存（L1、L2、L3、跨CPU Socket、主存）的数据一致性，通讯开销很大，在多处理器系统上更严重
	没法保证公平性，不保证等待进程/线程按照FIFO顺序获得锁
 * 
 * @author d_jin
 *
 */

public class SpinLock {
	
	private AtomicReference<Thread> owner = new AtomicReference<>();
	
	public void lock(){
		
		Thread curr = Thread.currentThread();
		while(owner.compareAndSet(null, curr)){}
	}
	
	public void unlock(){
		
		Thread curr = Thread.currentThread();
		owner.compareAndSet(curr, null);
	}

	public static void main(String[] args) {
		
	}

}
