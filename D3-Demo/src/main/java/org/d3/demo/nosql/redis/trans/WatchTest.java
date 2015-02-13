package org.d3.demo.nosql.redis.trans;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.d3.demo.nosql.redis.Invocable;
import org.d3.demo.nosql.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

public class WatchTest {
	
	public static void listItem(final String itemId,  final String sellerId, final int price){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				String inventory = "inventory:" + sellerId;
		        String item = itemId + '.' + sellerId;
		        long end = System.currentTimeMillis() + 5000;

		        while (System.currentTimeMillis() < end) {
		        	jedis.watch(inventory);
		            if (!jedis.sismember(inventory, itemId)){
		            	jedis.unwatch();
//		                return false;
		            }

		            Transaction trans = jedis.multi();
		            trans.zadd("market:", price, item);
		            trans.srem(inventory, itemId);
		            List<Object> results = trans.exec();
		            // null response indicates that the transaction was aborted due to
		            // the watched key changing.
		            if (results == null){
		                continue;
		            }
//		            return true;
		        }
//		        return false;
			}
		});
	}
	public static void testListItem(boolean nested) {
        if (!nested){
            System.out.println("\n----- testListItem -----");
        }

        System.out.println("We need to set up just enough state so that a user can list an item");
        
        final String seller = "userX";
        final String item = "itemX";
        
        Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
			    jedis.sadd("inventory:" + seller, item);
			    Set<String> i = jedis.smembers("inventory:" + seller);

			    System.out.println("The user's inventory has:");
			    for (String member : i){
			        System.out.println("  " + member);
			    }
			    System.out.println();

		        System.out.println("Listing the item...");
		        listItem(item, seller, 10);
//		        System.out.println("Listing the item succeeded? " + l);
//		        assert l;
		        Set<Tuple> r = jedis.zrangeWithScores("market:", 0, -1);
		        System.out.println("The market contains:");
		        for (Tuple tuple : r){
		            System.out.println("  " + tuple.getElement() + ", " + tuple.getScore());
		        }
//		        assert r.size() > 0;
			}
		});
    }

	public static void purchaseItem(final String buyerId, final String itemId, 
			final String sellerId, final double lprice) {

        final String buyer = "users:" + buyerId;
        final String seller = "users:" + sellerId;
        final String item = itemId + '.' + sellerId;
        final String inventory = "inventory:" + buyerId;
        final long end = System.currentTimeMillis() + 10000;
        Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				while (System.currentTimeMillis() < end){
					jedis.watch("market:", buyer);
					System.out.println("item: " + item);
		            double price = jedis.zscore("market:", item);
		            double funds = Double.parseDouble(jedis.hget(buyer, "funds"));
		            if (price != lprice || price > funds){
		            	jedis.unwatch();
		            	break;
		            }

		            Transaction trans = jedis.multi();
		            trans.hincrBy(seller, "funds", (int)price);
		            trans.hincrBy(buyer, "funds", (int)-price);
		            trans.sadd(inventory, itemId);
		            trans.zrem("market:", item);
		            List<Object> results = trans.exec();
		            // null response indicates that the transaction was aborted due to
		            // the watched key changing.
		            if (results == null){
		                continue;
		            }
		            break;
		        }
			}
		});
    }
	
	 public static void testPurchaseItem() {
	        System.out.println("\n----- testPurchaseItem -----");
	        testListItem(true);

	        Redis.execute(new Invocable() {
				@Override
				public void invoke(Jedis jedis) {
			        System.out.println("We need to set up just enough state so a user can buy an item");
			        jedis.hset("users:userY", "funds", "125");
			        Map<String,String> r = jedis.hgetAll("users:userY");
			        System.out.println("The user has some money:");
			        for (Map.Entry<String,String> entry : r.entrySet()){
			            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
			        }
			        assert r.size() > 0;
			        assert r.get("funds") != null;
			        System.out.println();
		
			        System.out.println("Let's purchase an item");
			        purchaseItem("userY", "itemX", "userX", 10);
//			        
			        r = jedis.hgetAll("users:userY");
			        System.out.println("Their money is now:");
			        for (Map.Entry<String,String> entry : r.entrySet()){
			            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
			        }
			        assert r.size() > 0;
		
			        String buyer = "userY";
			        Set<String> i = jedis.smembers("inventory:" + buyer);
			        System.out.println("Their inventory is now:");
			        for (String member : i){
			            System.out.println("  " + member);
			        }
			        assert i.size() > 0;
			        assert i.contains("itemX");
			        assert jedis.zscore("market:", "itemX.userX") == null;
				}
			});
	    }
	 
	 public void testBenchmarkUpdateToken(Jedis conn) {
	        System.out.println("\n----- testBenchmarkUpdate -----");
	        benchmarkUpdateToken(conn, 5);
	 }
	
	 public void benchmarkUpdateToken(Jedis conn, int duration) {
	        try{
	            @SuppressWarnings("rawtypes")
	            Class[] args = new Class[]{
	                Jedis.class, String.class, String.class, String.class};
	            Method[] methods = new Method[]{
	                this.getClass().getDeclaredMethod("updateToken", args),
	                this.getClass().getDeclaredMethod("updateTokenPipeline", args),
	            };
	            for (Method method : methods){
	                int count = 0;
	                long start = System.currentTimeMillis();
	                long end = start + (duration * 1000);
	                while (System.currentTimeMillis() < end){
	                    count++;
	                    method.invoke(this, conn, "token", "user", "item");
	                }
	                long delta = System.currentTimeMillis() - start;
	                System.out.println(
	                        method.getName() + ' ' +
	                        count + ' ' +
	                        (delta / 1000) + ' ' +
	                        (count / (delta / 1000)));
	            }
	        }catch(Exception e){
	            throw new RuntimeException(e);
	        }
	    }

	    public void updateToken(Jedis conn, String token, String user, String item) {
	        long timestamp = System.currentTimeMillis() / 1000;
	        conn.hset("login:", token, user);
	        conn.zadd("recent:", timestamp, token);
	        if (item != null) {
	            conn.zadd("viewed:" + token, timestamp, item);
	            conn.zremrangeByRank("viewed:" + token, 0, -26);
	            conn.zincrby("viewed:", -1, item);
	        }
	    }

	    public void updateTokenPipeline(Jedis conn, String token, String user, String item) {
	        long timestamp = System.currentTimeMillis() / 1000;
	        Pipeline pipe = conn.pipelined();
	        pipe.multi();
	        pipe.hset("login:", token, user);
	        pipe.zadd("recent:", timestamp, token);
	        if (item != null){
	            pipe.zadd("viewed:" + token, timestamp, item);
	            pipe.zremrangeByRank("viewed:" + token, 0, -26);
	            pipe.zincrby("viewed:", -1, item);
	        }
	        pipe.exec();
	    }
	
	public void run(){
		Jedis conn = new Jedis("10.8.92.9");
        conn.select(15);
//
//        testListItem(conn, false);
//        testPurchaseItem(conn);
//        testBenchmarkUpdateToken(conn);
        watch(conn);
        watch0(conn);
	}
	public static final String guild = "members:test";
	public static void watch(final Jedis conn){
		new Thread(new Runnable() {
			@Override
			public void run() {
				
//				conn.watch(guild);
				sleep(3000);
				Transaction trans = conn.multi();
				trans.zrange(guild, 0, -1);
				List<Object> members = trans.exec();
				System.out.println(members);
			}
		})
		.start();;
	}
	
	public static void watch0(final Jedis conn){
		new Thread(new Runnable() {
			@Override
			public void run() {
//				conn.watch(guild);
				sleep(2000);
				conn.zadd(guild, 0, "bar");
			}
		})
		.start();;
	}
	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
//		testPurchaseItem();
		new WatchTest().run();
	}

}
