package org.d3.wx.collector;

import org.d3.wx.writer.Storage;

public interface Collector {

	public void execute();
	public void storage(Storage storage);
}
