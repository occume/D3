package org.d3.std;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;


public class Printer {
	
	public static void printObject(Object obj){
		try {
			printObject(obj, "start stop close execute");
		} catch (Exception e) {
			e.printStackTrace();
		}
	};
	
	public static void printObject(Object add, String skiped) throws Exception{
		
		Map<String, String> mnames = Maps.newHashMap();
		int maxLen = -1;
		
		Method[] methods = add.getClass().getMethods();
		Object ret = null;
		
		for(Method m: methods){
			if(skiped.contains(m.getName())){
				ret = "SKIPPED";
			}
			else{
				if(!m.isVarArgs()){
					
					try{
						ret = m.invoke(add);
					}
					catch(Throwable e){
						ret = "ERR";
					}
					if(m.getName().length() > maxLen){
						maxLen = m.getName().length();
					}
					mnames.put(m.getName(), String.valueOf(ret));
				}
			}
		}
		
		for(String key: mnames.keySet()){
			int dis = maxLen - key.length();
			StringBuilder b = new StringBuilder();
			for(int i = 0; i < dis; i++){
				b.append(" ");
			}
			b.append(key)
			 .append(" : ")
			 .append(mnames.get(key));
			System.out.println(b);
		}
		System.out.println("-------------------------------------------");
	}
	
	public static void printMap(Map map){
		for(Object key: map.keySet()){
			System.out.println(key + " : " + map.get(key));
		}
	}
	
	
	public static void printByteArray(byte[] a) {
        int N = a.length;
        StdOut.println("length = " + N);
        for (int i = 0; i < N; i++) {
            StdOut.printf("%1d ", a[i]);
        }
        StdOut.println();
    }
}
