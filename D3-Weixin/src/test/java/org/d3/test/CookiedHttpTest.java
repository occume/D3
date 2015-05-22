package org.d3.test;

import java.io.IOException;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.d3.util.Http;

public class CookiedHttpTest {

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		for( ; scanner.hasNext(); ){
			scanner.next();
			doRequest();
		}
	}

	public static void doRequest(){
		try {
			String content = Http.doGet("http://localhost/cluster/test");
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
