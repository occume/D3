package org.d3.zk;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;

public class DeleteGroup extends ConnectionWatcher {

	public void delete(String groupName) throws KeeperException, InterruptedException {
		String path = "/" + groupName;
		try {
			List<String> children = zk.getChildren(path, false);
			for (String child : children) {
				zk.delete(path + "/" + child, -1);
			}
			zk.delete(path, -1);
		} catch (KeeperException.NoNodeException e) {
			System.out.printf("Group %s does not exist\n", groupName);
			System.exit(1);
		}
	}

	public static void main(String...strings) throws IOException, InterruptedException, KeeperException{
		DeleteGroup d = new DeleteGroup();
		d.connect("127.0.0.1");
		d.delete("registry");
		d.close();
	}
	
}
