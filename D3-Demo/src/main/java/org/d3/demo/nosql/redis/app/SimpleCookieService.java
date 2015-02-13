package org.d3.demo.nosql.redis.app;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.d3.demo.nosql.redis.Invocable;
import org.d3.demo.nosql.redis.Redis;
import org.d3.std.Stopwatch;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import redis.clients.jedis.Jedis;

public class SimpleCookieService{

	public static final int LIMIT = 10;
	
	public String checkToken(final String token) {
		final List<String> holder = new LinkedList<>();
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				String result = jedis.hget("login:", token);
				holder.add(result);
			}
			
		});
		return holder.get(0);
	}
	
	public void updateToken(final String token, final String user, final String item) {
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				long timeStamp = System.currentTimeMillis();
				jedis.hset("login:", token, user);
				jedis.zadd("recent:", timeStamp, token);
				if(!Strings.isNullOrEmpty(item)){
					jedis.zadd("viewed:" + token, timeStamp, item);
					jedis.zremrangeByRank("viewed:" + token, 0, -26);
					jedis.zincrby("viewed:", -1, item);
				}
			}
		});
	}
	
	public void addToCart(final String token, final String item, final int count) {
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				if(count <= 0){
					jedis.hdel("cart:" + token, item);
				}
				else{
					jedis.hset("cart:" + token, item, String.valueOf(count));
				}
			}
		});
	}
	
	public void cleanSessions() {
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				long size = jedis.zcard("recent:");
				if(size <= LIMIT) return;
				long endIndex = Math.min(size - LIMIT, 10);
				Set<String> tokens = jedis.zrange("recent:", 0, endIndex - 1);
				
				StringBuffer sb = new StringBuffer();
				for(String token: tokens){
					sb.append("viewed:" + token + ",");
					sb.append("cart:" + token + ",");
				}
				
				String[] sessions = sb.toString().split(",");
				jedis.del(sessions);
				String[] tks = tokens.toArray(new String[tokens.size()]);
				jedis.hdel("login:", tks);
				jedis.zrem("recent:", tks);
			}
		});
	}

	public void scheduleRowCache(final String rowId, final long delay) {
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				jedis.zadd("delay:", delay, rowId);
				jedis.zadd("schedule:", System.currentTimeMillis(), rowId);
			}
		});
	}
	
	public void cacheRows(){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				jedis.zrange("schedule:", 0, 0);
//				jedis.zscore(key, member);
			}
		});
	}
	
	public void rescaleViewed(){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
//				jedis.zinterstore("viewed:", "{\"viewed:\":0.5}");
//				jedis.zinterstore("viewed:",);
			}
		});
	}

	public static void main(String...strings){
		SimpleCookieService ss = new SimpleCookieService();
		String token = "token";
		String user = "user";
		
		Stopwatch sw = Stopwatch.newStopwatch();
		for(int i = 0; i < 100; i++){
			ss.updateToken(token + i, user + i, "item" + i);
		}
		System.out.println(sw.longTime());
//		ss.rescaleViewed();
//		String cc = ss.checkToken(token);
//		ss.cleanSessions();
//		System.out.println();
//		AtomicReference
	}

}