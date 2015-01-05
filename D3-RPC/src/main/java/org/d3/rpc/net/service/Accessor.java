package org.d3.rpc.net.service;

import org.d3.rpc.net.bean.Request;
import org.d3.rpc.util.Invokable;

/**
 * 访问 远程对象的类
 * 有2中策略：
 * 1. 同步
 * 2. 异步
 * @author d_jin
 *
 */
public interface Accessor {

	void accessAsync(Request request, Invokable invoke);
	
	Object accessSync(Request request);
}
