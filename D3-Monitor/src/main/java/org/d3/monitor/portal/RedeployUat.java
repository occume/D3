package org.d3.monitor.portal;

import java.io.IOException;
import net.schmizz.sshj.SSHClient;

public class RedeployUat extends Redeploy {

	private static String delete_pre = "cd /usr/local/tomcat/webapps;"
			+ "sudo rm -r portal;" 
			+ "sudo rm portal.war";

	private static String 
			localPath = "D:\\workspace\\octopus-portal-java\\target\\portal.war",
			remotePath = "/usr/local/tomcat/webapps/portal.war";

	private static String restart_tomcat = "cd /usr/local/tomcat/bin;"
			+ "sudo ./shutdown.sh;" 
			+ "sudo ./startup.sh;";
	
	

	public static void redeploy() {

		Node node = getNode(KEYS.PORTAL_UAT);
		SSHClient client;
		try {
			client = getClient(node);

			exec	(client, delete_pre);
			upload	(client, localPath, remotePath);
			exec	(client, restart_tomcat);

			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String...strings) throws IOException{
//		redeploy();
		Node node = getNode(KEYS.PORTAL_UAT);
		SSHClient client = getClient(node);
		exec	(client, restart_tomcat);
	}

}
