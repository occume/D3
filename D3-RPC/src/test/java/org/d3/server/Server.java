package org.d3.server;

import org.d3.rpc.net.server.Box;

public class Server {

	public static void main(String[] args) {
		
		Box box = Box.instance();
		box.start();
		box.register(new TestService());
		
		System.out.println(box.getAllServices());
	}

}
