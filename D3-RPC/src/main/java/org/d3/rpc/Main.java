package org.d3.rpc;

import org.d3.rpc.op.Echo;
import org.d3.rpc.op.RemoteEcho;
import org.d3.rpc.support.Server;



public class Main {
	public static void main(String[] args) {
		Server server = new RPC.RPCServer();
		server.register(Echo.class, RemoteEcho.class);
		server.start();
	}

}
