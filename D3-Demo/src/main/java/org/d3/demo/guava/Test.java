package org.d3.demo.guava;

import java.nio.charset.Charset;

import org.d3.std.Printer;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;

public class Test {

	public static void main(String[] args) {
		String result = Strings.padEnd("", 3, 'a');
		System.out.println(result);
		
		int ct = CharMatcher.anyOf("a").indexIn("abc");
		System.out.println(ct);
		test();
	}
	
	

	public static void test(){
		byte[] buf = {45,120,-11,89,60,-30,-59,82,21,47,-114,47,112,-33,-89,-34,27,-98,-3,112,-79,-89,-1,-28,119,88,37,-48,-107,8,-85,117};
//		String result = BaseEncoding.base16().encode(buf);
		String result = BaseEncoding.base16().encode(buf);
//		System.out.println(new String(buf, Charset.forName("ISO-8859-1")));
//		Printer.printByteArray(result);
		System.out.println(result);
	}
}
