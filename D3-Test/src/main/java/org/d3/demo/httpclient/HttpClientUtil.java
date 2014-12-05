package org.d3.demo.httpclient;

import java.io.IOException;






import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpClientUtil {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HttpClientUtil.class);

	private static DefaultHttpClient httpClient;

	static {
		PoolingClientConnectionManager connectPool = new PoolingClientConnectionManager();
		connectPool.setMaxTotal(20);
		httpClient = new DefaultHttpClient(connectPool);
		httpClient.getParams().setParameter("http.socket.timeout", 5000);
		httpClient.getParams().setParameter("http.connection.timeout", 5000);
	}
	
	public static DefaultHttpClient getClient(){
		return httpClient;
	}

	public static String httpGet(String url) {
		HttpGet httpMethod = new HttpGet(url);
		try {
			return getContent(httpClient.execute(httpMethod));
		} catch (IOException e) {
			LOGGER.error("", e.getMessage());
			return null;
		}	catch(Exception e)
		{
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

	public static String httpPost(String url, String upload) {
		HttpPost httpMethod = new HttpPost(url);
		try {
			httpMethod.setEntity(new StringEntity(upload, ContentType.create(
					"text/plain", "UTF-8")));
			return getContent(httpClient.execute(httpMethod));
		} catch (IOException e) {
			LOGGER.error("", e);
		} finally {
			httpMethod.releaseConnection();
		}
		return null;
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
