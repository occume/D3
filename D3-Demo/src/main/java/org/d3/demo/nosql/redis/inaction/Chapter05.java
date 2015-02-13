package org.d3.demo.nosql.redis.inaction;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.javatuples.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;

public class Chapter05 {
	
	public static final String DEBUG = "debug";
    public static final String INFO = "info";
    public static final String WARNING = "warning";
    public static final String ERROR = "error";
    public static final String CRITICAL = "critical";

    public static final Collator COLLATOR = Collator.getInstance(Locale.CHINA);
        
    public static final SimpleDateFormat TIMESTAMP =
        new SimpleDateFormat("EEE MMM dd HH:00:00 yyyy");
    private static final SimpleDateFormat ISO_FORMAT =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
    static{
        ISO_FORMAT.setTimeZone(TimeZone.getDefault());
    }

	public static void main(String[] args) throws InterruptedException {
		new Chapter05().run();
//		int result = COLLATOR.compare("2015-02-03T17:00:00", "2015-02-03T16:00:00");
//		System.out.println(result);
		
//		System.out.println(105 / 5 * 5);
//		String cutoff = String.valueOf(
//                ((System.currentTimeMillis() + 2 * 86400000) / 1000) - 1 * 1);
//		System.out.println(cutoff);
	}
	
	public void run()
	        throws InterruptedException
	    {
	        Jedis conn = new Jedis("localhost");
	        conn.select(15);

//	        testLogRecent(conn);
//	        testLogCommon(conn);
//	        testCounters(conn);
//	        testStats(conn);
//	        testAccessTime(conn);
//	        testIpLookup(conn);
//	        testIsUnderMaintenance(conn);
	        testConfig(conn);
	    }
	public void testConfig(Jedis conn) {
        System.out.println("\n----- testConfig -----");
        System.out.println("Let's set a config and then get a connection from that config...");
        Map<String,Object> config = new HashMap<String,Object>();
        config.put("db", 15);
        setConfig(conn, "redis", "test", config);

        Jedis conn2 = redisConnection("test");
        System.out.println(
            "We can run commands from the configured connection: " + (conn2.info() != null));
    }
	public void testCounters(Jedis conn)
	        throws InterruptedException
	    {
	        System.out.println("\n----- testCounters -----");
	        System.out.println("Let's update some counters for now and a little in the future");
	        long now = System.currentTimeMillis() / 1000;
	        for (int i = 0; i < 10; i++) {
	            int count = (int)(Math.random() * 5) + 1;
	            updateCounter(conn, "test", count, now + i);
	        }

	        List<Pair<Integer,Integer>> counter = getCounter(conn, "test", 1);
	        System.out.println("We have some per-second counters: " + counter.size());
	        System.out.println("These counters include:");
	        for (Pair<Integer,Integer> count : counter){
	            System.out.println("  " + count);
	        }
	        assert counter.size() >= 10;

	        counter = getCounter(conn, "test", 5);
	        System.out.println("We have some per-5-second counters: " + counter.size());
	        System.out.println("These counters include:");
	        for (Pair<Integer,Integer> count : counter){
	            System.out.println("  " + count);
	        }
	        assert counter.size() >= 2;
	        System.out.println();

	        System.out.println("Let's clean out some counters by setting our sample count to 0");
	        CleanCountersThread thread = new CleanCountersThread(0, 2 * 86400000);
	        thread.start();
	        Thread.sleep(1000);
	        thread.quit();
	        thread.interrupt();
	        counter = getCounter(conn, "test", 86400);
	        System.out.println("Did we clean out all of the counters? " + (counter.size() == 0));
	        assert counter.size() == 0;
	    }
	
	public void testStats(Jedis conn) {
        System.out.println("\n----- testStats -----");
        System.out.println("Let's add some data for our statistics!");
        List<Object> r = null;
        for (int i = 0; i < 5; i++){
            double value = (Math.random() * 11) + 5;
            r = updateStats(conn, "temp", "example", value);
        }
        System.out.println("We have some aggregate statistics: " + r);
        Map<String,Double> stats = getStats(conn, "temp", "example");
        System.out.println("Which we can also fetch manually:");
        System.out.println(stats);
        assert stats.get("count") >= 5;
    }
	
	public List<Pair<Integer,Integer>> getCounter(
	        Jedis conn, String name, int precision)
	    {
	        String hash = String.valueOf(precision) + ':' + name;
	        Map<String,String> data = conn.hgetAll("count:" + hash);
	        ArrayList<Pair<Integer,Integer>> results =
	            new ArrayList<Pair<Integer,Integer>>();
	        for (Map.Entry<String,String> entry : data.entrySet()) {
	            results.add(new Pair<Integer,Integer>(
	                        Integer.parseInt(entry.getKey()),
	                        Integer.parseInt(entry.getValue())));
	        }
	        Collections.sort(results);
	        return results;
	    }

	public void testLogRecent(Jedis conn) {
        System.out.println("\n----- testLogRecent -----");
        System.out.println("Let's write a few logs to the recent log");
        for (int i = 0; i < 5; i++) {
            logRecent(conn, "test", "this is message " + i);
        }
        List<String> recent = conn.lrange("recent:test:info", 0, -1);
        System.out.println(
                "The current recent message log has this many messages: " +
                recent.size());
        System.out.println("Those messages include:");
        for (String message : recent){
            System.out.println(message);
        }
        assert recent.size() >= 5;
    }

    public void testLogCommon(Jedis conn) {
        System.out.println("\n----- testLogCommon -----");
        System.out.println("Let's write some items to the common log");
        for (int count = 1; count < 6; count++) {
            for (int i = 0; i < count; i ++) {
                logCommon(conn, "test", "message-" + count);
            }
        }
        Set<Tuple> common = conn.zrevrangeWithScores("common:test:info", 0, -1);
        System.out.println("The current number of common messages is: " + common.size());
        System.out.println("Those common messages are:");
        for (Tuple tuple : common){
            System.out.println("  " + tuple.getElement() + ", " + tuple.getScore());
        }
        assert common.size() >= 5;
    }
    
    public void logRecent(Jedis conn, String name, String message) {
        logRecent(conn, name, message, INFO);
    }

    public void logRecent(Jedis conn, String name, String message, String severity) {
        String destination = "recent:" + name + ':' + severity;
        Pipeline pipe = conn.pipelined();
        pipe.lpush(destination, TIMESTAMP.format(new Date()) + ' ' + message);
        pipe.ltrim(destination, 0, 99);
        pipe.sync();
    }

    public void logCommon(Jedis conn, String name, String message) {
        logCommon(conn, name, message, INFO, 5000);
    }

    public void logCommon(
            Jedis conn, String name, String message, String severity, int timeout) {
        String commonDest = "common:" + name + ':' + severity;
        String startKey = commonDest + ":start";
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end){
            conn.watch(startKey);
            String hourStart = ISO_FORMAT.format(new Date());
            String existing = conn.get(startKey);

            Transaction trans = conn.multi();
            if (existing != null && COLLATOR.compare(existing, hourStart) < 0){
            	System.out.println("11111");
                trans.rename(commonDest, commonDest + ":last");
                trans.rename(startKey, commonDest + ":pstart");
                trans.set(startKey, hourStart);
            }
            else{
            	trans.set(startKey, hourStart);
            }

            trans.zincrby(commonDest, 1, message);

            String recentDest = "recent:" + name + ':' + severity;
            trans.lpush(recentDest, TIMESTAMP.format(new Date()) + ' ' + message);
            trans.ltrim(recentDest, 0, 99);
            List<Object> results = trans.exec();
            // null response indicates that the transaction was aborted due to
            // the watched key changing.
            if (results == null){
                continue;
            }
            return;
        }
    }
    public void updateCounter(Jedis conn, String name, int count) {
        updateCounter(conn, name, count, System.currentTimeMillis() / 1000);
    }

    public static final int[] PRECISION = new int[]{1, 5, 60, 300, 3600, 18000, 86400};
    public void updateCounter(Jedis conn, String name, int count, long now){
        Transaction trans = conn.multi();
        for (int prec : PRECISION) {
            long pnow = (now / prec) * prec;
            String hash = String.valueOf(prec) + ':' + name;
            trans.zadd("known:", 0, hash);
            trans.hincrBy("count:" + hash, String.valueOf(pnow), count);
        }
        trans.exec();
    }
    
    public List<Object> updateStats(Jedis conn, String context, String type, double value){
        int timeout = 5000;
        String destination = "stats:" + context + ':' + type;
        String startKey = destination + ":start";
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end){
            conn.watch(startKey);
            String hourStart = ISO_FORMAT.format(new Date());

            String existing = conn.get(startKey);
            Transaction trans = conn.multi();
            if (existing != null && COLLATOR.compare(existing, hourStart) < 0){
                trans.rename(destination, destination + ":last");
                trans.rename(startKey, destination + ":pstart");
            }
            trans.set(startKey, hourStart);

            String tkey1 = UUID.randomUUID().toString();
            String tkey2 = UUID.randomUUID().toString();
            trans.zadd(tkey1, value, "min");
            trans.zadd(tkey2, value, "max");

            trans.zunionstore(
                destination,
                new ZParams().aggregate(ZParams.Aggregate.MIN),
                destination, tkey1);
            trans.zunionstore(
                destination,
                new ZParams().aggregate(ZParams.Aggregate.MAX),
                destination, tkey2);

//            trans.del(tkey1, tkey2);
            trans.zincrby(destination, 1, "count");
            trans.zincrby(destination, value, "sum");
            trans.zincrby(destination, value * value, "sumsq");

            List<Object> results = trans.exec();
            if (results == null){
                continue;
            }
            return results.subList(results.size() - 3, results.size());
        }
        return null;
    }
    public Map<String,Double> getStats(Jedis conn, String context, String type){
        String key = "stats:" + context + ':' + type;
        Map<String,Double> stats = new HashMap<String,Double>();
        Set<Tuple> data = conn.zrangeWithScores(key, 0, -1);
        for (Tuple tuple : data){
            stats.put(tuple.getElement(), tuple.getScore());
        }
        stats.put("average", stats.get("sum") / stats.get("count"));
        double numerator = stats.get("sumsq") - Math.pow(stats.get("sum"), 2) / stats.get("count");
        double count = stats.get("count");
        stats.put("stddev", Math.pow(numerator / (count > 1 ? count - 1 : 1), .5));
        return stats;
    }
    public void setConfig(
            Jedis conn, String type, String component, Map<String,Object> config) {
        Gson gson = new Gson();
        conn.set("config:" + type + ':' + component, gson.toJson(config));
    }
    private static final Map<String,Map<String,Object>> CONFIGS =
            new HashMap<String,Map<String,Object>>();
    private static final Map<String,Long> CHECKED = new HashMap<String,Long>();
    @SuppressWarnings("unchecked")
	public Map<String,Object> getConfig(Jedis conn, String type, String component) {
        int wait = 1000;
        String key = "config:" + type + ':' + component;

        Long lastChecked = CHECKED.get(key);
        if (lastChecked == null || lastChecked < System.currentTimeMillis() - wait){
            CHECKED.put(key, System.currentTimeMillis());

            String value = conn.get(key);
            Map<String,Object> config = null;
            if (value != null){
                Gson gson = new Gson();
                config = (Map<String,Object>)gson.fromJson(
                    value, new TypeToken<Map<String,Object>>(){}.getType());
            }else{
                config = new HashMap<String,Object>();
            }

            CONFIGS.put(key, config);
        }

        return CONFIGS.get(key);
    }
    public static final Map<String,Jedis> REDIS_CONNECTIONS =
            new HashMap<String,Jedis>();
    public Jedis redisConnection(String component){
        Jedis configConn = REDIS_CONNECTIONS.get("config");
        if (configConn == null){
            configConn = new Jedis("localhost");
            configConn.select(15);
            REDIS_CONNECTIONS.put("config", configConn);
        }

        String key = "config:redis:" + component;
        Map<String,Object> oldConfig = CONFIGS.get(key);
        Map<String,Object> config = getConfig(configConn, "redis", component);

        if (!config.equals(oldConfig)){
            Jedis conn = new Jedis("localhost");
            if (config.containsKey("db")){
                conn.select(((Double)config.get("db")).intValue());
            }
            REDIS_CONNECTIONS.put(key, conn);
        }

        return REDIS_CONNECTIONS.get(key);
    }
}
