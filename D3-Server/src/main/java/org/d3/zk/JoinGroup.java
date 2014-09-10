package org.d3.zk;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

public class JoinGroup extends ConnectionWatcher{

	public void join(String groupName, String memberName) throws KeeperException, InterruptedException{
		String path = "/" + groupName + "/" + memberName;
		String createdPath = zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("Created " + createdPath);
	}
	
	public static void main(String...strings) throws KeeperException, InterruptedException, IOException{
		JoinGroup j = new JoinGroup();
		j.connect("192.168.0.2");
		j.join("nbxx", "jd");
		j.close();
	}
	
}
