package org.d3.rpc.net.dispatcher.sync;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Promise;

import org.d3.rpc.manage.World;
import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.channel.D3Channel;
import org.d3.rpc.net.channel.D3Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SyncDispatchStrategy{
	
	Object dispatch(Request req);
	
	public static SyncDispatchStrategy ROUND = new SyncDispatchStrategy(){
		private Logger logger = LoggerFactory.getLogger(getClass());
		@Override
		public Object dispatch(final Request req) {
			
			if(World.defaultChannelGroup().size() == 0){
				logger.warn("Rpc client connection size: 0");
				return null;
			}
			
			try {
				final D3Channel d3Channel = World.defaultChannelGroup().next();
				Channel channel = d3Channel.getChannel();
				Promise<Object> promise = new D3Promise(d3Channel);
				
				req.setId(d3Channel.generateRequestIndex());
				d3Channel.addPromise(1, promise);
				
				ChannelFuture f = channel.writeAndFlush(req);
				f.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if(!future.isSuccess()){
							d3Channel.getPromise(req.getId());
							logger.error("remote call error: " + (future.cause() == null ? "" : future.cause().getMessage()));
						}
					}
				});
				
				Object result = promise.get();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	};
}
