package org.d3.monitor.warrior;

import org.d3.rpc.util.Closer;

public class ShutDown {

	public static void main(String[] args) {
		Closer.close();
	}

}
