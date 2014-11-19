package org.d3.client;

import org.d3.rpc.annotion.RemoteService;

@RemoteService
public interface Test {
	
	public void test();
	
	public String testString();
}
