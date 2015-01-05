package org.d3.rpc.net.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Promise;

import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.channel.D3Channel;
import org.d3.rpc.net.channel.D3ChannelGroup;
import org.d3.rpc.net.channel.D3Promise;
import org.d3.rpc.net.node.Node;
import org.d3.rpc.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步请求
 * @author d_jin
 *
 */
public class SyncAccessor extends BaseAccessor{
	
	private D3Channel d3Channel;
	
	private static Logger LOG = LoggerFactory.getLogger(SyncAccessor.class);
	
	public SyncAccessor(Node node){
		D3ChannelGroup group = node.getGroups().get(NetUtil.localHostName());
		d3Channel = group.next();
	}
	
	@Override
	public Object accessSync(final Request request){

		Promise<Object> promise = new D3Promise(d3Channel);
		
		request.setId(d3Channel.generateRequestIndex());
		d3Channel.addPromise(request.getId(), promise);
		
		ChannelFuture f = d3Channel.send(request);
		
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (!future.isSuccess()) {
					d3Channel.getPromise(request.getId());
					LOG.error("remote call error: {}",
							(future.cause() == null ? "" : future.cause().getMessage()));
				}
			}
		});
		
		Object result = null;
		try {
			
			result = promise.get(5000, TimeUnit.MICROSECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			LOG.error("sync access error: {}", e.getMessage());
		}
		return result;
	}
}
