package org.d3.rpc.net.handler;

import org.d3.rpc.manage.World;
import org.d3.rpc.net.channel.D3Channel;
import org.d3.rpc.util.IdGenerator;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

public class StringRequestHandler extends SimpleChannelInboundHandler<String> {
	
	private D3Channel d3channel;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)
			throws Exception {
		System.out.print(d3channel.promiseSize());
		Promise<Object> promise = d3channel.getPromise(1);
		promise.setSuccess(msg);
		System.out.println(": " + msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		d3channel = new D3Channel(IdGenerator.autoIncrementId(), ctx.channel());
//		World.addChannel(d3channel);
//		World.defaultChannelGroup().add(d3channel);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

}
