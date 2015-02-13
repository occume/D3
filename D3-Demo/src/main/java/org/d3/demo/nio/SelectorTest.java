package org.d3.demo.nio;

import java.io.IOException;
import java.nio.channels.Selector;

public class SelectorTest {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		int maxSize = 5;
		final Selector[] selectors = new Selector[maxSize];
		
		for(int i = 0; i < maxSize; i++){
			selectors[i] = Selector.open();
//			selectors[i].wakeup();
			System.out.println(selectors[i]);
		}
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				selectors[0].wakeup();
			}})
		.start();
		
		selectors[0].select();
		System.out.println("wakeup");
	}

}
