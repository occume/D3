package org.d3.demo.netty.performance;

import org.d3.std.Stopwatch;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Zerocopy {

	public static void main(String[] args) {
		
		ByteBuf in = Unpooled.buffer(10000);
		
		int writeable = in.writableBytes();
		for(int i = 0; i < writeable; i++){
			in.writeByte((byte)i);
		}
		
		int loops = 1000000;
		Stopwatch sw = Stopwatch.newStopwatch();
//		Thread.dumpStack();
//		byte[] content = null;
		ByteBuf content = null;
		for(int i = 0; i < loops; i++){
//			content = new byte[writeable];
//			in.readBytes(content);
			in.readerIndex(0);
//			content = in.slice(0, writeable);
			content = in.readSlice(10000);
		}
		System.out.println(sw.longTime());
		System.out.println(content.getByte(999));
		
	}

}
