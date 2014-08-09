package org.d3.std;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LookupFile {
	
	public static final String EMPTY = "";
	
	private static ExecutorService executor = Executors.newFixedThreadPool(8);

	public static void main(String[] args) throws InterruptedException {
		lookup("global_config1");
	}
	
	public static File lookup(String name) throws InterruptedException{
		URL url = LookupFile.class.getClassLoader().getResource(EMPTY);
		File file = new File(url.getPath());
		
		File parent = file;
		File root = null;
		while(true){
			parent = parent.getParentFile();
			if(parent == null){
				break;
			}
			root = parent;
		}
		
		File ret = null;
		getPath(root, name, ret);
		executor.awaitTermination(100000, TimeUnit.SECONDS);
		return ret;
	}
	
	private static void getPath(final File root, final String expect, final File ret){
		String[] files = root.list();
		String thePath = root.getPath();
		
		if(files == null) return;
		
		for(String name: files){
			final File temp = new File(thePath + File.separator + name);
			if(temp.isDirectory()){
				executor.submit(new Runnable() {
					public void run() {
						getPath(temp, expect, ret);
					}
				});
				
			}
			else{
				if(name.toLowerCase().contains(expect)){
					System.out.println(temp);
//					ret = temp;
				}
			}
		}
	}

}
