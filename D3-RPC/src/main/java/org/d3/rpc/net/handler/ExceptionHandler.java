package org.d3.rpc.net.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ExceptionHandler extends ChannelHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
//		cause.printStackTrace();
		System.err.println(cause.getMessage());
		ctx.close();
//		ctx.fireChannelInactive();
	}

}
