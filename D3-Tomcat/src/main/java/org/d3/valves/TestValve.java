package org.d3.valves;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.Manager;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

public class TestValve extends ValveBase{

	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {
		
		String contextName = request.getMappingData().context.getName();
		Manager m = request.getMappingData().context.getManager();
		
//		System.out.println("--- a test valve ---");
//		System.out.println("--- context: "+ contextName +" ---");
//		System.out.println("--- use manager: "+ m +" ---");
//		System.out.println("--- a test valve ---");
		
		getNext().invoke(request, response);
	}

	@Override
	public void backgroundProcess() {
//		System.out.println("--- backgroundProcess in TestValve ---");
	}



	public static void main(String...strings) throws IOException, ServletException{
		new TestValve().invoke(null, null);
	}
}
