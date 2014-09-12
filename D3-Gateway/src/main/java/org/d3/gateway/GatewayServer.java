package org.d3.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import org.d3.server.Server;
import org.d3.thread.NamedThreadFactory;
import org.springframework.stereotype.Component;

@Component
public class GatewayServer implements Server{
	
	private ServerBootstrap b;
	private NioEventLoopGroup boss;
	private NioEventLoopGroup worker;

	@Override
	public void start() {
		try {
			
			boss = new NioEventLoopGroup(2, new NamedThreadFactory("D3-Gateway-BOSS"));
			worker = new NioEventLoopGroup(2, new NamedThreadFactory("D3-Gateway-WORKER"));
			
			b = new ServerBootstrap();
			b.group(boss, worker)
			 .channel(NioServerSocketChannel.class)
			 .handler(new LoggingHandler())
			 .childHandler(new GatewayServerInitor())
			 .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true);
			
			Channel serverChannel = b.bind(21060).sync().channel();
//			ALL_CHANNELS.add(serverChannel);
			
		} catch (Exception e) {
//			LOG.error("Server not start");
		}
	}
	
	public static void main(String...strings){
		Server server = new GatewayServer();
		server.start();
	}

	@Override
	public void stop() {
		boss.shutdownGracefully();
		worker.shutdownGracefully();
	}

}
