package org.d3.demo.nosql.redis;

import java.util.Random;
import java.util.Set;

import org.d3.std.Stopwatch;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class Test {

	public static void main(String[] args) {
		zsetTest();
	}

	public static void zsetTest(){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				int index = 0;
				Pipeline pipeline = jedis.pipelined();
				Stopwatch sw = Stopwatch.newStopwatch();
				for(int i = 0; i < 500; i++){
					pipeline.multi();
					for(int j = 0; j < 10000; j++){
						index++;
						pipeline.zadd("user:rank1", index, "user" + index);
					}
					pipeline.exec();
				}
				System.out.println(sw.longTime());
			}
		});
	}
	
	public static void zsetGetTest(){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				Random r = new Random(5000000);
				Stopwatch sw = Stopwatch.newStopwatch();
				
//				long size = jedis.zcard("user:rank1");
//				System.out.println(size);
//				jedis.zincrby("user:rank1", -1000000, "user" + 5000000);
				long rank = jedis.zrank("user:rank1", "user" + 5000000);
				
				System.out.println(rank);
				System.out.println(sw.longTime());
			}
		});
	}
	
	public static void listTest(){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				int index = 0;
				Pipeline pipeline = jedis.pipelined();
				
				Stopwatch sw = Stopwatch.newStopwatch();
				for(int i = 0; i < 1000; i++){
					Stopwatch sw1 = Stopwatch.newStopwatch();
					pipeline.multi();
					for(int j = 0; j < 10000; j++){
//						index;
						String user = "user" + index;
						pipeline.rpush("recent:", user);
					}
					pipeline.exec();
					System.out.println(sw1.longTime());
				}
				System.out.println(sw.longTime());
			}
		});
	}
	
	public static void listGetTest(){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				Stopwatch sw1 = Stopwatch.newStopwatch();
				long len = jedis.llen("recent:");
				for(int i = 0; i < 5000000; i++){
					jedis.rpop("recent:");
				}
				System.out.println(sw1.longTime());
			}
		});
	}
}
