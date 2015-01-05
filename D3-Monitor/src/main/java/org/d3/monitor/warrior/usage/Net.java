package org.d3.monitor.warrior.usage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 采集网络带宽使用率
 */
public class Net{

	private static Logger log = LoggerFactory.getLogger(Net.class);
	private static Net INSTANCE = new Net();
	private final static float TotalBandwidth = 100;	//网口带宽,Mbps
	
	private Net(){
	
	}
	
	public static Net getInstance(){
		return INSTANCE;
	}
	
	/**
	 * @Purpose:采集网络带宽使用率
	 * @param args
	 * @return float,网络带宽使用率,小于1
	 */
	public float get() {
		log.info("开始收集网络带宽使用率");
		float netUsage = 0.0f;
		Process pro1,pro2;
		Runtime r = Runtime.getRuntime();
		try {
			String command = "cat /proc/net/dev";
			//第一次采集流量数据
			long startTime = System.currentTimeMillis();
			pro1 = r.exec(command);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			String line = null;
			long inSize1 = 0, outSize1 = 0;
			while((line=in1.readLine()) != null){	
				line = line.trim();
				if(line.startsWith("eth0")){
					log.info(line);
					String[] temp = line.split("\\s+"); 
					inSize1 = Long.parseLong(temp[0].substring(5));	//Receive bytes,单位为Byte
					outSize1 = Long.parseLong(temp[8]);				//Transmit bytes,单位为Byte
					break;
				}				
			}	
			in1.close();
			pro1.destroy();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				log.error("NetUsage休眠时发生InterruptedException. " + e.getMessage());
				log.error(sw.toString());
			}
			//第二次采集流量数据
			long endTime = System.currentTimeMillis();
			pro2 = r.exec(command);
			BufferedReader in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
			long inSize2 = 0 ,outSize2 = 0;
			while((line=in2.readLine()) != null){	
				line = line.trim();
				if(line.startsWith("eth0")){
					log.info(line);
					String[] temp = line.split("\\s+"); 
					inSize2 = Long.parseLong(temp[0].substring(5));
					outSize2 = Long.parseLong(temp[8]);
					break;
				}				
			}
			if(inSize1 != 0 && outSize1 !=0 && inSize2 != 0 && outSize2 !=0){
				float interval = (float)(endTime - startTime)/1000;
				//网口传输速度,单位为bps
				float curRate = (float)(inSize2 - inSize1 + outSize2 - outSize1)*8/(1000000*interval);
				netUsage = curRate/TotalBandwidth;
//				log.info("本节点网口速度为: " + curRate + "Mbps");
//				log.info("本节点网络带宽使用率为: " + netUsage);
			}				
			in2.close();
			pro2.destroy();
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			log.error("NetUsage发生InstantiationException. " + e.getMessage());
			log.error(sw.toString());
		}	
		return netUsage;
	}

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		while(true){
			System.out.println(Net.getInstance().get());
			Thread.sleep(5000);
		}
	}
}