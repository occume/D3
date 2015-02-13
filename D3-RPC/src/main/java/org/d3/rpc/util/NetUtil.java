package org.d3.rpc.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtil {
	
	

	public static String localHostName(){
		String host = Constant.Srings.EMPTY;
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}
		return host;
	}
	
}
