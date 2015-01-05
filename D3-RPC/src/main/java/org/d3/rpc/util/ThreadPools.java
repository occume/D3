package org.d3.rpc.util;

import java.util.ArrayList;
import java.util.List;

import org.d3.thread.NamedThreadFactory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;


public class ThreadPools {
	
	private static List<EventLoopGroup> allPool = new ArrayList<>();
	
	private static class DefaultPool10Holder{
		static EventLoopGroup defaultPool10 = new NioEventLoopGroup(10, new NamedThreadFactory("RPC_DFT_10"));
	}
	
	private static class MessagePoolHolder{
		static EventLoopGroup messagePool = new NioEventLoopGroup(20, new NamedThreadFactory("D3-MESSAGE"));
	}
	
	public static EventLoopGroup defaultPool10(){
		return DefaultPool10Holder.defaultPool10;
	}
	
	public static void closePool10(){
		DefaultPool10Holder.defaultPool10.shutdownGracefully();
	}
	
	public static EventLoopGroup messagePool(){
		return MessagePoolHolder.messagePool;
	}
	
	public static void closeMessagePool(){
		MessagePoolHolder.messagePool.shutdownGracefully();
	}
	
	public static EventLoopGroup newPool(int threads, String name){
		return new NioEventLoopGroup(threads, new NamedThreadFactory(name));
	}
	
	public static EventLoopGroup cpuCountPool(String name){
		return new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new NamedThreadFactory(name));
	}
	
	public static void shutDown(){
		closePool10();
		closeMessagePool();
	}
}
