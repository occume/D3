package org.d3.demo.serializer.kryo;

import org.d3.std.Binaries;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoTest {

	public static void main(String[] args) {
		
		Kryo kryo = new Kryo();
		final Output output = new Output(1024);
		
		Car car = new Car(1, "buick", 100);
		
		kryo.writeObject(output, car);
		output.close();
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				
				Kryo kryo = new Kryo();
				byte[] result = output.toBytes();
				System.out.println(result.length);
				
				Car carr = kryo.readObject(new Input((Binaries.HexStringToBinary("01 00 6F 72 67 2E 64 33 2E 72 70 63 2E 6E 65 74 2E 62 65 61 6E 2E 52 65 71 75 65 73 F4"))), Car.class);
				System.out.println(carr);
			}
		});
		t.start();
		
	}

}
