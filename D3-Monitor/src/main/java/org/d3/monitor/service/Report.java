package org.d3.monitor.service;

import org.d3.monitor.vo.Usage;
import org.d3.rpc.annotion.RemoteService;

@RemoteService
public interface Report {
	
	public void report(Usage usage);
}
