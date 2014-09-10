package org.d3.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorTest {

	public static void main(String[] args) throws Exception {
		
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.0.2", retryPolicy);
		client.start();
		
//		client.create().forPath("/d3-conf4");
		client.setData().forPath("/d3-conf4", "hello zk".getBytes());
//		client.close();
		
	}

}
