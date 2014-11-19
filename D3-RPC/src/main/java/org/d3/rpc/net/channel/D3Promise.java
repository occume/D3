package org.d3.rpc.net.channel;

import io.netty.util.concurrent.DefaultPromise;

public class D3Promise extends DefaultPromise<Object>{

	private D3Channel channel;
	
	public D3Promise(D3Channel channel){
		super(channel.getChannel().eventLoop());
		this.channel = channel;
	}
	
}
