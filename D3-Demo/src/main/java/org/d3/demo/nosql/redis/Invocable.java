package org.d3.demo.nosql.redis;

import redis.clients.jedis.Jedis;

public interface Invocable {
	public void invoke(Jedis jedis);
}