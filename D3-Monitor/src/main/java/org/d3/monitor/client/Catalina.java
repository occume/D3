package org.d3.monitor.client;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class Catalina {

	public static void main(String[] args) throws IOException {
		Path path = Paths.get("D:/logs/catalina.out");
		List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
//		Files.size(path);
		int size = lines.size();
		System.out.println(size);
		int num = (size / 10);
		int index = 1;
		int curr = 0;
		StringBuffer sb = new StringBuffer();
		
		System.out.println(num);
		
		for(int i = 0; i < size; i += num){
			
			lines.subList(i, i + num);
//			String content = Joiner.on("\n").join(lines.subList(i, i + num).iterator());
//			String line = lines.get(i);
			List<String> subList = lines.subList(i, i + num);
			Path subPath = Paths.get("D:/logs/catalina.out." + index);
			if(!Files.exists(subPath)){
				Files.createFile(subPath);
			}
			for(String content: subList){
				sb.append(content).append("\n");
				if(curr++ == 1000){
					Files.write(subPath, sb.toString().getBytes(), StandardOpenOption.APPEND);
					curr = 0;
					sb = new StringBuffer();
				}
			}
			if(sb.length() > 0){
				Files.write(subPath, sb.toString().getBytes(), StandardOpenOption.APPEND);
			}
			index++;
		}
	}

}
