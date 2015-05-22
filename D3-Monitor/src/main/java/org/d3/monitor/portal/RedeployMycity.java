package org.d3.monitor.portal;

import java.io.IOException;
import net.schmizz.sshj.SSHClient;

public class RedeployMycity extends Redeploy {
	
	/**
	 *  部署mycity的时候，注意 相对 路径的问题
	 *  
	 *  /portal/xxx
	 *  /xxxx
	 */

	private static String delete_pre = "cd /usr/local/tomcat/webapps;"
			+ "rm -r mycity;"
			+ "rm mycity.war";

	private static String 
			localPath = "D:\\Workspaces2014\\ZiuWan\\target\\ziuwan.jar",
			remotePath = "/usr/local/tomcat8/webapps/ziuwan/WEB-INF/lib/ziuwan.jar";

//	private static String restart_tomcat = "cd /usr/local/tomcat8/bin;"
//			+ "./shutdown.sh;"
//			+ "./startup.sh;";
	private static String restart_tomcat = "service tomcat restart;";
	
	public static void redeploy() {
		Node node = getNode(KEYS.MYCITY);
		SSHClient client;
		try {
			client = getClient(node);

			exec	(client, delete_pre);
			upload	(client, localPath, remotePath);
			exec	(client, restart_tomcat);

			client.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void pwd(){
		Node node = getNode(KEYS.MYCITY);
		SSHClient client;
		try {
			client = getClient(node);

			exec	(client, "pwd");

			client.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String...strings) throws IOException{
		redeploy();
//		Node node = getNode(KEYS.PORTAL_UAT);
//		SSHClient client = getClient(node);
//		exec	(client, "pwd");
//		client.close();
	}
}
