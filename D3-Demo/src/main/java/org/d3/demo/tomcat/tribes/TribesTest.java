package org.d3.demo.tomcat.tribes;

import java.io.Serializable;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.group.GroupChannel;

public class TribesTest {

	public static void main(String[] args) throws Exception {
		//create a channel
		Channel myChannel = new GroupChannel();

		//create my listeners
		ChannelListener msgListener = new MyMessageListener();
		MembershipListener mbrListener = new MyMemberListener();

		//attach the listeners to the channel
		myChannel.addMembershipListener(mbrListener);
		myChannel.addChannelListener(msgListener);

		//start the channel
		myChannel.start(Channel.DEFAULT);

		//create a message to be sent, message must implement java.io.Serializable
		//for performance reasons you probably want them to implement java.io.Externalizable
		Serializable myMsg = new MyMessage();

		//retrieve my current members
		Member[] group = myChannel.getMembers();
		
		//send the message
		
//		myChannel.send(group,myMsg,Channel.SEND_OPTIONS_DEFAULT);
		
	}

}
