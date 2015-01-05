package org.d3.demo.httpclient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.d3.std.Stopwatch;

public class HttpClientTest {

	public static void main(String...strings) throws InterruptedException {
		
		int loops = 10000;
		CountDownLatch latch = new CountDownLatch(loops);
		ExecutorService pool = Executors.newFixedThreadPool(100);
		
		Stopwatch sw = Stopwatch.newStopwatch();
		
		for(int i = 0; i < loops; i++){
			pool.execute(new RequestTask(latch));
		}
		
		latch.await();
		System.out.println(sw.longTime());
	}

	private static class RequestTask implements Runnable{
		private CountDownLatch latch;
		public RequestTask(CountDownLatch latch){
			this.latch = latch;
		}
		@Override
		public void run() {
			String result = HttpClientUtil.doGet("http://z.cn");
			latch.countDown();
		}
	}
}
