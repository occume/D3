package org.d3.demo.commons.exec;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

import com.xqbase.util.ByteArrayQueue;
import com.xqbase.util.Streams;

public class SSHTest {
	
	private static class Node{
		private String host;
		private int port;
		private String username;
		private String password;
		public Node(String host, int port, String username, String password) {
			this.host = host;
			this.port = port;
			this.username = username;
			this.password = password;
		}
	}
	
	private static List<Node> nodeList = new ArrayList<>();

	static{
//		nodeList.add(new Node("10.8.90.86", 1022, 	"deploy", 	"654321WY_654321wy"));
//		nodeList.add(new Node("10.8.90.87", 1022, 	"deploy", 	"654321WY_654321wy"));
		nodeList.add(new Node("10.8.74.8", 	1022,  	"op1",    	"1qaz2wsx#EDC$RFV"));
//		nodeList.add(new Node("10.8.74.7", 	1022,  	"op1",    	"1qaz2wsx#EDC$RFV"));
//		nodeList.add(new Node("10.2.5.82", 	22,  	"fxUser",    	")OKM9ijn"));
	}
	
	public static void main(String[] args) throws IOException {
		
		for(Node node: nodeList){
			SSHClient client = new SSHClient();
			client.loadKnownHosts();
			client.addHostKeyVerifier(new PromiscuousVerifier());

			client.connect(node.host, node.port);
			client.authPassword(node.username, node.password);
			
//			deletePre(client);
//			upload(client);
//			restartTomcat(client);
			
//			pwd(client);
			
			deployMonitor(client);
			
			client.close();
		}
	}
	
	static void deletePre(SSHClient client) throws IOException{
		String cmd = "cd /home/fxUser/octopus-portal;"
				+ "sudo rm -r portal;"
				+ "sudo rm portal.war";
		exec(client, cmd);
	}
	
	static void restartTomcat(SSHClient client) throws IOException{
		String cmd = "cd /usr/share/tomcat-portal/bin;"
				+ "sudo ./shutdown.sh;"
				+ "sudo ./startup.sh;";
		exec(client, cmd);
	}
	
	static void stopTomcat(SSHClient client) throws IOException{
		String cmd = "cd /usr/share/tomcat-portal/bin;"
				+ "sudo ./startup.sh";
		exec(client, cmd);
	}
	
	static void startTomcat(SSHClient client) throws IOException{
		String cmd = "cd /usr/share/tomcat-portal/bin;"
				+ "sudo ./startup.sh";
		exec(client, cmd);
	}
	
	private static void upload(SSHClient ssh) throws IOException {
		SCPFileTransfer scp = ssh.newSCPFileTransfer();
		scp.upload("D:\\workspace\\octopus-portal-java\\target\\portal.war", "/home/fxUser/octopus-portal/portal.war");
	}
	
	static void pwd(SSHClient client) throws IOException{
		String cmd = "pwd";
		exec(client, cmd);
	}
	
	static void deployMonitor(SSHClient client) throws IOException{
		String cmd = "rsync -r 10.8.90.87::octopus_prepare /tmp;"
				  + "source /tmp/prepare.sh";
		exec(client, cmd);
	}
	
	static void shutDown(SSHClient client) throws IOException{
		String cmd = "sudo /home/deploy/monitor/monitor.sh stop";
		exec(client, cmd);
	}
	
	static void exec(SSHClient client, String cmd) throws IOException {
		ByteArrayQueue baq = new ByteArrayQueue();
		exec0(client, cmd,baq);
		try (
			BufferedReader in = new BufferedReader(new
					InputStreamReader(baq.getInputStream()));
		) {
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
		}
		finally{
			client.disconnect();
		}
	}
	
	static void exec0(SSHClient ssh, String command, ByteArrayQueue baq) throws IOException {
		try (
			Session session = ssh.startSession();
			Command command_ = session.exec(command);
		) {
			Streams.copy(command_.getErrorStream(), System.err);
			if (baq == null) {
				Streams.copy(command_.getInputStream(), new ByteArrayOutputStream());
			} else {
				Streams.copy(command_.getInputStream(), baq.getOutputStream());
			}
			session.close();
		}
		
	}

}
