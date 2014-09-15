package org.d3.gateway.report;

import org.springframework.stereotype.Component;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
@Component
public class ReportHandler extends SimpleChannelInboundHandler<String>{
	
	
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg)
			throws Exception {
		System.out.println(msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("active");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
	}

}
