package org.d3.rpc.net.handler;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.d3.std.Stopwatch;

public class MessageHandler extends SimpleChannelInboundHandler<Message> {
	
	private D3Channel d3channel;
	
	private Node node;
	
	static AtomicInteger count = new AtomicInteger();
	private static Stopwatch sw;
	
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
//		System.out.println(request);
		if(count.compareAndSet(0, 1)){
			sw = Stopwatch.newStopwatch();
		}
		else{
			count.incrementAndGet();
			if(count.compareAndSet(100000, 0)){
				System.out.println("it cost " + sw.longTime());
			}
		}
		
		
		String serviceName = request.getServiceName();
		MethodEntry methodEntry = request.getMethodEntry();
//		System.out.println("methodEntry: " + methodEntry);
		ServiceEntry serviceEntry = node.getService(serviceName);
		Object service = serviceEntry.getService();
		
		String paramTypes = methodEntry.getParamTypes();
		String methodName = methodEntry.getName();
		String compose = methodName + paramTypes;
		Method method = node.getMethod(compose);
		
		if(method == null){
			method = Reflections.getByNameParameter(service.getClass(), methodName, paramTypes);
			node.putMethod(compose, method);
		}
		
		Object result = method.invoke(service, methodEntry.getArgs());
		Response response = new Response(request.getId(), result);
		ctx.writeAndFlush(response);
		
	}
	
	private void handleResponse(ChannelHandlerContext ctx, Response response){
	
//		InvokeFuture<Object> future = d3channel.getFuture(response.getId());
//		if(future != null){
//			future.setResult(response.getResult());
//		}
		
		Promise<Object> promise = d3channel.getPromise(response.getId());
		if(promise != null){
			promise.setSuccess(response.getResult());
			node.removePromise(response.getId());
			node.receiveOkIncrement();
			node.release();
		}
	}
	
}
