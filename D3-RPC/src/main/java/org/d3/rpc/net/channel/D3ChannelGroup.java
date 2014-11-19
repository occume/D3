package org.d3.rpc.net.channel;

public interface D3ChannelGroup {
	
	public String name();

	public D3Channel next();
	
	public void close();
	
	public boolean add(D3Channel e);
	
	public int size();
}
