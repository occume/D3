package org.d3.monitor.portal;

import java.io.IOException;

import net.schmizz.sshj.SSHClient;

public class RedeployFat extends Redeploy {

	private static String delete_pre = "cd /home/fxUser/octopus-portal;"
			+ "sudo rm -r portal;" 
			+ "sudo rm portal.war";

	private static String 
			localPath = "D:\\workspace\\octopus-portal-java\\target\\portal.war",
			remotePath = "/home/fxUser/octopus-portal/portal.war";

	private static String restart_tomcat = "cd /usr/share/tomcat-portal/bin;"
			+ "sudo ./shutdown.sh;" 
			+ "sudo ./startup.sh;";

	public static void redeploy() {

		Node node = getNode(KEYS.PORTAL_FAT);
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
	
	public static void main(String...strings){
		redeploy();
	}

}
