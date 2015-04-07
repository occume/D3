package org.d3.session;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {

	private static JedisPool pool;
	
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(5);
		config.setMaxIdle(5);
		config.setTestOnBorrow(true);
		
		pool = new JedisPool(config, "localhost");
	}
	
	public static void main(String...strings){
		execute(new Invocable() {
			
			@Override
			public void invoke(Jedis jedis) {
				System.out.println(jedis.exists("123"));
			}
		});
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
