package org.d3.session;

import redis.clients.jedis.Jedis;

public interface Invocable {
	public void invoke(Jedis jedis);
}