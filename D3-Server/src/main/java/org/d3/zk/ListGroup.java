package org.d3.zk;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;

public class ListGroup extends ConnectionWatcher {

	public void list(String groupName){
		String path = "/" + groupName;
		try {
			List<String> children = zk.getChildren(path, false);
			if(children.isEmpty()){
				System.out.println("no children");
				System.exit(1);
			}
			for(String name: children){
				System.out.println(name);
			}
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String...strings) throws IOException, InterruptedException{
		ListGroup l = new ListGroup();
		l.connect("192.168.0.2");
		l.list("nbxx");
		l.close();
	}
	
}
