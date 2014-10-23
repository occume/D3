package org.d3.socket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SoketClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Socket s = new Socket("127.0.0.1",10005);
        //定义读取键盘数据对象
        BufferedReader bufr = 
            new BufferedReader(new FileReader("D:/wy.jpg"));  
        //定义目的，将数据写入socket输出流，发给服务器
        PrintWriter out = new PrintWriter(s.getOutputStream(),true);
 
        String line = null;
        while((line=bufr.readLine())!=null)
        {   
            out.println(line);
        }
        
        s.getOutputStream().close();
        s.shutdownOutput();
         
        BufferedReader bufIn = 
            new BufferedReader(new InputStreamReader(s.getInputStream()));
         
         
        String str = bufIn.readLine();
 
        System.out.println(str);
 
        bufr.close();
        s.close();
 
	}

}
