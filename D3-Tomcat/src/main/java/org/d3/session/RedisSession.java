package org.d3.session;

import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardSession;

public class RedisSession extends StandardSession {
	
	private static final long serialVersionUID = -333976912834721842L;
	
	private transient volatile boolean writed;
	
	private transient volatile boolean readed;

	public RedisSession(Manager manager) {
		super(manager);
	}

	public boolean isWrited() {
		return writed;
	}

	public void setWrited(boolean writed) {
		this.writed = writed;
	}

	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

	@Override
	public void removeAttribute(String name) {
		super.removeAttribute(name);
		setWrited(true);
	}

	@Override
	public void setAttribute(String name, Object value) {
		super.setAttribute(name, value);
		setWrited(true);
	}

	@Override
	public void access() {
		super.access();
		setReaded(true);
	}

	@Override
	public void endAccess() {
		super.endAccess();
		setReaded(true);
	}

}
