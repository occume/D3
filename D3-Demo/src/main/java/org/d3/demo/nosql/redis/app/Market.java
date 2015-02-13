package org.d3.demo.nosql.redis.app;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.d3.demo.nosql.redis.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class Market {
	
	private static final String INVENTORY = "inventory:";
	private static final String MARKET = "market:";
	private static final String USERS = "users:";
	
	public void addItem(String itemId, String user){
		String inventory = INVENTORY + user;
		Jedis conn = Redis.get();
		try{
			conn.sadd(inventory, itemId);
		}finally{
			Redis.back(conn);
		}
	}
	
	public void listItem(String itemId, String seller, double price){
		String inventory = INVENTORY + seller;
		String item = itemId + "." + seller;
		long end = System.currentTimeMillis() + 5000;
		Jedis conn = Redis.get();
		try{
			while(System.currentTimeMillis() < end){
				conn.watch(inventory);
				System.out.println(inventory);
				System.out.println(itemId);
				System.out.println(conn.sismember(inventory, itemId));
				Set<String> items = conn.smembers("inventory:1");
				System.out.println(items);
				
				if(!conn.sismember(inventory, itemId)){
					conn.unwatch();
					System.out.println(seller + " don't have " + itemId);
					return;
				}
				Transaction trans = conn.multi();
				trans.zadd(MARKET, price, item);
				trans.srem(inventory, itemId);
				List<Object> result = trans.exec();
				if(result == null) continue;
				return;
			}
		}finally{
			Redis.back(conn);
		}
	}
	
	public void purchaseItem(String buyerId, String itemId, String sellerId, double lprice){
		String buyer = USERS + buyerId;
		String seller = USERS + sellerId;
		String item = itemId + "." + sellerId;
		long end = System.currentTimeMillis() + 10000;
		String inventory = INVENTORY + buyerId;
		
		Jedis conn = Redis.get();
		try{
			while(System.currentTimeMillis() < end){
				conn.watch("market:", buyer);
				double price = conn.zscore(MARKET, item);
				double funds = Double.valueOf(conn.hget(buyer, "funds"));
				if(lprice != price || funds < price){
					conn.unwatch();
					return;
				}
				
				Transaction trans = conn.multi();
				trans.hincrBy(seller, "funds", (int)price);
				trans.hincrBy(buyer, "funds", (int)-price);
				trans.sadd(inventory, itemId);
				trans.zrem(MARKET, item);
				trans.exec();
			}
		}finally{
			Redis.back(conn);
		}
	}

	public static void main(String[] args) {
//		ReentrantLock
		Market market = new Market();
//		market.listItem("itemA", "1", 10);
		market.purchaseItem("2", "itemA", "1", 10);
	}

}
