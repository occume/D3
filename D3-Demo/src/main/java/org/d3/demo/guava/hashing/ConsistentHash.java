package org.d3.demo.guava.hashing;

import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;

public class ConsistentHash {

	public static void main(String[] args) {
		
		List<String> servers = Lists.newArrayList("a", "b");
		String[] keys = {"aa", "bb", "cc", "dd", "ee", "ff", "gg", "hh"};
		for(String key: keys){
			int bucket = Hashing.consistentHash(Hashing.md5().hashString(key, Charsets.UTF_8), servers.size());
			String name = servers.get(bucket);
			System.out.println(key + " >> " + name);
		}
		
	}

}