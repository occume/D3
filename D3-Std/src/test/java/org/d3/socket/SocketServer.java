package org.d3.socket;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

	public static void main(String[] args) throws IOException {
		
		ServerSocket ss = new ServerSocket(10005);
		 
        Socket s = ss.accept();
        String ip = s.getInetAddress().getHostAddress();
        System.out.println("IP::"+ip);
         
        BufferedReader bufIn = 
            new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = 
            new PrintWriter(new FileWriter("Kints1.txt"),true);
         
        String line = null;
        while((line=bufIn.readLine())!=null)
        {   
            //if("over".equals(line))
            //    break;
            out.println(line);
        }
 
         
        PrintWriter pw = 
            new PrintWriter(s.getOutputStream(),true);
     
        pw.println("上传成功");
 
        out.close();
        s.close();
        ss.close();
		
	}

}
