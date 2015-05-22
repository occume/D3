package org.d3.session;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class RedisSessionManager extends LifecycleMBeanBase implements Manager{
	
	private final Log log = LogFactory.getLog(RedisSessionManager.class);
	
	private Context context;
	
	private boolean distributable;
	
	private int	maxInactiveInterval;
	
	protected SessionIdGenerator sessionIdGenerator = null;
	
	private byte[] TOMCAT_SESSION_KEY;
	
	private byte[] TOMCAT_SESSION_ACCESS_KEY;

	@Override
	public void load() throws ClassNotFoundException, IOException {
		// do nothing
	}

	@Override
	public void unload() throws IOException {
		// do nothing
	}
	@Override
    public void startInternal() throws LifecycleException {
		
		String BASE_KEY = "TOMREDIS:";
		TOMCAT_SESSION_KEY = BASE_KEY.getBytes();
		TOMCAT_SESSION_ACCESS_KEY = (BASE_KEY + "ACCESS:").getBytes();
		
        SessionIdGenerator sessionIdGenerator = getSessionIdGenerator();
        if(sessionIdGenerator == null){
        	sessionIdGenerator = new StandardSessionIdGenerator();
        	setSessionIdGenerator(sessionIdGenerator);
        }
        
        if(context != null){
        	context.getPipeline().addValve(new RedisSessionValve(this));
        }
        setState(LifecycleState.STARTING);
        log.info("RedisSessionManager is started..." + getClass().getClassLoader());
    }
	
	@Override
	public Session findSession(String id) throws IOException {
		
		if(id == null) return null;
		
		Session session = loadSession(id);
		
		return session;
	}
	
	@Override
	public Session createSession(String sessionId) {
		
		RedisSession session = (RedisSession) createEmptySession();
		session.setNew(true);
		session.setValid(true);
		session.setCreationTime(System.currentTimeMillis());
		String id = sessionId;
		if(id == null){
			id = sessionIdGenerator.generateSessionId();
		}
		session.setId(id);
		return session;
	}
	
	private Session loadSession(String id){
		
		final byte[] key = id.getBytes();
		final Object[] holder = {null};
		
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				if(jedis.hexists(TOMCAT_SESSION_KEY, key)){
					byte[] value = jedis.hget(TOMCAT_SESSION_KEY, key);
					
					RedisSession redisSession = (RedisSession) createEmptySession();
					try {
						redisSession.readObjectData(byte2stream(value));
						holder[0] = (redisSession);
					}
					catch (Exception e) {
						log.error("read session from byte array error, key: " + key, e);
					}
				}
				else{
					log.info("session id not exist in redis");
				}
			}
		});
		return (Session) holder[0];
	}
	
	private ObjectInputStream byte2stream(byte[] buf){
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(buf)));
		} catch (IOException e) {
			stream = null;
			log.error("Invalid byte array, {}", e);
		}
		return stream;
	}
	
	public void save(RedisSession session){
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final byte[] key = session.getId().getBytes();
		ObjectOutputStream stream;
		try {
			stream = new ObjectOutputStream(new BufferedOutputStream(bos));
			session.writeObjectData(stream);
			stream.flush();
		} catch (IOException e) {
			log.error("serialize a session failure, id: " + session.getId(), e);
			return;
		}
		
		final byte[] value = bos.toByteArray();
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				jedis.hset(TOMCAT_SESSION_KEY, key, value);
				jedis.zadd(TOMCAT_SESSION_ACCESS_KEY, System.currentTimeMillis(), key);
			}
		});
		
//		updateAccessTime(session);
//		if(log.isInfoEnabled()){
//			log.info("save a session, length: " + value.length);
//		}
	}
	
	public void updateAccessTime(RedisSession session){
		
		final byte[] key = session.getId().getBytes();
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				jedis.zadd(TOMCAT_SESSION_ACCESS_KEY, System.currentTimeMillis(), key);
			}
		});
	}

	@Override
	public Session[] findSessions() {
		return null;
	}

	@Override
	public Container getContainer() {
		return context;
	}

	@Override
	public void setContainer(Container container) {
		if(container instanceof Context || container == null){
			this.context = (Context) container;
		}
	}

	@Override
	public Context getContext() {
		return context;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public boolean getDistributable() {
		return this.distributable;
	}

	@Override
	public void setDistributable(boolean distributable) {
		this.distributable = distributable;
	}

	@Override
	public int getMaxInactiveInterval() {
		return this.maxInactiveInterval;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}

	@Override
	public SessionIdGenerator getSessionIdGenerator() {
		return this.sessionIdGenerator;
	}

	@Override
	public void setSessionIdGenerator(SessionIdGenerator sessionIdGenerator) {
		this.sessionIdGenerator = sessionIdGenerator;
	}

	@Override
	public int getSessionIdLength() {
		return 0;
	}

	@Override
	public void setSessionIdLength(int idLength) {
		
	}

	@Override
	public long getSessionCounter() {
		return 0;
	}

	@Override
	public void setSessionCounter(long sessionCounter) {
		
	}

	@Override
	public int getMaxActive() {
		return 0;
	}

	@Override
	public void setMaxActive(int maxActive) {
		
	}

	@Override
	public int getActiveSessions() {
		return 0;
	}

	@Override
	public long getExpiredSessions() {
		return 0;
	}

	@Override
	public void setExpiredSessions(long expiredSessions) {
		
	}

	@Override
	public int getRejectedSessions() {
		return 0;
	}

	@Override
	public int getSessionMaxAliveTime() {
		return 0;
	}

	@Override
	public void setSessionMaxAliveTime(int sessionMaxAliveTime) {
		
	}

	@Override
	public int getSessionAverageAliveTime() {
		return 0;
	}

	@Override
	public int getSessionCreateRate() {
		return 0;
	}

	@Override
	public int getSessionExpireRate() {
		return 0;
	}

	@Override
	public void add(Session session) {
		/**
		 * this method is called when set the session id
		 */
		if(session instanceof RedisSession){
			save((RedisSession)session);
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		
	}

	@Override
	public void changeSessionId(Session session) {
		
	}

	@Override
	public void changeSessionId(Session session, String newId) {
		
	}

	@Override
	public Session createEmptySession() {
		return new RedisSession(this);
	}

	@Override
	public void remove(Session session) {
		
		final byte[] key = session.getId().getBytes();
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				jedis.hdel(TOMCAT_SESSION_KEY, key);
			}
		});
		
		if(log.isInfoEnabled()){
			log.info("remove a session, id: " + session.getId());
		}
	}

	@Override
	public void remove(Session session, boolean update) {
		
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		
	}

	@Override
	public void backgroundProcess() {
		/**
		 * 
		 */
		expire();
	}
	
	public void expire(){
		
		final long now = System.currentTimeMillis();
		final long expire = now - 30 * 60 * 1000;

		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				Set<byte[]> result = jedis.zrangeByScore(TOMCAT_SESSION_ACCESS_KEY, 0, expire);
				if(result.size() > 0){
					jedis.zremrangeByScore(TOMCAT_SESSION_ACCESS_KEY, 0, expire);
					Pipeline pipeline = jedis.pipelined();
					pipeline.multi();
					for(byte[] key: result){
						pipeline.hdel(TOMCAT_SESSION_KEY, key);
					}
					pipeline.exec();
				}
			}
		});
	}

	@Override
	protected String getDomainInternal() {
		return null;
	}

	@Override
	protected String getObjectNameKeyProperties() {
		 StringBuilder name = new StringBuilder("type=Manager");

	      name.append(",host=");
	      name.append(context.getParent().getName());

	        name.append(",context=");
	        String contextName = context.getName();
	        if (!contextName.startsWith("/")) {
	            name.append('/');
	        }
	        name.append(contextName);

	        return name.toString();
	}

	@Override
	protected void stopInternal() throws LifecycleException {
		
	}

}
