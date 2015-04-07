package org.d3.session.test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.d3.session.RedisSession;

public class SessionSerialize {

	public static void main(String[] args) {
		serialize();
	}
	
	public static void serialize(){
		RedisSession session = new RedisSession(null);
		session.setNew(true);
		session.setValid(true);
		session.setCreationTime(System.currentTimeMillis());
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ObjectOutputStream stream;
		try {
			stream = new ObjectOutputStream(new BufferedOutputStream(bos));
			stream.writeLong(Long.valueOf(session.getCreationTime()));
	        stream.writeLong(Long.valueOf(session.getLastAccessedTime()));
	        stream.writeObject(Integer.valueOf(session.getMaxInactiveInterval()));
	        stream.writeObject(Boolean.valueOf(session.isNew()));
	        stream.writeObject(Boolean.valueOf(session.isValid()));
	        stream.writeObject(Long.valueOf(session.getThisAccessedTime()));
	        stream.writeObject(session.getId());
	        stream.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println(bos.toByteArray().length);
	}

}
