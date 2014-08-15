package org.d3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
//		ByteBuf buf = ctx.alloc().buffer();
//		System.out.println(ctx.alloc());
		
//		buf.release();
//		buf.retain();
//		System.out.println(buf.refCnt());
//		System.out.println(msg);
//		buf.writeByte((byte)2);
		
//		ctx.writeAndFlush(buf);
		System.out.println(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		System.out.println(123);
	}

}
