package org.d3.demo.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.d3.std.Printer;

public class Server {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		start();
	}
	
	public static void start() throws IOException, InterruptedException{
		ServerSocket server = new ServerSocket(8888, 1);
		Socket conn = server.accept();
		
		byte[] buf = new byte[128];
		conn.getInputStream().read(buf);
		Printer.printByteArray(buf);
		conn.getOutputStream().write(127);
		conn.getInputStream().read(buf);
		Printer.printByteArray(buf);
		Thread.sleep(5000);
		
//		conn.close();
		server.close();
	}
}
