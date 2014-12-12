package org.d3.rpc.net.bean;

public class Response extends Message{
	
	public static Response INVOKE_TIME_OUT = new Response();
	
	private Object result;
	
	public Response(){}
	
	public Response(long id, Object result) {
		this.id = id;
		this.result = result;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "Response [id=" + id + ", result=" + result + "]";
	}

}
