package org.d3.monitor.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

public class CmdExecutor{
	
	public static void execute(String cmd){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream,errorStream);
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		
		
		CommandLine cmdLine = CommandLine.parse(cmd);
		
		DefaultExecutor excutor = new DefaultExecutor();
		try {
			excutor.setStreamHandler(streamHandler);
			excutor.execute(cmdLine);
			
			String out = outputStream.toString("gbk");
	        String error = errorStream.toString("gbk");
	        System.out.println(out);
	        System.out.println("--------------------------------------------------");
	        System.out.println(error);
	        System.out.println("--------------------------------------------------");

		} catch (ExecuteException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String...strings){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream,errorStream);
//		
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		
		String line = "D:/server/cwRsync/bin/rsync -vzrtopg /cygdrive/d/log/ 10.8.90.87::test";
		
		CommandLine cmdLine = CommandLine.parse(line);
		
		int exitValue = -1;
		DefaultExecutor excutor = new DefaultExecutor();
		try {
//			exitValue = excutor.execute(cmdLine);
			excutor.setStreamHandler(streamHandler);
			excutor.execute(cmdLine);
			
			String out = outputStream.toString("gbk");
	        String error = errorStream.toString("gbk");
	        System.out.println(out);
	        System.out.println("--------------------------------------------------");
	        System.out.println(error);
	        System.out.println("--------------------------------------------------");
	        exitValue = 0;
		} catch (ExecuteException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
