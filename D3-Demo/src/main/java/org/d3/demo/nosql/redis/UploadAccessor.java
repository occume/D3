package org.d3.demo.nosql.redis;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

import org.d3.demo.util.Bytes;

import com.google.common.io.Files;

import redis.clients.jedis.Jedis;

public class UploadAccessor{
	
	private static final String UPLOAD			= "octopus:upload:";
	private static final String UPLOAD_BODY		= "body";
	private static final String UPLOAD_LENGTH	= "Content-Length";
	private static final String UPLOAD_ETAG		= "ETag";
	private static final String UPLOAD_MODIFIED	= "Last-Modified";
	private static final String UPLOAD_TYPE		= "Content-Type";

	private static TimeZone GMT = TimeZone.getTimeZone("GMT");

	public static void post(final String path, String contentType, final byte[] body) {
		final HashMap<String, String> map = new HashMap<>();
		map.put(UPLOAD_LENGTH, "" + body.length);
		try {
			map.put(UPLOAD_ETAG, '"' + Bytes.toHexLower(MessageDigest.
					getInstance("MD5").digest(body)) + '"');
		} catch (GeneralSecurityException e) {
		}
		SimpleDateFormat format = new SimpleDateFormat("EEE',' dd-MMM-yyyy " +
				"HH:mm:ss 'GMT'", Locale.US);
		format.setTimeZone(GMT);
		map.put(UPLOAD_MODIFIED, format.format(new Date()));
		map.put(UPLOAD_TYPE, contentType);
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				jedis.hset((UPLOAD + path).getBytes(), UPLOAD_BODY.getBytes(), body);
				jedis.hmset(UPLOAD + path, map);
			}
		});
	}

	public static void main(String...strings) throws IOException{
		File file = new File("d:\\Users\\d_Jin\\Desktop\\work\\lib.gz");
		System.out.println(file.getName());
//		InputStream in = new FileInputStream(file);
//		Files.toByteArray(file);
		post(file.getName(), "application/octet-stream", Files.toByteArray(file));
	}
}