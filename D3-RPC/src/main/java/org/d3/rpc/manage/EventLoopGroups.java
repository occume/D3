package org.d3.rpc.manage;

import org.d3.thread.NamedThreadFactory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;


public class EventLoopGroups {
	
	private static EventLoopGroup defaultEventLoopGroup = new NioEventLoopGroup(100, new NamedThreadFactory("GPS_DFT"));
	
	public static EventLoopGroup defaultEventLoopGroup(){
		return defaultEventLoopGroup;
	}
	
	public static void closeDefaultEventLoopGroup(){
		defaultEventLoopGroup.shutdownGracefully();
	}
	
	public static EventLoopGroup newEventLoopGroup(int threads, String name){
		return new NioEventLoopGroup(threads, new NamedThreadFactory(name));
	}
	
	public static EventLoopGroup cpuCountEventLoopGroup(String name){
		return new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new NamedThreadFactory(name));
	}
}
