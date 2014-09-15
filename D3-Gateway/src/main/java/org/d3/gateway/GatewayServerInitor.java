package org.d3.gateway;

import java.net.InetSocketAddress;

import org.d3.gateway.report.ReportHandler;
import org.d3.gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

@Component
public class GatewayServerInitor extends ChannelInitializer<SocketChannel> {

	@Autowired
	private AuthService authService;
	@Autowired
	private StringDecoder stringDecoder;
	@Autowired
	private StringEncoder stringEncoder;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		InetSocketAddress addr = ch.remoteAddress();
		if(!authService.inBlackList(addr.getHostString())){
			reportNodeConnect(ch);
		}
		else{
			ch.writeAndFlush(99).addListener(ChannelFutureListener.CLOSE);
		}
	}

	private void reportNodeConnect(SocketChannel ch) {
		ch.pipeline().addLast(stringDecoder);
		ch.pipeline().addLast(stringEncoder);
		ch.pipeline().addLast(new ReportHandler());
//		ch.pipeline().remove(this);
	}

}
