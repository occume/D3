package org.d3.demo.guava;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Random;
import java.util.Set;

import org.d3.std.Stopwatch;

import com.google.common.hash.Hashing;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ResourceInfo;

public class ClassPathTest {

	public static void main(String[] args) throws IOException {
		test();
//		Stopwatch sw = Stopwatch.newStopwatch();
//		model();
//		System.out.println(sw.longTime());
//		
//		Stopwatch sw1 = Stopwatch.newStopwatch();
//		nonmodel();
//		System.out.println(sw1.longTime());
		
		System.out.println(8 & 19);
		System.out.println(9 & 19);
		new Object().hashCode();
		String s = "";
		int code = s.hashCode();
		int hash = code % 8191;
		System.out.println(hash);
		
		System.out.println(Math.sqrt(2));
		
//		IdentityHashMap<K, V>
	}
	
	private static int loop = 1000000000;
	private static Random r = new Random(System.currentTimeMillis());

	public static void test() throws IOException{
		ClassPath cp = ClassPath.from(ClassPathTest.class.getClassLoader());
		
		Set<ResourceInfo> rss = cp.getResources();
		for(ResourceInfo ri: rss){
			if(ri.getResourceName().contains("src/main/resources"))
			System.out.println(ri.getResourceName());
		}
//		HashMap<K, V>
//		Collections.sort(list);
		System.out.println(new ClassPathTest().hashCode());
//		Hashing.murmur3_32().
	}
	
	public static int model(){
		int result = 0;
		for(int i = 0; i < loop; i++){
			result = r.nextInt() % 1024;
		}
		return result;
	}
	
	public static int nonmodel(){
		int result = 0;
		for(int i = 0; i < loop; i++){
			result = r.nextInt() & (1024 - 1);
		}
		return result;
	}
}