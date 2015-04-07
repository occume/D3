package org.d3.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

	private static CloseableHttpClient httpClient;

	static {
		
		RequestConfig requestConfig = RequestConfig.custom()
			.setConnectTimeout(5000)
			.setSocketTimeout(5000)
			.build();
		
		PoolingHttpClientConnectionManager connectPool = new PoolingHttpClientConnectionManager();
		connectPool.setMaxTotal(1000);
		connectPool.setDefaultMaxPerRoute(1000);
		httpClient = HttpClientBuilder.create()
				.setConnectionManager(connectPool)
				.setDefaultRequestConfig(requestConfig)
				.setMaxConnTotal(1000)
				.setMaxConnPerRoute(100)
				.build();
	}
	
	public static CloseableHttpClient getClient(){
		return httpClient;
	}

	public static String doGet(String url) {
		HttpGet httpMethod = new HttpGet(url);
		
		try {
			return getContent(httpClient.execute(httpMethod));
		}catch(Exception e){
			LOGGER.error("", e.getMessage());
		}finally{
			httpMethod.releaseConnection();
		}
		return null;
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
		for(Header tempHeader:headers){
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
