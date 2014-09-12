package org.d3.gateway.service;

import org.d3.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.MoreExecutors;

@Component
public class GatewayServerStartService extends AbstractService {

	@Autowired
	Server tcpServer;
	
	public GatewayServerStartService(){
		super();
		addListener(
		        new Listener() {
					@Override
					public void starting() {
						GatewayServerStartService.this.notifyStarted();
					}
		        },
		        MoreExecutors.sameThreadExecutor());
	}
	
	@Override
	protected void doStart() {
		tcpServer.start();
	}

	@Override
	protected void doStop() {
		tcpServer.stop();
	}

}