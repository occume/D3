package org.d3.monitor.portal;

import java.io.IOException;

import org.d3.monitor.util.CmdExecutor;

import net.schmizz.sshj.SSHClient;

public class RedeployLocal extends Redeploy {

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
		String cmd = "D:/server/cwRsync/bin/rsync "
				+ "-vzrtopg /cygdrive/d/workspace/octopus-portal-java/target/classes "
				+ "127.0.0.1::portal_class";
		CmdExecutor.execute(cmd);
	}
	
	public static void main(String...strings) throws IOException{
		redeploy();
//		Node node = getNode(KEYS.PORTAL_UAT);
//		SSHClient client = getClient(node);
//		exec	(client, "pwd");
//		client.close();
	}

}
