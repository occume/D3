package org.d3.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Http.class);

	private static CloseableHttpClient httpClient;

	static {
		
		RequestConfig requestConfig = RequestConfig.custom()
			.setConnectTimeout(5000)
			.setSocketTimeout(5000)
			.build();
		
		PoolingHttpClientConnectionManager connectPool = new PoolingHttpClientConnectionManager();
		connectPool.setMaxTotal(100);
		connectPool.setDefaultMaxPerRoute(100);
		httpClient = HttpClientBuilder.create()
				.setConnectionManager(connectPool)
				.setDefaultRequestConfig(requestConfig)
				.setMaxConnTotal(100)
				.setMaxConnPerRoute(10)
				.build();
	}
	
	public static CloseableHttpClient getClient(){
		return httpClient;
	}

	public static String get(String url) {
		HttpGet httpMethod = new HttpGet(url);
		
		try {
			return getContent(httpClient.execute(httpMethod));
		} catch (Exception e) {
			LOGGER.error("", e.getMessage());
			return null;
		}
		finally {
			httpMethod.releaseConnection();
		}
	}
	
	public static List<Header> httpGetHeaders(String url)
	{
		HttpGet httpMethod = new HttpGet(url);
		
		try {
			return getHeader(httpClient.execute(httpMethod));
		} catch (IOException e) {
			LOGGER.error("", e.getMessage());
		} finally {
			httpMethod.releaseConnection();
		}
		return null;
	}
	
	public static String post(String url, String paramString) {
		
		HttpPost httpost = new HttpPost(url);
	    httpost.setEntity(new StringEntity(paramString, ContentType.create("text/plain", "UTF-8")));  
       
		try {
			return getContent(httpClient.execute(httpost));
		} catch (IOException e) {
			LOGGER.error("", e);
		} finally {
			httpost.releaseConnection();
		}
		return null;
	}

	public static String httpPost(String url, Map<String, String> params) {
		HttpPost httpMethod = postForm(url, params);
		try {
			return getContent(httpClient.execute(httpMethod));
		} catch (IOException e) {
			LOGGER.error("", e);
		} finally {
			httpMethod.releaseConnection();
		}
		return null;
	}
	
	private static HttpPost postForm(String url, Map<String, String> params){  
        
        HttpPost httpost = new HttpPost(url);
        if(params != null){
	        List<NameValuePair> nvps = new ArrayList <NameValuePair>();  
	          
	        Set<String> keySet = params.keySet();  
	        for(String key : keySet) {  
	            nvps.add(new BasicNameValuePair(key, params.get(key)));  
	        }  
	          
	        try {   
	            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	        }  
        }
        return httpost;  
    }
	
	public static List<Header> getHeader(HttpResponse httpResponse)
	{
		Header[] headers = httpResponse.getAllHeaders();
		List<Header> headerList=new LinkedList<Header>();
		for(Header tempHeader:headers)
		{
			headerList.add(tempHeader);
		}
		return headerList;
	}

	private static String getContent(HttpResponse httpResponse)
			throws IOException {
		if (HttpStatus.SC_OK != httpResponse.getStatusLine().getStatusCode()) {
			return null;
		}
		if (httpResponse.getEntity() != null) {
			return EntityUtils.toString(httpResponse.getEntity(),
					getEntityEncode(httpResponse.getEntity()));
		}
		return null;
	}

	private static String getEntityEncode(HttpEntity entity) {
		if (entity.getContentEncoding() != null
				&& isNotEmpty(entity.getContentEncoding().getValue())) {
			return entity.getContentEncoding().getValue();
		}
		return "utf8";
	}

	static boolean isNotEmpty(String str) {
		return str != null && str.trim().length() > 0;
	}
}
