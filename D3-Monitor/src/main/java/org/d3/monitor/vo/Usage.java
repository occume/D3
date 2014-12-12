package org.d3.monitor.vo;

public class Usage {
	
	private String host;
	private float net;
	private float mem;
	private float io;
	private float cpu;
	
	public Usage(){}
	
	public Usage(String host, float net, float mem, float io, float cpu) {
		this.host = host;
		this.net = net;
		this.mem = mem;
		this.io = io;
		this.cpu = cpu;
	}
	
	public float getNet() {
		return net;
	}
	public void setNet(float net) {
		this.net = net;
	}
	public float getMem() {
		return mem;
	}
	public void setMem(float mem) {
		this.mem = mem;
	}
	public float getIo() {
		return io;
	}
	public void setIo(float io) {
		this.io = io;
	}
	public float getCpu() {
		return cpu;
	}
	public void setCpu(float cpu) {
		this.cpu = cpu;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String toString() {
		return "Usage [host=" + host + ", net=" + net + ", mem=" + mem
				+ ", io=" + io + ", cpu=" + cpu + "]";
	}
	
}
