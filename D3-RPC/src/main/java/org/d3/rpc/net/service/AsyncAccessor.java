package org.d3.rpc.net.service;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.channel.D3Channel;
import org.d3.rpc.net.channel.D3ChannelGroup;
import org.d3.rpc.net.channel.D3Promise;
import org.d3.rpc.net.node.Node;
import org.d3.rpc.util.Invokable;
import org.d3.rpc.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步请求
 * @author d_jin
 *
 */
public class AsyncAccessor extends BaseAccessor{
	
	private D3Channel d3Channel;
	
	private Node node;
	
	private static Logger LOG = LoggerFactory.getLogger(AsyncAccessor.class);
	
	public AsyncAccessor(Node node){
		
		this.node = node;
		D3ChannelGroup group = node.getGroups().get(NetUtil.localHostName());
		d3Channel = group.next();
	}

	public void accessAsync(final Request request, final Invokable invoke){
		
//		if(!node.ready()){
//			System.out.println(Thread.currentThread().getName() + "---park");
//			node.park();
//			System.out.println("---unpark");
//		}
		node.aquire();

		/**
		 *  如果 不是新的服务 ,这里有可能得到一个空的channel,
		 *  
		 *  如果是空的channel,可以重新获取
		 */
		Promise<Object> promise = new D3Promise(d3Channel);
		
		if(invoke != null){
			promise.addListener(new FutureListener<Object>() {
				@Override
				public void operationComplete(Future<Object> future)
						throws Exception{
					if(future.isSuccess())
						invoke.invoke(future.get());
				}
			});
		}
		
		request.setId(node.reqestIndex());
		d3Channel.addPromise(request.getId(), promise);
		node.addPromise(request.getId(), promise);
		
		ChannelFuture f = d3Channel.send(request);
		
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (!future.isSuccess()) {
					Promise promise = d3Channel.getPromise(request.getId());
//					promise.setSuccess(null);
					promise.setFailure(new Exception("send fail"));
					node.failCountIncrement();
//					LOG.error("remote call error: {}",
//							(future.cause() == null ? "" : future.cause().getMessage()));
				}
				else{
					node.sendOkIncrement();
				}
			}
		});
	}
}
