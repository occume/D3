package org.d3.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.d3.std.Stopwatch;

public class BufferTest {
	
	private static volatile ByteBuffer bb;
	private static volatile int need;

	public static void main(String[] args) {
		
		Stopwatch sw = Stopwatch.newStopwatch();
		for(int i = 0; i < 10000000; i++){
			bb = ByteBuffer.allocate(10);
//			ByteBuffer.allocateDirect(10);
		}
		System.out.println(sw.longTime());
		
		Stopwatch sw1 = Stopwatch.newStopwatch();
		for(int i = 0; i < 10000000; i++){
			Random r = new Random();
			need = r.nextInt();
		}
		System.out.println(sw1.longTime());
		
		Stopwatch sw2 = Stopwatch.newStopwatch();
		for(int i = 0; i < 10000000; i++){
			need = ThreadLocalRandom.current().nextInt();
		}
		System.out.println(sw2.longTime());
	}

}
