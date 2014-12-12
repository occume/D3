package org.d3.demo.performance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 采集磁盘IO使用率
 */
public class IO{

	private static Logger log = LoggerFactory.getLogger(IO.class);
	private static IO INSTANCE = new IO();
	
	private IO(){
	
	}
	
	public static IO getInstance(){
		return INSTANCE;
	}
	
	/**
	 * @Purpose:采集磁盘IO使用率
	 * @param args
	 * @return float,磁盘IO使用率,小于1
	 */
	public float get() {
		log.info("开始收集磁盘IO使用率");
		float ioUsage = 0.0f;
		Process pro = null;
		Runtime r = Runtime.getRuntime();
		try {
			String command = "iostat -d -x";
			pro = r.exec(command);
			BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			String line = null;
			int count =  0;
			while((line=in.readLine()) != null){		
				if(++count >= 4){
//					log.info(line);
					String[] temp = line.split("\\s+");
					if(temp.length > 1){
						float util =  Float.parseFloat(temp[temp.length-1]);
						ioUsage = (ioUsage>util)?ioUsage:util;
					}
				}
			}
			if(ioUsage > 0){
				log.info("本节点磁盘IO使用率为: " + ioUsage);	
				ioUsage /= 100; 
			}			
			in.close();
			pro.destroy();
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			log.error("IoUsage发生InstantiationException. " + e.getMessage());
			log.error(sw.toString());
		}	
		return ioUsage;
	}

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		while(true){
			System.out.println(IO.getInstance().get());
			Thread.sleep(5000);
		}
	}

}