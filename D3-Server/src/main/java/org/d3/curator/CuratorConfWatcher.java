package org.d3.curator;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class CuratorConfWatcher implements Watcher{
	
	public static void main(String...strings){
		
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println(event);
	}
	
}
