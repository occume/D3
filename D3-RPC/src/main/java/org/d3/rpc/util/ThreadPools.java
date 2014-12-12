package org.d3.rpc.util;

import org.d3.thread.NamedThreadFactory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;


public class ThreadPools {
	
	private static class DefaultEventLoopGroupHolder{
		static EventLoopGroup defaultEventLoopGroup = new NioEventLoopGroup(10, new NamedThreadFactory("RPC_DFT"));
	}
	
	private static class DefaultSingleEventLoopGroupHolder{
		static EventLoopGroup defaultSingleEventLoopGroup = new NioEventLoopGroup(1, new NamedThreadFactory("RPC_DFT_SINGLE"));
	}
	
	public static EventLoopGroup defaultEventLoopGroup(){
		return DefaultEventLoopGroupHolder.defaultEventLoopGroup;
	}
	
	public static void closeDefaultEventLoopGroup(){
		DefaultEventLoopGroupHolder.defaultEventLoopGroup.shutdownGracefully();
	}
	
	public static EventLoopGroup singleEventLoopGroup(){
		return DefaultSingleEventLoopGroupHolder.defaultSingleEventLoopGroup;
	}
	
	public static void closeDefaultSingleEventLoopGroup(){
		DefaultSingleEventLoopGroupHolder.defaultSingleEventLoopGroup.shutdownGracefully();
	}
	
	public static EventLoopGroup newEventLoopGroup(int threads, String name){
		return new NioEventLoopGroup(threads, new NamedThreadFactory(name));
	}
	
	public static EventLoopGroup cpuCountEventLoopGroup(String name){
		return new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new NamedThreadFactory(name));
	}
}
