package org.d3.demo.httpclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

public class HttpClientTest {

	public static void main(String...strings){
		
		HttpClient httpclient = HttpClientUtil.getClient();
        HttpPost httppost = new HttpPost("http://localhost:8888/hotel/qunar?data=a,b,c,d,e,f");  
        
        for(int i = 0; i < 100; i++){
        	
			try {
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();  
		        System.out.println(response.getStatusLine());  
		        if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){  
		            HttpEntity entity = response.getEntity();  
		            //显示内容  
		            if (entity != null) {  
		                System.out.println(EntityUtils.toString(entity));  
		            }
		        }  
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
	}

}
