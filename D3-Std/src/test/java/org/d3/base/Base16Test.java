package org.d3.base;

import org.d3.std.Binaries;

import com.google.common.base.Splitter;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteSink;
import com.google.common.io.Files;
import com.google.common.primitives.Bytes;

public class Base16Test {

	public static void main(String[] args) {
		String result = BaseEncoding.base16().encode("hello guava".getBytes());
		
		System.out.println(result);
		
		result = Binaries.bytes2BinaryStr("hello guava".getBytes());
		System.out.println(result);
		result = Binaries.BinaryToHexString("hello guava".getBytes());
		
		System.out.println(result);
	}

}