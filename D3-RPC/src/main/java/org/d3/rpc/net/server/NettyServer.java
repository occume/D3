package org.d3.rpc.net.server;

import org.d3.rpc.net.handler.RequestDecoder;
import org.d3.rpc.net.handler.RequestHandler;
import org.d3.rpc.net.handler.ResponseEncoder;
import org.d3.rpc.net.handler.exception.ExceptionHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
	
	public void start(){
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(boss, worker)
			 .channel(NioServerSocketChannel.class)
			 .handler(new ChannelInitializer<ServerSocketChannel>() {

				@Override
				protected void initChannel(ServerSocketChannel ch) throws Exception {
					System.out.println(Thread.currentThread().getName());
//					throw new RuntimeException("get out 111");
				}
				
			})
			 .childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new RequestDecoder());
					ch.pipeline().addLast(new ResponseEncoder());
					ch.pipeline().addLast(new ExceptionHandler());
					ch.pipeline().addLast(new RequestHandler());
				}
				
			})
			 .option(ChannelOption.SO_BACKLOG, 128)
//			 .option(ChannelOption.SO_RCVBUF, 1024 * 8)
			 .childOption(ChannelOption.SO_RCVBUF, 1)
			 .childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture f = b.bind(10086);
//			try {
//				f.channel().closeFuture().sync();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
		}finally{
//			boss.shutdownGracefully();
//			worker.shutdownGracefully();
		}
	}
	
}
