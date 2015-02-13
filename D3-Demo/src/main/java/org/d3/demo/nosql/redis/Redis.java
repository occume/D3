package org.d3.demo.nosql.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {

	private static JedisPool pool;
	
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(5);
		config.setMaxIdle(5);
//		config.setMaxWait(1000 * 100);
		config.setTestOnBorrow(true);
//		pool = new JedisPool(config, "localhost");
		pool = new JedisPool(config, "10.8.92.9");
	}
	
	public static Jedis get(){
		return pool.getResource();
	}
	
	public static void back(Jedis jedis){
		pool.returnResource(jedis);
	}
	
	public static void execute(Invocable invoker){
		Jedis jedis = null;
		try{
			jedis = pool.getResource();
			invoker.invoke(jedis);
		}
		finally{
			pool.returnResource(jedis);
		}
	}
	
}
