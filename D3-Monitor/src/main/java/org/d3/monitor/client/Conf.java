package org.d3.monitor.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Conf {
	
	private static final Logger LOG = LoggerFactory.getLogger(Conf.class);
	
	private static Properties props = new Properties();
	
	private static String confName = "/conf/conf.properties";
	
	static{
		props = new Properties();
		try {
			
			String fileName = Conf.class.getResource("/").getFile();
			File file = new File(fileName);
			LOG.info("res path: " + file.getPath());
//			String parentPath = file.getParentFile().getPath();
			String parentPath = file.getPath();
			String confPath = parentPath + confName;
			LOG.info("parent path: " + parentPath);
			
			InputStream	ins = new FileInputStream(new File(confPath));
			props.load(ins);
			
		} catch (FileNotFoundException e) {
			LOG.error("配置文件不存在");
		} catch (IOException e) {
			LOG.error("配置文件读取异常");
		}
	}
	
	public static Properties get(String confName){
		Properties props = new Properties();
		try {
			
			String fileName = Conf.class.getResource("/").getFile();
			File file = new File(fileName);
			LOG.info("res path: " + file.getPath());
//			String parentPath = file.getParentFile().getPath();
			String parentPath = file.getPath();
			String confPath = parentPath + "/" + confName + ".properties";
			LOG.info("parent path: " + confPath);
			
			InputStream	ins = new FileInputStream(new File(confPath));
			props.load(ins);
			
		} catch (FileNotFoundException e) {
			LOG.error("配置文件不存在");
		} catch (IOException e) {
			LOG.error("配置文件读取异常");
		}
		return props;
	}
	
	public static String getProperty(String key){
		return props.getProperty(key);
	}
	
	public static int getInt(String key){
		int ret = 0;
		try{
			ret = Integer.valueOf(props.getProperty(key));
		}catch(Exception e){
			LOG.error("读取配置配置异常：" + key);
		}
		return ret;
	}
	
	public static void main(String...strings){
//		System.out.println(Conf.getProperty(Constants.ConfKey.SERVER_PORT));
	}
}
