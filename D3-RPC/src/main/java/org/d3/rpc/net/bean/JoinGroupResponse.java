package org.d3.rpc.net.bean;

public class JoinGroupResponse extends Message{
	
	private String groupName;
	
	public JoinGroupResponse(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return "JoinGroupMessage [groupName=" + groupName + "]";
	}

}
