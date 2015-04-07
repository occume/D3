package org.d3.session;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;


public class RedisSessionValve extends ValveBase {
	
	private static final Log log = LogFactory.getLog(RedisSessionValve.class);
	
	private RedisSessionManager manager;
	
	public RedisSessionValve(RedisSessionManager manager){
		this.manager = manager;
	}

	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {
		
		getNext().invoke(request, response);
		
		try {
			Files.deleteIfExists(Paths.get("D:/log/catalina.txt"));
			printObject(request.getContext());
			printObject(request.getContext().getParent());
			printObject(request.getContext().getParent().getParent());
			printObject(request.getConnector());
			printObject(request.getCookies());
			printObject(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Session session = request.getSessionInternal(false);
		if(session != null){
			if(session.isValid()){
				if(!(session instanceof RedisSession)) return;
				RedisSession redisSession = (RedisSession) session;
				if(redisSession.isWrited()){
					manager.save(redisSession);
					if(log.isInfoEnabled()){
						log.info("session changed, id: " + session.getId());
					}
				}
				else if(redisSession.isReaded()){
					manager.updateAccessTime(redisSession);
				}
			}
			else{
				manager.remove(session);
			}
		}
	}
	
	public static void printObject(Object add){
		
		if(add == null) return;
		
		Map<String, String> mnames = new HashMap<>();
		int maxLen = -1;
		
		Method[] methods = add.getClass().getMethods();
		for(Method m: methods){
			String mName = m.getName();
			if(		mName.contains("start") || 
					mName.contains("stop") || 
					mName.contains("reload") ||
					mName.contains("pause") 
					){
				continue;
			}
				Object ret = null;
				try{
					ret = m.invoke(add);
					String value;
					if(m.getName().length() > maxLen){
						maxLen = m.getName().length();
					}
					if(ret.getClass().isArray()){
						value = Arrays.toString((Object[])ret);
					}
					else{
						value = String.valueOf(ret);
					}
					
					mnames.put(m.getName(), value);
				}
				catch(Throwable e){
					ret = "ERR";
				}
		}
		
		StringBuilder b = new StringBuilder();
		
		b.append("\n");
		b.append("=======================   "+ add.getClass().getName() +"   =======================");
		b.append("\n\n");
		
		for(String key: mnames.keySet()){
			int dis = maxLen - key.length();
			
			b.append(key);
			for(int i = 0; i < dis; i++){
				b.append("-");
			}
			
			b.append("  ")
			 .append(mnames.get(key))
			 .append("\n")
			 ;
		}
		
		try {
			if(!Files.exists(Paths.get("D:/log/catalina.txt")))
				Files.createFile(Paths.get("D:/log/catalina.txt"));
			Files.write(Paths.get("D:/log/catalina.txt"), b.toString().getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
