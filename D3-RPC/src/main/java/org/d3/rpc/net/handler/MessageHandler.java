package org.d3.rpc.net.handler;

import java.lang.reflect.Method;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

import org.d3.rpc.net.bean.Message;
import org.d3.rpc.net.bean.MethodEntry;
import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.bean.Response;
import org.d3.rpc.net.bean.ServiceEntry;
import org.d3.rpc.net.channel.D3Channel;
import org.d3.rpc.net.node.Node;
import org.d3.rpc.util.Reflections;

public class MessageHandler extends SimpleChannelInboundHandler<Message> {
	
	private D3Channel d3channel;
	
	private Node node;
	
	public MessageHandler(Node node, D3Channel d3channel){
		this.node = node;
		this.d3channel = d3channel;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg)
			throws Exception {
		
		if(msg instanceof Request){
			handleRequest(ctx, (Request) msg);
		}
		else if(msg instanceof Response){
			handleResponse(ctx, (Response)msg);
		}
	}

	private void handleRequest(ChannelHandlerContext ctx, Request request) throws Exception{
		
		String serviceName = request.getServiceName();
		MethodEntry methodEntry = request.getMethodEntry();
		System.out.println("methodEntry: " + methodEntry);
		ServiceEntry serviceEntry = node.getService(serviceName);
		Object service = serviceEntry.getService();
		
		String paramTypes = methodEntry.getParamTypes();
		String methodName = methodEntry.getName();
		Method method = node.getMethod(methodName);
		
		if(method == null){
			method = Reflections.getByParameter(service.getClass(), paramTypes);
			node.putMethod(methodName, method);
		}
		
		Object result = method.invoke(service, methodEntry.getArgs());
		Response response = new Response(request.getId(), result);
		ctx.writeAndFlush(response);
		
	}
	
	private void handleResponse(ChannelHandlerContext ctx, Response response){
		Promise<Object> promise = d3channel.getPromise(response.getId());
		promise.setSuccess(response.getResult());
	}
	
}
