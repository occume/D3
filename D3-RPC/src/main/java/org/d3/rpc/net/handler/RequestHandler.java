package org.d3.rpc.net.handler;

import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.bean.Response;
import org.d3.rpc.net.bean.ServiceEntry;
import org.d3.rpc.net.server.Box;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RequestHandler extends SimpleChannelInboundHandler<Request> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Request request)
			throws Exception {
		System.out.println("server request: " + request);
		
		String serviceName = request.getServiceName();
		String methodName = request.getMethodName();
		
		ServiceEntry serviceEntry = Box.instance().getService(serviceName);
		Object service = serviceEntry.getService();
		System.out.println(service);
		
		TypeToken tt = TypeToken.of(service.getClass());
		Invokable invok = tt.method(service.getClass().getMethod(methodName));
		Object result = invok.invoke(service);
		
		Response response = new Response(request.getId(), result);
		ctx.writeAndFlush(response);
	}

}
