package org.d3.demo.nosql.redis.inaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class CleanCountersThread
extends Thread
{
private Jedis conn;
private int sampleCount = 100;
private boolean quit;
private long timeOffset; // used to mimic a time in the future.

public CleanCountersThread(int sampleCount, long timeOffset){
    this.conn = new Jedis("localhost");
    this.conn.select(15);
    this.sampleCount = sampleCount;
    this.timeOffset = timeOffset;
}

public void quit(){
    quit = true;
}

public void run(){
    int passes = 0;
    while (!quit){
        long start = System.currentTimeMillis();
//        long start = System.currentTimeMillis() + timeOffset;
        int index = 0;
        while (index < conn.zcard("known:")){
            Set<String> hashSet = conn.zrange("known:", index, index);
            index++;
            if (hashSet.size() == 0) {
                break;
            }
            String hash = hashSet.iterator().next();
            int prec = Integer.parseInt(hash.substring(0, hash.indexOf(':')));
            int bprec = (int)Math.floor(prec / 60);
            if (bprec == 0){
                bprec = 1;
            }
            if ((passes % bprec) != 0){
                continue;
            }

            String hkey = "count:" + hash;
            String cutoff = String.valueOf(
                ((System.currentTimeMillis()) / 1000) - 1 * prec);
//            ((System.currentTimeMillis() + timeOffset) / 1000) - sampleCount * prec);
           
            ArrayList<String> samples = new ArrayList<String>(conn.hkeys(hkey));
            Collections.sort(samples);
            int remove = bisectRight(samples, cutoff);
            System.out.println(hash + " : " + cutoff + " : " + remove);
            if (remove != 0){
                conn.hdel(hkey, samples.subList(0, remove).toArray(new String[0]));
                if (remove == samples.size()){
                    conn.watch(hkey);
                    if (conn.hlen(hkey) == 0) {
                        Transaction trans = conn.multi();
                        trans.zrem("known:", hash);
                        trans.exec();
                        index--;
                    }else{
                        conn.unwatch();
                    }
                }
            }
        }

        passes++;
        long duration = Math.min(
            (System.currentTimeMillis() + timeOffset) - start + 1000, 60000);
        try {
            sleep(Math.max(60000 - duration, 1000));
        }catch(InterruptedException ie){
            Thread.currentThread().interrupt();
        }
    }
}

// mimic python's bisect.bisect_right
public int bisectRight(List<String> values, String key) {
    int index = Collections.binarySearch(values, key);
    return index < 0 ? Math.abs(index) - 1 : index + 1;
}
}
