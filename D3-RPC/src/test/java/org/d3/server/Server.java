package org.d3.server;

import org.d3.rpc.net.server.Engine;
import org.d3.rpc.net.server.SimpleEngine;

public class Server {

	public static void main(String[] args) {
		
		Engine engine = SimpleEngine.instance();
		engine.launch();
		engine.registerService(new TestService());
		
		System.out.println(engine.getAllServices());
	}

}
