package org.d3.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HttpHeader {

	private static List<String> userAgents = new ArrayList<String>(16);
	private static int index = 0;
	
	static{
//		userAgents.add("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        userAgents.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0");
        userAgents.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
        userAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/22.0");
        userAgents.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.72 Safari/537.36");
        userAgents.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
        userAgents.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:22.0) Gecko/20100101 Firefox/21.0");
        userAgents.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0");

        userAgents.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; SLCC1)");                                                               // IE7(Vista)
        userAgents.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)");                                                         // IE8(Win7)
        userAgents.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; WOW64; Trident/4.0)");                                                  // IE8(Win2k3 x64)
        userAgents.add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");                                                         // IE9(Win7)
        userAgents.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0)");                                                 // IE10(Win8)
        userAgents.add("Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");                                                    // IE11(Win8.1)
        userAgents.add("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");                                                // Firefox27
        userAgents.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.7) Gecko/20100625 Firefox/3.6.7");                                // Firefox3.6
        userAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:24.0) Gecko/20100101 Firefox/24.0");                                       // Firefox(Mac)
        userAgents.add("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.58 Safari/537.36");            // Chrome
        userAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9) AppleWebKit/537.71 (KHTML, like Gecko) Version/7.0 Safari/537.71");           // Safari7(Mac)
        userAgents.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533.21.1 (KHTML, like Gecko) Version/5.0.5 Safari/533.21.1");  // Safari7(Windows)
	
        userAgents.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
        userAgents.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        userAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:28.0) Gecko/20100101 Firefox/28.0");
        
//        userAgentArr = (String[]) userAgents.
	}
	
	public static String randomUserAgent(){
		Random r = new Random();
		int index = r.nextInt(userAgents.size());
		return userAgents.get(index);
	}
	
	public static String nextUserAgent(){
		if(index >= userAgents.size())
			index = 0;
		return userAgents.get(index++);
	}
}
