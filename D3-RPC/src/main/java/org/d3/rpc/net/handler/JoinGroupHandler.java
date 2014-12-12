package org.d3.rpc.net.handler;

import java.util.concurrent.CountDownLatch;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.d3.rpc.manage.World;
import org.d3.rpc.net.bean.JoinGroupRequest;
import org.d3.rpc.net.bean.JoinGroupResponse;
import org.d3.rpc.net.bean.Message;
import org.d3.rpc.net.channel.D3Channel;
import org.d3.rpc.net.node.Node;
import org.d3.rpc.net.node.SimpleNode.NodeType;
import org.d3.rpc.util.IdGenerator;
import org.d3.rpc.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinGroupHandler extends SimpleChannelInboundHandler<Message> {
	
	private D3Channel d3channel;
	
	private Node node;
	
	private CountDownLatch latch;
	
	private static Logger LOG = LoggerFactory.getLogger(JoinGroupHandler.class);
	
	public JoinGroupHandler(Node node){
		this.node = node;
	}
	
	public JoinGroupHandler(CountDownLatch latch, Node node){
		this.node = node;
		this.latch = latch;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		NodeType type = node.type();
		d3channel = new D3Channel(IdGenerator.autoIncrementId(), ctx.channel());
		switch(type){
			case CLIENT:
				askJoinGroup();
				break;
			case SERVER:
				repJoinGroup();
				break;
		}
		
	}
	
	private void repJoinGroup() {
		World.addChannel(d3channel);
		World.defaultChannelGroup().add(d3channel);
	}

	private void askJoinGroup(){
		LOG.debug("ask join group: {}", NetUtil.localHostName());
		World.addChannel(d3channel);
		World.defaultChannelGroup().add(d3channel);
		JoinGroupRequest msg = new JoinGroupRequest(NetUtil.localHostName());
		d3channel.getChannel().writeAndFlush(msg);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg)
			throws Exception {
		LOG.debug("joinGroupHandler: {}", node.type());
		if(msg instanceof JoinGroupRequest){
			JoinGroupRequest message = (JoinGroupRequest) msg;
			node.add2Group(message.getGroupName(), d3channel);
			JoinGroupResponse response = new JoinGroupResponse(message.getGroupName());
			ctx.channel().writeAndFlush(response);
			LOG.debug("response join group ask: {}", message.getGroupName());
		}
		else if(msg instanceof JoinGroupResponse){
			JoinGroupResponse message = (JoinGroupResponse) msg;
			node.add2Group(message.getGroupName(), d3channel);
			latch.countDown();
		}
		else{
			
		}
		ctx.pipeline().addLast(new MessageHandler(node, d3channel));
		ctx.pipeline().remove(this);
	}
	
}
