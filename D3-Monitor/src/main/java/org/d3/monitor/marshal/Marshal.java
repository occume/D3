package org.d3.monitor.marshal;

import org.d3.monitor.marshal.service.provider.BigdataClientWatcherProvider;
import org.d3.monitor.marshal.service.provider.ReportProvider;
import org.d3.rpc.net.node.server.SimpleServer;

public class Marshal {
	
	public static void main(String...strings){
		
		SimpleServer server = new SimpleServer();
		server.start();
		
		server.registerService(new ReportProvider());
		server.registerService(new BigdataClientWatcherProvider());
		System.out.println(server.getAllServices());
	}
	
}
