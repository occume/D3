package org.d3.test.thread.synchronizition;

public class ContendedTest extends Thread {

	private static class DataHolder {
		private volatile long l1 = 0;
		private volatile long l2 = 0;
		private volatile long l3 = 0;
		private volatile long l4 = 0;
		@Override
		public String toString() {
			return "DataHolder [l1=" + l1 + ", l2=" + l2 + ", l3=" + l3
					+ ", l4=" + l4 + "]";
		}
		
	}

	private static DataHolder dh = new DataHolder();

	private static long nloops;

	public ContendedTest(Runnable r) {
		super(r);
	}

	public static void main(String[] args) throws InterruptedException {

		nloops = 10000000;
		ContendedTest[] tests = new ContendedTest[4];

		tests[0] = new ContendedTest(new Runnable() {
			@Override
			public void run() {
				for (long i = 0; i < nloops; i++) {
					dh.l1 += i;
				}
			}
		});
		tests[1] = new ContendedTest(new Runnable() {
			public void run() {
				for (long i = 0; i < nloops; i++) {
					dh.l2 += i;
				}
			}
		});
		tests[2] = new ContendedTest(new Runnable() {
			public void run() {
				for (long i = 0; i < nloops; i++) {
					dh.l3 += i;
				}
			}
		});
		tests[3] = new ContendedTest(new Runnable() {
			public void run() {
				for (long i = 0; i < nloops; i++) {
					dh.l4 += i;
				}
			}
		});

		long then = System.currentTimeMillis();
		for (ContendedTest ct : tests) {
			ct.start();
		}
		for (ContendedTest ct : tests) {
			ct.join();
		}
		long now = System.currentTimeMillis();
		System.out.println("Duration: " + (now - then) + " ms");
		System.out.println(dh);
	}

}
