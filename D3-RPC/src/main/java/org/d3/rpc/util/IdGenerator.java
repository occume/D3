package org.d3.rpc.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
	
	private static AtomicInteger worldId = new AtomicInteger(1);
	
	public static UUID uuuid(){
		return UUID.randomUUID();
	}
	
	public static int autoIncrementId(){
		return worldId.getAndIncrement();
	}
	
}
