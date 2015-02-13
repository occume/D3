package org.d3.service.auto;

import org.d3.rpc.net.service.ServiceDiscovery;

public class ServiceDiscoveryTest {

	public static void main(String[] args) throws InterruptedException {
		ServiceDiscovery d = new ServiceDiscovery("127.0.0.1");
		String add = d.discover();
		System.out.println(add);
		
		Thread.sleep(100000);
	}

}
