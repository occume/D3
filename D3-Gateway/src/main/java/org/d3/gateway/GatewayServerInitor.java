package org.d3.gateway;


import java.net.InetSocketAddress;

import org.d3.gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class GatewayServerInitor extends ChannelInitializer<SocketChannel> {

	@Autowired
	private AuthService authService;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		InetSocketAddress addr = ch.remoteAddress();
		if(!authService.inBlackList(addr.getHostString())){
			System.out.println("pass");
		}
		else{
			ch.writeAndFlush(99).addListener(ChannelFutureListener.CLOSE);
		}
	}

}
