package org.d3.demo.tomcat.tribes;

import java.io.Serializable;

import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Member;

public class MyMessageListener implements ChannelListener {

	@Override
	public boolean accept(Serializable arg0, Member arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void messageReceived(Serializable arg0, Member arg1) {
		// TODO Auto-generated method stub

	}

}
