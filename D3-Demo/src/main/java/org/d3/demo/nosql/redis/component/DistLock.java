package org.d3.demo.nosql.redis.component;

import java.util.UUID;

import org.d3.demo.nosql.redis.Redis;

import redis.clients.jedis.Jedis;

public class DistLock {
	
	private static final String NS = "lock:";

	public String lock(String lockName, long timeout){
		return aquire(lockName, timeout);
	}
	
	public String aquire(String lockName, long timeout){
		Jedis conn = Redis.get();
		String identi = UUID.randomUUID().toString();
		long end = System.currentTimeMillis() + timeout;
		while(System.currentTimeMillis() < end){
			if(conn.setnx(NS + lockName, identi) == 1){
				return identi;
			}
		}
		return null;
	}
	
	
	
	public static void main(String[] args) {
		for(int i = 0; i < 20; i++){
			Jedis conn = Redis.get();
			System.out.println(conn);
			Redis.back(conn);
		}
	}
}
