package org.d3.monitor.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtil {
	
	public static String hostName(){
		String host = "";
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}
		return host;
	}
	
}
