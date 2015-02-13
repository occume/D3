package org.d3.demo.concurrent.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class TicketLock {
	
	private AtomicInteger service = new AtomicInteger();
	private AtomicInteger ticket = new AtomicInteger();
	
	public int lock(){
		
		int myticket = ticket.getAndIncrement();
		while(service.get() != myticket){}
		
		return myticket;
	}
	
	public void unlock(int ticket){
		
		int next = ticket + 1;
		service.compareAndSet(ticket, next);
	}

	public static void main(String[] args) {
//		ReentrantLock
		AtomicReferenceFieldUpdater<TicketLock, AtomicInteger> updater = 
				AtomicReferenceFieldUpdater.newUpdater(TicketLock.class, AtomicInteger.class, "service");
//		updater.getAndSet(this, newValue);
		
		AtomicReference<Thread> owner = new AtomicReference<>();
		owner.getAndSet(Thread.currentThread());
	}
}
