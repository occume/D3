package org.d3.monitor.portal;

import java.io.IOException;
import net.schmizz.sshj.SSHClient;

public class RedeployServerProd extends Redeploy {
	
	/**
	 *  部署的时候，注意 相对 路径的问题
	 *  
	 *  /portal/xxx
	 *  /xxxx
	 */
	private static final String BASE_LOCAL_PATH = "D:/workspace/enteroctopus/enteroctopus/";
	private static final String BASE_REMOTE_PATH = "/home/op1/octopus/webapps/ROOT/WEB-INF/lib/";
	
	private static String delete_pre = "cd /home/op1/octopus/webapps/ROOT/WEB-INF/lib;"
			+ "sudo rm enteroctopus-server-2.2.0.jar";

	private static String 
			localServerPath 	= BASE_LOCAL_PATH 	+ "enteroctopus-server/target/enteroctopus-server-2.2.0.jar",
			remoteServerPath 	= BASE_REMOTE_PATH 	+ "enteroctopus-server-2.2.0.jar",
			localHotelPath 		= BASE_LOCAL_PATH 	+ "enteroctopus-hotel/target/enteroctopus-hotel-1.0.0.jar",
			remoteHotelPath 	= BASE_REMOTE_PATH 	+ "enteroctopus-hotel-1.0.0.jar",
			localVacationPath 	= BASE_LOCAL_PATH 	+ "enteroctopus-vacation/target/enteroctopus-vacation-1.0.0.jar",
			remoteVacationPath 	= BASE_REMOTE_PATH 	+ "enteroctopus-vacation-1.0.0.jar",
			localFlightPath 	= BASE_LOCAL_PATH 	+ "enteroctopus-flight/target/enteroctopus-flight-1.0.0.jar",
			remoteFlightPath 	= BASE_REMOTE_PATH 	+ "enteroctopus-flight-1.0.0.jar",
			localOtherPath 		= BASE_LOCAL_PATH 	+ "enteroctopus-other/target/enteroctopus-other-1.0.0.jar",
			remoteOtherPath 	= BASE_REMOTE_PATH 	+ "enteroctopus-other-1.0.0.jar";

	private static String restart_tomcat = "sudo service tomcat restart;";
	
	public static void redeployAll(){
		String[] servers = {KEYS.SERVER178, KEYS.SERVER179};
		for(String server: servers){
			redeploy(server);
		}
	}
	
	public static void redeploy(String server) {
		Node node = getNode(server);
		SSHClient client;
		try {
			client = getClient(node);

			exec	(client, delete_pre);
			upload	(client, localServerPath, 	remoteServerPath);
			upload	(client, localHotelPath, 	remoteHotelPath);
			upload	(client, localVacationPath, remoteVacationPath);
			upload	(client, localFlightPath, 	remoteFlightPath);
			upload	(client, localOtherPath, 	remoteOtherPath);
			exec	(client, restart_tomcat);

			client.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void pwd(){
		Node node = getNode(KEYS.SERVER179);
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
		redeployAll();
	}
}
