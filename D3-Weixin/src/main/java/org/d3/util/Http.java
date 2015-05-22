package org.d3.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.d3.std.Printer;

public class Http {
	
	private static CloseableHttpClient httpclient;
	private static BasicCookieStore cookieStore;
	
	static{
		
		cookieStore = new BasicCookieStore();
		
//		Printer.printObject(cookieStore);
		
		CookieSpecProvider easySpecProvider = getCookieSpecProvider();
		Registry<CookieSpecProvider> r = 
				RegistryBuilder
				.<CookieSpecProvider> create()
				.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
				.register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
				.register("easy", easySpecProvider).build();

//		RequestConfig requestConfig = 
//				RequestConfig
//				.custom()
////				.setCookieSpec("easy").setSocketTimeout(10000)
//				.setConnectTimeout(10000).build();
		
		HttpHost proxy = new HttpHost("121.9.206.83", 80);
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		
		httpclient = HttpClients.custom()
//				.setDefaultCookieSpecRegistry(r)
//				.setDefaultRequestConfig(requestConfig)
//				.setDefaultCookieStore(cookieStore)
//				.setProxy(proxy)
//				.setRoutePlanner(routePlanner)
//				.setDefaultCredentialsProvider(credentialsProvider)
				.build();
		Printer.printObject(httpclient);
	}
	
	private static CookieSpecProvider getCookieSpecProvider(){
		return new CookieSpecProvider() {
			public CookieSpec create(HttpContext context) {
				return createBrowserCompatSpec();
			}
		};
	}
	
	private static BrowserCompatSpec createBrowserCompatSpec(){
		 return new BrowserCompatSpec() {
				@Override
				public void validate(Cookie cookie, CookieOrigin origin)
						throws MalformedCookieException {
//					Printer.printObject(cookie);
//					Printer.printObject(origin);
				}
			};
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

	private static boolean isNotEmpty(String str) {
		return str != null && str.trim().length() > 0;
	}
	
	public static String doGet(String url) throws ClientProtocolException, IOException{
		
		String content = "";
		CloseableHttpResponse response = null;
		try {
			printCookies();
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:28.0) Gecko/20100101 Firefox/28.0");
			response = httpclient.execute(httpGet);
			content = getContent(response);
			printCookies();
		} finally {
			if (response != null)
				response.close();
		}
		return content;
	}
	
	private static void printCookies(){
		List<Cookie> cookies = cookieStore.getCookies();
		if (cookies.isEmpty()) {
			System.out.println("no cookies");
		} else {
			// 读取Cookie
			for (int i = 0; i < cookies.size(); i++) {
				System.out.println("post request - "
						+ cookies.get(i).toString());
			}
		}
	}
	
	public static String doPost(String url, HashMap<String, String> hashMap)
			throws IOException {
		String content = "";
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (hashMap != null) {
			Iterator<String> it = hashMap.keySet().iterator();
			while (it.hasNext()) {

				String key = it.next();
				String value = hashMap.get(key);
				nvps.add(new BasicNameValuePair(key, value));
			}
		}

		HttpPost httpPost = new HttpPost(url);
		
		httpPost.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate");
		httpPost.setHeader("Accept-Language", "en-US,en;q=0.5");
		httpPost.setHeader("Cache-Control", "max-age=0");
		httpPost.setHeader("Connection", "keep-alive");

		httpPost.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:28.0) Gecko/20100101 Firefox/28.0");
		// 如果参数是中文，需要进行转码
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			for (Header s : response.getAllHeaders()) {
				System.out.println("post header====" + s);
			}
			InputStream is = entity.getContent();
			BufferedReader in = new BufferedReader(new InputStreamReader(is,
					Consts.UTF_8));
			String line = "";
			while ((line = in.readLine()) != null) {
				content += line;
			}

			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				// 读取Cookie
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("post request - "
							+ cookies.get(i).toString());
				}
			}

		} finally {
			if (response != null)
				response.close();
		}
		return content;
	}
	
	public static void main(String...strings){
		
	}
}
