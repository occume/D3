package org.d3.rpc.net.handler;

import org.d3.rpc.manage.World;
import org.d3.rpc.net.bean.Response;
import org.d3.rpc.net.bean.ServiceEntry;
import org.d3.rpc.net.channel.D3Channel;
import org.d3.rpc.net.server.Box;
import org.d3.rpc.util.IdGenerator;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

public class ResponseHandler extends SimpleChannelInboundHandler<Response> {

	private D3Channel d3channel;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Response response)
			throws Exception {
		
		Promise<Object> promise = d3channel.getPromise(1);
		promise.setSuccess(response.getResult());
		
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		d3channel = new D3Channel(IdGenerator.autoIncrementId(), ctx.channel());
		World.addChannel(d3channel);
		World.defaultChannelGroup().add(d3channel);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

}
