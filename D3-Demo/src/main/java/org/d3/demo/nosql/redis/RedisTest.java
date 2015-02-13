package org.d3.demo.nosql.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xqbase.util.Bytes;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTest {
	
	static JedisPool pool;
	static int POOL_NUM;
	public static final String NEXT_LINE = "(*_*)";
	static {
		
		JedisPoolConfig config = new JedisPoolConfig();
//		config.setMaxActive(100);
		config.setMaxIdle(5);
//		config.setMaxWait(1000 * 100);
		config.setTestOnBorrow(true);
		pool = new JedisPool(config, "localhost");
//		pool = new JedisPool(config, "10.2.5.82");
	}
	
	public static void info(){
		Jedis jedis = pool.getResource();
		String info = jedis.info();
		System.out.println(info);
		pool.returnResource(jedis);
	}
	
	public static void hmset(Map<byte[], byte[]> map){
		Jedis jedis = pool.getResource();
		jedis.hmset("myhash".getBytes(), map);
		pool.returnResource(jedis);
	}
	
	public static List<byte[]> hmget(byte[] filed){
		List<byte[]> result;
		Jedis jedis = pool.getResource();
		result = jedis.hmget("myhash".getBytes(), filed);
		pool.returnResource(jedis);
		return result;
	}
	
	public static void set(String key, String value){
		Jedis jedis = pool.getResource();
		jedis.set("sync:" + key, value);
		pool.returnResource(jedis);
	}
	
	public static String get(String key){
		Jedis jedis = pool.getResource();
		String ret = jedis.get("sync:" + key);
		pool.returnResource(jedis);
		return ret;
	}
	
	public static void peek(){
		String v = get("k1");
		System.out.println(v);
	}
	
	public static void clear(){
		set("k1", "0");
	}
	
	public static void increment(){
		String v = get("k1");
		int intv = Integer.valueOf(v);
		set("k1", String.valueOf(++intv));
	}
	
	static final int loops = 10000;
	
	public static void doIncrement(){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
//				String name = Thread.currentThread().getName();
				for(int i = 0; i < loops; i++){
					increment();
				}
			}
		});
		t.start();
//		try {
//			t.join();
//		} catch (InterruptedException e) {
//		}
	}

	public static void main(String[] args) {
//		clear();
		Map<byte[], byte[]> map = new HashMap<>();
		for(int i = 0; i < 1000000; i++)
//			doIncrement();
			map.put(Bytes.fromInt(i), ("value").getBytes());
//		hmset(map);
		info();
//		List<byte[]> values = hmget(Bytes.fromInt(0));
//		System.out.println(new String(values.get(0)));
//		peek();
		
	}
	
}
