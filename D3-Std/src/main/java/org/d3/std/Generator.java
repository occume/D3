package org.d3.std;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.google.common.math.DoubleMath;

public class Generator {

	public static void main(String[] args) {
		System.out.println(DoubleMath.mean(doubleArray(10)[0], doubleArray(10)[1]));
	}
	
	private static Random random = new Random();
	
	public static double[] doubleArray(int length){
		double[] ret = new double[length];
		for(int i = 0; i < length; i++){
			ret[i] = random.nextDouble();
		}
		return ret;
	}

	public static Integer[] intArray(int length, int max){
		Integer[] ret = new Integer[length];
		for(int i = 0; i < length; i++){
			ret[i] = random.nextInt(max);
		}
		return ret;
	}
	
	public static Integer[] intArray(int length){
		Integer[] ret = new Integer[length];
		for(int i = 0; i < length; i++){
			ret[i] = random.nextInt(100);
		}
		return ret;
	}
	
	public static byte[] byteArray(int length){
		byte[] ret = new byte[length];
		random.nextBytes(ret);
		return ret;
	}
	
	public static Collection<String> stringCollection(int size){
		Collection<String> c = new ArrayList<String>();
		for(int i = 0; i < size; i++){
			c.add("" + i);
		}
		return c;
	}
	
}
