package org.d3.demo.netty.file;

import java.io.File;
import java.io.FileInputStream;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

public class FileServer {

	public static void main(String[] args) {
		
		NioEventLoopGroup worker = new NioEventLoopGroup();
		NioEventLoopGroup boss	 = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(boss, worker)
			 .channel(NioServerSocketChannel.class)
			 .handler(new LoggingHandler(LogLevel.INFO))
			 .childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(
//							new StringEncoder(CharsetUtil.ISO_8859_1),
//							new LineBasedFrameDecoder(8192),
//							new StringDecoder(CharsetUtil.UTF_8),
							new FileHandler()
					);
				}
				
			});
			
			ChannelFuture f = b.bind(8081).sync();
			
			f.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			worker.shutdownGracefully();
			boss.shutdownGracefully();
		}
		
	}
	
	private static final class FileHandler extends SimpleChannelInboundHandler<String>{

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, String msg)
				throws Exception {
//			System.out.println(msg);
			
//			ByteBuf buf = ctx.alloc().buffer();
//			System.out.println(buf.getClass());
//			System.out.println(buf.alloc().getClass());
//			
//			buf.release();
//			System.out.println(buf.refCnt());
//			
//			buf.writeLong(0xdeadbeef);
//			System.out.println(buf.readableBytes());
//			ctx.writeAndFlush(buf);
			
			File file = new File("d:/log/data.txt");
			
			if(file.exists()){
				if(!file.isFile()){
					System.out.println("Not a file: " + file + "\n");
				}
				ctx.write(file + " " + file.length() + "\n");
				FileInputStream fis = new FileInputStream(file);
				FileRegion region = new DefaultFileRegion(fis.getChannel(), 0, file.length());
				
				ctx.write(region);
				ctx.writeAndFlush("\n");
				fis.close();
			}
			else{
				ctx.writeAndFlush("File not found: " + file + "\n");
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
		}
		
	}

}
