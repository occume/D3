package org.d3.monitor.portal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import com.google.common.collect.Maps;
import net.schmizz.sshj.SSHClient;
public class RedeployServerProd extends Redeploy {
	
	/**
	 * 	1, server
		2, hotel
		3, vacation
		4, flight
		5, other
	 */
	public static void main(String...strings) throws IOException{
		redeployAll(3);
	}
	
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
	
	private static String
			serverPackagePath 	= BASE_LOCAL_PATH + "enteroctopus-server/pom.xml",
			hotelPackagePath 	= BASE_LOCAL_PATH + "enteroctopus-hotel/pom.xml",
			vacationPackagePath = BASE_LOCAL_PATH + "enteroctopus-vacation/pom.xml",
			flightPackagePath 	= BASE_LOCAL_PATH + "enteroctopus-flight/pom.xml",
			otherPackagePath 	= BASE_LOCAL_PATH + "enteroctopus-other/pom.xml";

	private static String MVN_PACKAGE = "cmd /c mvn clean install -DskipTests ";
	private static String restart_tomcat = "sudo service tomcat restart;";
	private static Map<Integer, String> pkgPathMapping = Maps.newHashMap();
	private static Map<Integer, String> pkgNameMapping = Maps.newHashMap();
	
	static{
		pkgPathMapping.put(1, serverPackagePath);
		pkgPathMapping.put(2, hotelPackagePath);
		pkgPathMapping.put(3, vacationPackagePath);
		pkgPathMapping.put(4, flightPackagePath);
		pkgPathMapping.put(5, otherPackagePath);
		
		pkgNameMapping.put(1, "server");
		pkgNameMapping.put(2, "hotel");
		pkgNameMapping.put(3, "vacation");
		pkgNameMapping.put(4, "flight");
		pkgNameMapping.put(5, "other");
	}
	
	public static void redeployAll(int...ids){
		
		/**
		 * 编译打包
		 */
		for(int id: ids){
			try {
				comExec(id);
			} catch (Exception e) {
				e.printStackTrace();
				err(e.getMessage());
				return;
			}
		}
		/**
		 * 部署重启
		 */
		String[] servers = {KEYS.SERVER178, KEYS.SERVER179};
		for(String server: servers){
			redeploy(server);
		}
	}
	
	public static void comExec(int id) throws Exception{
		
		print("PACKAGE " + pkgNameMapping.get(id) + " START");
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream,errorStream);
		
        String target = pkgPathMapping.get(id);
		String line = MVN_PACKAGE + "-f " + target;

		CommandLine cmdLine = CommandLine.parse(line);
		
		DefaultExecutor excutor = new DefaultExecutor();
		
			excutor.setStreamHandler(streamHandler);
			excutor.execute(cmdLine);
			
			String out = outputStream.toString("gbk");
	        if(out.contains("BUILD SUCCESS")){
	        	print("PACKAGE " + pkgNameMapping.get(id) + " SUCCESS");
	        }
	        else{
	        	err("PACKAGE " + pkgNameMapping.get(id) + " FAIL");
	        	throw new Exception();
	        }
	}
	
	public static void redeploy(String server) {
		
		Node node = getNode(server);
		print("DEPLOY "+ node.host +" START");
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
			print("DEPLOY "+ node.host +" SCUCCESS");
		}catch(IOException e){
			err(e.getMessage());
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
	
	private static void print(String step){
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    	System.out.println(step);
    	System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    	System.out.println();
	}
	
	private static void err(String step){
		System.err.println("++++++++++++++++++++++++++++++++++++++++++++");
    	System.err.println(step);
    	System.err.println("++++++++++++++++++++++++++++++++++++++++++++");
    	System.err.println();
	}
}
