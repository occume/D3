package org.d3.gateway.bootstrap;

import java.util.Set;

import org.d3.gateway.D3Context;
import org.d3.gateway.service.GatewayServerStartService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.common.util.concurrent.ServiceManager.Listener;

/**
 * 
 * @author d_jin
 *
 */
@Component
public class Bootstrap{

//	@Autowired
//	private BufferFacade bufferFacade;
	
	ServiceManager serviceManager;
	
	public Bootstrap(){
		
		
	}
	
	public void lanucher(){
		Set<Service> services = Sets.newLinkedHashSet();
		/**
		 * 节点管理服务
		 */
		GatewayServerStartService tcpService = (GatewayServerStartService) D3Context.getBean("gatewayServerStartService");
		services.add(tcpService);
		
		serviceManager = new ServiceManager(services);
		serviceManager.addListener(new Listener() {

			@Override
			public void failure(Service service) {
				
			}

			@Override
			public void healthy() {
				System.out.println("all service has been started");
			}

			@Override
			public void stopped() {
				
			}
			
		}, MoreExecutors.sameThreadExecutor());
		serviceManager.startAsync();
	}
	
	public void shoutDown(){
		serviceManager.stopAsync();
	}
	
	/** 启动定时任务 */
	 @Scheduled(cron = "0/5 * * * * *")  
	protected void startScheduleTask() {
		
		//schedule tasks

	}
	
}
