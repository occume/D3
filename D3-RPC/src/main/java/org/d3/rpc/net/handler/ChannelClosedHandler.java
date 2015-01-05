package org.d3.rpc.net.handler;

import org.d3.rpc.net.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ChannelClosedHandler extends ChannelInboundHandlerAdapter {
	
	private Node node;
	
	private static Logger LOG = LoggerFactory.getLogger(ChannelClosedHandler.class);
	
	public ChannelClosedHandler(Node node){
		this.node = node;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOG.error("connection inactive: {}", ctx.channel().remoteAddress());
	}

}
