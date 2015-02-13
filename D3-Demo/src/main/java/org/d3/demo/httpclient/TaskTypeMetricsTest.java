package org.d3.demo.httpclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class TaskTypeMetricsTest {

	public static void main(String...strings) throws UnsupportedEncodingException{
		
		HttpClient httpclient = HttpClientUtil.getClient();
//        HttpPost httppost = new HttpPost("http://localhost:8888/hotel/qunar?data=a,b,c,d,e,f");
        
        		String url = "http://engine.dashboard.sh2.ctripcorp.com:8080/data?"
        				+ "env=PROD&"
        				+ "metric-name=octopus.tasks&"
        				+ "interval=2m&"
        				+ "start-time=2014-12-16%2011:34:00&"
        				+ "end-time=2014-12-16%2011:36:00&"
        				+ "tag=%7Bclient_code:[ADSL_VMS_NJ_07]%7D&"
        				+ "group-by=[task_type,action]&"
        				+ "chart=line&"
//        				+ "max-datapoint-count=10&"
        				+ "aggregator=sum&"
        				+ "ts=1413189009510&"
        				+ "downsampler=sum"
        		;
        HttpGet httpget = new HttpGet(url);
        
        for(int i = 0; i < 1; i++){
        	
			try {
				HttpResponse response = httpclient.execute(httpget);
//				HttpEntity resEntity = response.getEntity();
		        System.out.println(response.getStatusLine());
		        if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){
		            HttpEntity entity = response.getEntity();
		            //显示内容  
		            if (entity != null) {
		            	String str = EntityUtils.toString(entity);
//		            	str = str.replace("\\x", "\\u00");
//		            	System.out.println(str);
//		            	str = StringEscapeUtils.unescapeJava(str.replace("\\x", "\\u00"));
//		            	str = decode(str);
//		            	str = revert(str);
		            	JSONObject jobj = new JSONObject(str);
		            	Map<String, ClientNode> nodes = new HashMap<>();
//		            	
		            	JSONArray jarr = jobj.getJSONArray("time-series-group-list");
		            	int len = jarr.length();
		            	for(int m =0; m < len; m++){
		            		
		            		JSONObject item = jarr.optJSONObject(m);
		            		JSONObject group = item.getJSONObject("time-series-group");
		            		String action = group.getString("action");
		            		String clientCode = group.getString("client_code");
		            		String taskType = group.getString("task_type");
		            		
		            		JSONObject dp = item.getJSONObject("data-points");
		            		JSONArray dps = dp.getJSONArray("data-points");
		            		double point = dps.getDouble(0);
		            		
		            		ClientNode node = nodes.get(clientCode);
		            		if(node == null){
		            			node = new ClientNode();
		            			nodes.put(clientCode, node);
		            		}
		            		
		            		node.add(taskType, action, point);
//		            		node.
//		            		
//		            		Integer val = clientAction.get(action);
//		            		if(val == null){
//		            			clientAction.put(action, point);
//		            		}
//		            		else{
//		            			clientAction.put(action, clientAction.get(action) + point);
//		            		}
		            	}
		            	
		            	System.out.println(nodes.size());
		            	
		            	for(String node: nodes.keySet()){
		            		if(node.equals("ADSL_VMS_CD_101")){
		            			System.out.println(nodes.get(node).okRate());
		            		}
		            	}
		            }
		        }
		        
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
	}
	
	public static String revert(String str) {  
		  
        if (str != null && str.trim().length() > 0) {  
            String un = str.trim();  
            StringBuffer sb = new StringBuffer();  
            int idx = un.indexOf("\\u");  
            while (idx >= 0) {  
                if (idx > 0) {  
                    sb.append(un.substring(0, idx));  
                }  
  
                String hex = un.substring(idx + 2, idx + 2 + 4);  
                sb.append((char) Integer.parseInt(hex, 16));  
                un = un.substring(idx + 2 + 4);  
                idx = un.indexOf("\\u");  
            }  
            sb.append(un);  
            return sb.toString();  
        }  
        return "";  
    }  
	
    public static String decode(String unicodeStr) {  
        if (unicodeStr == null) {  
            return null;  
        }  
        StringBuffer retBuf = new StringBuffer();  
        int maxLoop = unicodeStr.length();  
        for (int i = 0; i < maxLoop; i++) {  
            if (unicodeStr.charAt(i) == '\\') {  
                if ((i < maxLoop - 5)  
                        && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr  
                                .charAt(i + 1) == 'U')))  
                    try {  
                        retBuf.append((char) Integer.parseInt(  
                                unicodeStr.substring(i + 2, i + 6), 16));  
                        i += 5;  
                    } catch (NumberFormatException localNumberFormatException) {  
                        retBuf.append(unicodeStr.charAt(i));  
                    }  
                else  
                    retBuf.append(unicodeStr.charAt(i));  
            } else {  
                retBuf.append(unicodeStr.charAt(i));  
            }  
        }  
        return retBuf.toString();  
    }  

}
