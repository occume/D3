package org.d3.demo.nosql.redis.trans;

import org.d3.demo.nosql.redis.Invocable;
import org.d3.demo.nosql.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class TransactionTest {

	public static void main(String[] args) {
		trans();
	}
	
	public static void noTrans(){
		int threads = 10;
		for(int i = 0; i < threads; i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					Redis.execute(new Invocable() {
						@Override
						public void invoke(Jedis jedis) {
							System.out.println(jedis.incr("notrans:"));
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							jedis.incrBy("notrans:", -1);
						}
					});
				}
			}).start();;
		}
	}
	
	public static void trans(){
		int threads = 10;
		for(int i = 0; i < threads; i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					Redis.execute(new Invocable() {
						@Override
						public void invoke(Jedis jedis) {
							Response r;
//							Pipeline pl = jedis.pipelined();
							Transaction trans = jedis.multi();
//							pl.incr("notrans:");
							r = trans.incr("notrans:");
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							trans.incrBy("notrans:", -1);
//							pl.sync();
							trans.exec();
							System.out.println(r.get());
						}
					});
				}
			}).start();;
		}
	}
}
