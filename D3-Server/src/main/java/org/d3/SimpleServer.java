package org.d3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

public class SimpleServer {

	public static void main(String[] args) throws Exception {
		
		EventLoopGroup boss = new OioEventLoopGroup();
		EventLoopGroup worker = new OioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(boss, worker)
			 .channel(OioServerSocketChannel.class)
			 .childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new SimpleHandler());
				}
				
			})
			 .option(ChannelOption.SO_BACKLOG, 128)
			 .childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture f = b.bind(8080).sync();
			f.channel().closeFuture().sync();
			
		}finally{
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
		
	}

}
