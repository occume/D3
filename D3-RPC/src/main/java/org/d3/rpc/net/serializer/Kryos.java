package org.d3.rpc.net.serializer;

import com.esotericsoftware.kryo.Kryo;

public class Kryos {

	private static ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>(){
		@Override
		protected Kryo initialValue() {
			Kryo k = new Kryo();
			return k;
		}
		
	};
	
	public static Kryo kryo(){
		return kryos.get();
	}
}
