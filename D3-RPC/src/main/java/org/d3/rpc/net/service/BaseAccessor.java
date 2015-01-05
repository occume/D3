package org.d3.rpc.net.service;

import org.d3.rpc.net.bean.Request;
import org.d3.rpc.util.Invokable;

public class BaseAccessor implements Accessor {

	@Override
	public void accessAsync(Request request, Invokable invoke) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object accessSync(Request request) {
		throw new UnsupportedOperationException();
	}

}
