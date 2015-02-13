package org.d3.demo.nosql.redis;

import java.util.List;

import com.google.common.collect.Lists;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class Sharding {
	
	private static ShardedJedisPool pool;
	
	static{
		
		JedisPoolConfig config = new JedisPoolConfig();
		config.setTestOnBorrow(true);
//		config.setMaxActive(100);
		config.setMaxIdle(50);
//		config.setMaxWait(10000);
		
		List<JedisShardInfo> shards = Lists.newArrayList(
				new JedisShardInfo("localhost"), new JedisShardInfo("10.2.5.82"));
		pool = new ShardedJedisPool(config, shards);
	}

	public static void main(String[] args) {
		ShardedJedis one = pool.getResource();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            String result = one.set("spn" + i, "n" + i);
        	System.out.println(one);
        }
        long end = System.currentTimeMillis();
        pool.returnResource(one);
        System.out.println("Simple@Pool SET: " + ((end - start)/1000.0) + " seconds");
	}
}
