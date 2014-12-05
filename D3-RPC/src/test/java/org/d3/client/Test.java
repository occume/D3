package org.d3.client;

import java.util.List;

import org.d3.rpc.annotion.RemoteService;

@RemoteService
public interface Test {
	
	public void test();
	
	public String testString();
	
	public List<String> order(List<String> list);
}
