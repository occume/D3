package org.d3.demo.nosql.redis.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.d3.demo.nosql.redis.Invocable;
import org.d3.demo.nosql.redis.Redis;

import com.google.common.base.Strings;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

public class SimpleVoteService{
	
	public static int 	ONE_WEEK_IN_MILLION_SECONDS = 7 * 86400000;
	public static int	VOTE_SCORE 	= 432000;
	public static int 	PAGE_SIZE 	= 25;

	public void articleVote(final String userId, final String articleId){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				long cutoff = System.currentTimeMillis() - ONE_WEEK_IN_MILLION_SECONDS;
				Double postTime = jedis.zscore("time:", articleId);
				System.out.println(cutoff + " : " + postTime);
				if(postTime != null && postTime < cutoff) return;
				
				long result = jedis.sadd("voted:" + articleId, userId);
				if(result == 1){
					jedis.zincrby("score:", VOTE_SCORE, articleId);
					jedis.hincrBy(articleId, "votes", 1);
				}
				else{
					jedis.zincrby("score:", VOTE_SCORE, articleId);
					jedis.hincrBy(articleId, "votes", 1);
				}
			}
		});
	}
	
	private long articleId;
	public long postArticle(final String userId, final String title,
				final String link){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				long articleId = jedis.incr("article:");
				SimpleVoteService.this.articleId = articleId;
				String voted = "voted:" + articleId;
				jedis.sadd(voted, userId);
				jedis.expire(voted, ONE_WEEK_IN_MILLION_SECONDS);
				long now = System.currentTimeMillis();
				String article = "article:" + articleId;
				
				Map<String, String> hash = new HashMap<String, String>();
				hash.put("title", title);
				hash.put("link", link);
				hash.put("poster", userId);
				hash.put("time", str(now));
				hash.put("votes", "1");
				jedis.hmset(article, hash);
				jedis.zadd("score:", VOTE_SCORE, article);
				jedis.zadd("time:", now, article);
			}
		});
		return SimpleVoteService.this.articleId;
	}
	
	private String str(Object from){
		return String.valueOf(from);
	}
	
	public void addRemoveGroups(final String articleId, final String toAdd, final String toRemove){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				if(!Strings.isNullOrEmpty(toAdd)){
					jedis.sadd("group:" + toAdd, articleId);
				}
				if(!Strings.isNullOrEmpty(toRemove)){
					jedis.srem("group:" + toRemove, articleId);
				}
			}
		});
	}
	
	public List<Map<String, String>> getArticles(int page){
		final int start = (page - 1) * PAGE_SIZE;
		final int end = page * PAGE_SIZE - 1;
		final List<Map<String, String>> result = new ArrayList<>();
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				Set<String> ids = jedis.zrevrange("score:", start, end);
				System.out.println(ids);
				for(String id: ids){
					Map<String, String> article = jedis.hgetAll(id);
					article.put("id", id);
					result.add(article);
				}
			}
		});
		return result;
	}
	
	public void getGroupArticles(final String group, final int page){
		Redis.execute(new Invocable() {
			@Override
			public void invoke(Jedis jedis) {
				String order = "score:";
				String key = order + group;
				if(!jedis.exists(key)){
					System.out.println("asdf");
					ZParams zp = new ZParams();
					zp.aggregate(ZParams.Aggregate.MAX);
					long result = jedis.zinterstore(key, zp, "group:" + group, "score:");
					System.out.println(result);
				}
				jedis.expire(key, 60);
			}
		});
	}
	
	public static void main(String...strings){
		int articles = 10;
		SimpleVoteService ss = new SimpleVoteService();
		List<Long> ids = new ArrayList<>();
//		for(int i = 1; i <= articles; i++){
//			long id = ss.postArticle("u" + i, "title" + i, "link" + i);
//			ids.add(id);
//		}
//		System.out.println(ids);
//		ss.articleVote("u2", "article:3");
//		List<Map<String, String>> article = ss.getArticles(1);
//		System.out.println(article);
//		ss.addRemoveGroups("article:6", "programing", "");
//		ss.getGroupArticles("programing", 1);
		noTrans(ss);
	}
	
	public static void noTrans(final SimpleVoteService ss){
		int threads = 10;
		for(int i = 0; i < threads; i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
//					Redis.execute(new Invocable() {
//						@Override
//						public void invoke(Jedis jedis) {
//							ss.articleVote("u2", "article:1");
//						}
//					});
					ss.articleVote("u2", "article:1");
				}
			}).start();;
		}
	}
}
