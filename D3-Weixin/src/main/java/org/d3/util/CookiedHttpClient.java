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
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class CookiedHttpClient {

	public static void main(String[] args) throws IOException {
		getHeaders();
	}
	
	public static String userAgent = HttpHeader.nextUserAgent();
	private static int count = 0;
	private static int totalCount = 0;
	private static CloseableHttpClient httpclient;
	private static BasicCookieStore cookieStore;
	
	static{
		cookieStore = new BasicCookieStore();
		CookieSpecProvider easySpecProvider = new CookieSpecProvider() {
			public CookieSpec create(HttpContext context) {

				return new BrowserCompatSpec() {
					@Override
					public void validate(Cookie cookie, CookieOrigin origin)
							throws MalformedCookieException {
						// Oh, I am easy
//						System.out.println(cookie);
//						System.out.println(origin);
						if(origin.getPath().contains("antispider")){
							System.err.println("anti spider comes");
							cookieStore.clear();
							CookiedHttpClient.changeUserAgent();
							try {
								Thread.sleep(300000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
					}
				};
			}

		};
		Registry<CookieSpecProvider> r = RegistryBuilder
				.<CookieSpecProvider> create()
				.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
				.register(CookieSpecs.BROWSER_COMPATIBILITY,
						new BrowserCompatSpecFactory())
				.register("easy", easySpecProvider).build();

		RequestConfig requestConfig = RequestConfig.custom()
				.setCookieSpec("easy").setSocketTimeout(10000)
				.setConnectTimeout(10000).build();
		
		HttpHost proxy = new HttpHost("121.9.206.83", 80);
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		
		httpclient = HttpClients.custom()
				.setDefaultCookieSpecRegistry(r)
				.setDefaultRequestConfig(requestConfig)
				.setDefaultCookieStore(cookieStore)
//				.setProxy(proxy)
//				.setRoutePlanner(routePlanner)
//				.setDefaultCredentialsProvider(credentialsProvider)
				.build();
	}
	
	public static void changeUserAgent(){
		userAgent = HttpHeader.nextUserAgent();
	}
	
	public static int getTotalCount(){
		return totalCount;
	}
	
	public static String doArticleGet(String url){
		speedDown();
		String content = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeaders(getArticleHeaders());
			System.out.println("---------------" + httpGet.getAllHeaders().length);
			content = doGet0(httpGet);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public static String doGet(String url){
		speedDown();
		String content = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeaders(getHeaders());
			content = doGet0(httpGet);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	private static void speedDown(){
		totalCount++;
		if(totalCount == 150){
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			totalCount = 0;
		}
		count++;
		if(count == 30){
			count = 0;
			changeUserAgent();
		}
	}
	
	public static String doGet0(HttpGet httpGet) throws ClientProtocolException, IOException{
		
		String content = "";
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
			content = getContent(response);
			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies.isEmpty()) {
				System.out.println("no cookies");
			} else {
				// 读取Cookie
				for (int i = 0; i < cookies.size(); i++) {
//					System.out.println("post request - "
//							+ cookies.get(i).toString());
				}
			}

		} finally {
			if (response != null)
				response.close();
		}
		return content;
	}
	
	private static Header[] getArticleHeaders(){
		List<Header> hds = getHeaders0();
		hds.add(new BasicHeader("Cookie", "ABTEST=1|1423130790|v1; IPLOC=CN3100; SUID=9196983D6F1C920A0000000054D340A6; SUID=9196983D60C80D0A0000000054D340A7; weixinIndexVisited=1; SUV=00FD783A3D98969154D341868E392096; sct=1; SNUID=505759FDC0C4CF9543739ADCC1D7E77A; wapsogou_qq_nickname=asdf"));
		hds.add(new BasicHeader(
				"User-Agent", 
//				userAgent);
//				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
//	"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
//	"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/28.0"
				userAgent
				));
		return hds.toArray(new Header[hds.size()]);
	}
	
	private static Header[] getHeaders(){
		List<Header> hds = getHeaders0();
		hds.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:28.0) Gecko/20100101 Firefox/28.0"));
		return hds.toArray(new Header[hds.size()]);
	}
	
	public static List<Header> getHeaders0(){
		List<Header> hds = new ArrayList<>(10);
		hds.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
		hds.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
		hds.add(new BasicHeader("Accept-Language", "en-US,en;q=0.5"));
		hds.add(new BasicHeader("Cache-Control", "max-age=0"));
		hds.add(new BasicHeader("Connection", "keep-alive"));
		hds.add(new BasicHeader("Host", "weixin.sogou.com"));
//		hds.add(new BasicHeader("Referer", "http://weixin.sogou.com/weixin?type=2&query=%E5%85%B0%E5%B7%9E&fr=sgsearch&ie=utf8&_ast=1421146752&_asf=null&w=01029901&p=40040100&dp=1&cid=null"));
		hds.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
		return hds;
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

	public static String doPost(String url, HashMap<String, String> hashMap)
			throws IOException {
		String content = "";
		BasicCookieStore cookieStore = new BasicCookieStore();
		CookieSpecProvider easySpecProvider = new CookieSpecProvider() {
			public CookieSpec create(HttpContext context) {

				return new BrowserCompatSpec() {
					@Override
					public void validate(Cookie cookie, CookieOrigin origin)
							throws MalformedCookieException {
						// Oh, I am easy
					}
				};
			}

		};
		Registry<CookieSpecProvider> r = RegistryBuilder
				.<CookieSpecProvider> create()
				.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
				.register(CookieSpecs.BROWSER_COMPATIBILITY,
						new BrowserCompatSpecFactory())
				.register("easy", easySpecProvider).build();

		RequestConfig requestConfig = RequestConfig.custom()
				.setCookieSpec("easy").setSocketTimeout(10000)
				.setConnectTimeout(10000).build();

		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultCookieSpecRegistry(r)
				.setDefaultRequestConfig(requestConfig)
				.setDefaultCookieStore(cookieStore)

				.build();
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
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

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
}