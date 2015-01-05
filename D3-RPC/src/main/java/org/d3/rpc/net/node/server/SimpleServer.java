package org.d3.rpc.net.node.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.d3.rpc.net.codec.LengthBasedDecoder;
import org.d3.rpc.net.codec.LengthBasedEncoder;
import org.d3.rpc.net.handler.ExceptionHandler;
import org.d3.rpc.net.handler.JoinGroupHandler;
import org.d3.rpc.net.node.SimpleNode;
import org.d3.rpc.util.ThreadPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleServer extends SimpleNode implements Server{
	
	private EventLoopGroup boss;
	
	private static final int PORT = 28256;
	
	private static Logger LOG = LoggerFactory.getLogger(SimpleServer.class);
	
	public SimpleServer(){
		boss = ThreadPools.cpuCountPool("D3-RPC-BOSS");
	}
	@Override
	public void start() {
		
//		try {
			ServerBootstrap b = new ServerBootstrap();
			
			b.group(boss, getWorker())
			 .channel(NioServerSocketChannel.class)
			 .childHandler(initor())
			 .option(ChannelOption.SO_BACKLOG, 128)
			 .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			 .childOption(ChannelOption.SO_RCVBUF, 1)
			 ;
			b.bind(PORT);
			LOG.info("RPC Server listen at: {}", PORT);
//		}
//		finally{
//		//		boss.shutdownGracefully();
//		//		worker.shutdownGracefully();
//		}
	}
	
	private ChannelInitializer<SocketChannel> initor(){
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
//				ch.pipeline().addLast(new RequestDecoder());
//				ch.pipeline().addLast(new ResponseEncoder());
				ch.pipeline().addLast(new LengthBasedEncoder());
				ch.pipeline().addLast(new LengthBasedDecoder());
				ch.pipeline().addLast(new ExceptionHandler());
				ch.pipeline().addLast(new JoinGroupHandler(SimpleServer.this));
//				ch.pipeline().addLast(new RequestHandler());
			}
			
		};
	}

	@Override
	public void shutDown() {
		
	}

	@Override
	public NodeType type() {
		return NodeType.SERVER;
	}
	@Override
	public boolean ready() {
		return false;
	}

	
}
