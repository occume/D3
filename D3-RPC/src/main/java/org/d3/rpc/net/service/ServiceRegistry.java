package org.d3.rpc.net.service;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.d3.rpc.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRegistry{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
			
	private CountDownLatch latch = new CountDownLatch(1);
	
	private String registryAddress;
	
	public ServiceRegistry(String registryAddress){
		this.registryAddress = registryAddress;
	}
	
	public void register(String data){
		if(data != null){
			ZooKeeper zk = connectServer();
			if(zk != null){
				createNode(zk, data);
			}
		}
	}
	
	private ZooKeeper connectServer(){
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(registryAddress, Constant.ZK.SESSION_TIMEOUT, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected) {
			            latch.countDown();
			        }
				}
			});
			latch.await();
		} catch (IOException | InterruptedException e) {
			LOGGER.error("", e);
		}
		return zk;
	}

	private void createNode(ZooKeeper zk, String data){
		String path = Constant.ZK.DATA_PATH;
		byte[] bytes = data.getBytes();
		try {
			Stat stat = zk.exists(path, false);
			if(stat == null){
				zk.create(path, bytes, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			}
			else{
				zk.setData(path, bytes, -1);
			}
			
//			String path = zk.create(Constant.ZK.DATA_PATH, bytes, 
//					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			LOGGER.debug("create zookeeper node ({} => {})", path, data);
		} catch (KeeperException | InterruptedException e) {
			LOGGER.error("", e);
		}
	}
	
}
