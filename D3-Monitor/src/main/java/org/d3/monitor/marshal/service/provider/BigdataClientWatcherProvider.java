package org.d3.monitor.marshal.service.provider;

import java.io.File;

import org.d3.monitor.service.BigdataClientWatcher;

public class BigdataClientWatcherProvider implements BigdataClientWatcher {

	@Override
	public boolean changed() {
		System.out.println("changed: ");
		return false;
	}

	
	private String vp = "D:/workspace/enteroctopus/enteroctopus/"
			+ "enteroctopus-bigdata/enteroctopus-bigdata/src/main/resources/_prod/version";
	private File version = new File(vp);
	@Override
	public long vlength() {
//		System.out.println("length: " + version.length());
		return version.length();
	}

}
