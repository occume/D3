package org.d3.service.auto;

import org.d3.rpc.net.service.ServiceRegistry;

public class ServiceRegistryTest {

	public static void main(String[] args) throws InterruptedException {
		ServiceRegistry r = new ServiceRegistry("127.0.0.1");
		r.register("127.0.0.1:28256");
		r.register("192.168.0.1:28257");
		Thread.sleep(100000);
	}

}
