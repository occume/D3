package org.d3.demo.concurrent.thread;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class DownLoader extends Thread{
	
	private static interface ProgressListener {
		  void onProgress(int current, int total);
		}

	private InputStream in;
	private OutputStream out;
	private Path path;
	private ArrayList<ProgressListener> listeners; 
	
	public DownLoader(URL url, Path path) throws IOException{
		in = url.openConnection().getInputStream();
		this.path = path;
		out = Files.newOutputStream(path);
		listeners = new ArrayList<>();
	}
	
	private void addListener(ProgressListener progressListener) {
		listeners.add(progressListener);
	}

	private void updateProgress(int m, int n) {
		for (ProgressListener listener: listeners)
		     listener.onProgress(m, n);
	}
	
	public void run(){
		int n = 0, total = 0;
		byte[] buf = new byte[1024 * 1024];
		try {
			while((n = in.read(buf)) != -1){
				total += n;
//				Files.write(path, buf, StandardOpenOption.APPEND);
//				Files.
				out.write(buf, 0, n);
				out.flush();
				updateProgress(n, total);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Path p = Paths.get("D:/tmp/jmm1.pdf");
		if(!Files.exists(p)){
			Files.createFile(p);
		}
//	  	URL from = new URL("http://download.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2");
	  	URL from = new URL("http://www.cs.umd.edu/~pugh/java/memoryModel/Dagstuhl.pdf");
//	  	from.openConnection().s
	  	DownLoader downloader = new DownLoader(from, p);
	  	downloader.start();
	  	downloader.addListener(new ProgressListener() {
	  		public void onProgress(int m, int n) {System.out.println(m + " " + n);}
	  		public void onComplete(boolean success) {}
	  	});
	  	downloader.join();
	  }
}
