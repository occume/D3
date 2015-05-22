/***
 * Excerpted from "Seven Concurrency Models in Seven Weeks",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
***/
package org.d3.demo.concurrent.thread;

import java.util.Random;

public class DiningPhilosophers {

  public static void main(String[] args) throws InterruptedException {
    Philosopher[] philosophers = new Philosopher[5];
    Chopstick[] chopsticks = new Chopstick[5];
    
    for (int i = 0; i < 5; ++i)
      chopsticks[i] = new Chopstick(i);
    for (int i = 0; i < 5; ++i) {
      philosophers[i] = new Philosopher(chopsticks[i], chopsticks[(i + 1) % 5]);
      philosophers[i].start();
    }
    for (int i = 0; i < 5; ++i)
      philosophers[i].join();
  }
  
  static class Philosopher extends Thread {
	  private Chopstick left, right;
	  private Random random;
	  private int thinkCount;

	  public Philosopher(Chopstick left, Chopstick right) {
	    this.left = left; this.right = right;
	    random = new Random();
	  }

	  public void run() {
	    try {
	      while(true) {
	        ++thinkCount;
	        if (thinkCount % 10 == 0)
	          System.out.println("Philosopher " + this + " has thought " + thinkCount + " times");
	        Thread.sleep(random.nextInt(1000));     // Think for a while
	        synchronized(left) {                    // Grab left chopstick 
	          synchronized(right) {                 // Grab right chopstick 
	            Thread.sleep(random.nextInt(1000)); // Eat for a while
	          }
	        }
	      }
	    } catch(InterruptedException e) {}
	  }
	}
  
   static class Chopstick {
		private int id;
		public Chopstick(int id) { this.id = id; }
	  public int getId() { return id; }
	}
}
