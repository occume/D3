package org.d3.monitor.client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.schmizz.sshj.SSHClient;


import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xqbase.util.ByteArrayQueue;
import com.xqbase.util.Streams;

public class Watcher {
	
	public static void main(String...strings){
		for(Node node: nodes){
			check(node);
		}
	}
	
	protected static List<Node> nodes = Lists.newArrayList();
	private static Map<String, String> passwords = Maps.newHashMap();
	
	private static final String USER = "op1";
	private static final int PORT = 1022;
	
	private static final String localServerPath = "D:/Workspaces2014/D3/D3-Monitor/src/main/resources/client.properties";
	private static final String remoteServerPath = "/var/crawl/client.properties";
	
	static{
		
		passwords.put("NT", "e2xqsSij8cbgCedg^");
		passwords.put("NJ", "0mvMixlmZjaf}8onp");
		passwords.put("CD", "e2xqsSij8cbgCedg^");
		passwords.put("OY", ">xaD67wvewmvXng^a");
		passwords.put("SZ", ":k5lPEugy9mpugcve");
		passwords.put("HZ", "cW#pp8g_3fQvcow");
		passwords.put("HF", "cW#pp8g_3fQvcow");
		passwords.put("GZ", "4ssmolbpjwR5ch.Yg");
		passwords.put("BJ", "cW#pp8g_3fQvcow");
		
		Properties clients = Conf.get("abnormal_clients");
		
		for(Object c: clients.keySet()){
			String clientCode = String.valueOf(c);
			nodes.add(new Node(clientCode, PORT, USER, passwords.get(transform(clientCode))));
		}
//		nodes.add(new Node("ADSL-VMS-NT-72.octopi.cn", 1022, 	"op1", 	"e2xqsSij8cbgCedg^"));
	}
	
	public static void check(Node node){
//		Node node = nodes.get(0);
		SSHClient client;
		try {
			client = getClient(node);

			List<String> lines = exec	(client, "cat client.properties;cat Auth.properties");
			
			for(String line: lines){
				if(line.contains("serverUrls")){
					if(line.contains("140.207.228.179/prod0/")){
						System.out.print(node.clientCode + "   ok");
					}
					else{
						System.err.println(node.clientCode + "   " + line);
						System.out.print("......upload client.properties");
						upload	(client, localServerPath, 	remoteServerPath);
						System.out.println("......upload ok");
					}
					
				}
				else if(line.contains("userid")){
					System.out.print("   " + line);
				}
				else if(line.contains("password")){
					System.out.println("   " + line);
				}
			}
			
//			lines = exec	(client, "cat Auth.properties");
//			for(String line: lines){
//				if(line.contains("userid")){
//					System.out.print("   " + line);
//				}
//				else if(line.contains("password")){
//					System.out.println("   " + line);
//				}
//			}

			client.close();
		}
		catch(UnknownHostException e){
			System.err.println(node.clientCode + "   UnknownHost");
		}
		catch(IOException e){
			System.err.println(node.clientCode + "   " + e.getMessage());
		}
	}
	
	public static SSHClient getClient(Node node) throws IOException{
		
		SSHClient client = new SSHClient();
		client.loadKnownHosts();
		client.addHostKeyVerifier(new PromiscuousVerifier());

		client.connect(node.host, node.port);
		client.authPassword(node.username, node.password);
		client.setConnectTimeout(3000);
		client.setTimeout(3000);
		
		return client;
	};
	
	public static List<String> exec(SSHClient client, String cmd) throws IOException {
		ByteArrayQueue baq = new ByteArrayQueue();
		List<String> result = Lists.newArrayList();
		exec0(client, cmd, baq);
		try (
			BufferedReader in = new BufferedReader(new
					InputStreamReader(baq.getInputStream()));
		) {
			String line;
			while ((line = in.readLine()) != null) {
				result.add(line);
			}
		}
		finally{
//			client.disconnect();
		}
		return result;
	}
	
	protected static void upload(SSHClient ssh, String local, String remote) throws IOException {
		SCPFileTransfer scp = ssh.newSCPFileTransfer();
		scp.upload(local, remote);
	}
	
	public static void exec0(SSHClient ssh, String command, ByteArrayQueue baq) throws IOException {
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

	private static String transform(String clientCode){
		if(clientCode.contains("NT")) return "NT";
		else if(clientCode.contains("CD")) return "CD";
		else if(clientCode.contains("OY")) return "OY";
		else if(clientCode.contains("SZ")) return "SZ";
		else if(clientCode.contains("HZ")) return "HZ";
		else if(clientCode.contains("HF")) return "HF";
		else if(clientCode.contains("GZ")) return "GZ";
		else if(clientCode.contains("BJ")) return "BJ";
		else return "";
	}

	protected static class Node{
		public String host;
		public String clientCode;
		public int port;
		public String username;
		public String password;
		public Node(String clientCode, int port, String username, String password) {
			this.clientCode = clientCode;
			this.host = clientCode.replace("_", "-") + ".octopi.cn";
			this.port = port;
			this.username = username;
			this.password = password;
		}
		@Override
		public String toString() {
			return "Node [host=" + host + ", clientCode=" + clientCode
					+ ", port=" + port + ", username=" + username
					+ ", password=" + password + "]";
		}
	}
	
	private static class Validation{
		public String clientCode;
	}
}
