package org.d3.monitor.service;

import org.d3.rpc.annotion.RemoteService;

@RemoteService
public interface BigdataClientWatcher {

	public boolean changed();
	
	public long vlength();
}
