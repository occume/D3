package org.d3.rpc.net.node.client;

import java.net.SocketAddress;

import org.d3.rpc.net.node.Node;

public interface Client extends Node{
	
	public void connect(SocketAddress...addressArray);
}
