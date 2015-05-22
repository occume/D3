package org.d3.demo.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.d3.std.Generator;
import org.d3.std.Printer;


public class Client {

	public static void main(String...strings) throws InterruptedException{
		
		try {
			Socket conn = new Socket("localhost", 8888);
			conn.setSendBufferSize(1024);
			byte[] buf = Generator.byteArray(1024);
			
//			conn.getInputStream().read(buf);
//			buf = new byte[1024];
//			conn.getInputStream().read(buf);
			while(true){
				conn.getOutputStream().write(buf);
				System.out.println("111");
				Thread.sleep(100);
			}
//			conn.close();
//			conn.getOutputStream().close();
//			conn.getInputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Thread.sleep(3000);
	}
}
