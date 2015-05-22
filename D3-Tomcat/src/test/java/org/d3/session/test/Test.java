package org.d3.session.test;

import java.nio.channels.SelectionKey;
import java.util.Arrays;

public class Test {

	public static void main(String[] args) {
		int accept = SelectionKey.OP_ACCEPT;
		int connect = SelectionKey.OP_CONNECT;
		int write = SelectionKey.OP_WRITE;
		int read = SelectionKey.OP_READ;
		
		System.out.println(~accept);
		System.out.println(~connect);
		System.out.println(~write);
		System.out.println(~read);
		
		System.out.println(write & (~accept));
		System.out.println(write & (~connect));
		System.out.println(write & (~write));
		System.out.println(write & (~read));
		
		Arrays.asList();
	}

}
