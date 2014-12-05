package org.d3.rpc.net.handler;

import java.lang.reflect.Method;
import org.d3.rpc.net.bean.MethodEntry;
import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.bean.Response;
import org.d3.rpc.net.bean.ServiceEntry;
import org.d3.rpc.net.server.SimpleEngine;
import org.d3.rpc.util.Reflections;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RequestHandler extends SimpleChannelInboundHandler<Request> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Request request)
			throws Exception {
		
		String serviceName = request.getServiceName();
		MethodEntry methodEntry = request.getMethodEntry();
		
		ServiceEntry serviceEntry = SimpleEngine.instance().getService(serviceName);
		Object service = serviceEntry.getService();
		
		String paramTypes = methodEntry.getParamTypes();
		Method method = SimpleEngine.instance().getMethod(paramTypes);
		
		
		if(method == null){
			method = Reflections.getByParameter(service.getClass(), paramTypes);
			SimpleEngine.instance().putMethod(paramTypes, method);
		}
		
		Object result = method.invoke(service, methodEntry.getArgs());
		Response response = new Response(request.getId(), result);
		ctx.writeAndFlush(response);
	}

}
