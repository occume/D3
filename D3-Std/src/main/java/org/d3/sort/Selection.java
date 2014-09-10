package org.d3.sort;

import org.d3.std.Generator;
import org.d3.std.StdArrayIO;

public class Selection{
	
	public static boolean less(Comparable v, Comparable w){
		return v.compareTo(w) < 0;
	}
	
	public static void exch(Comparable[] a, int i, int j){
		Comparable t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	
	public static void show(Comparable[] a){
		for(int i = 0; i < a.length; i++){
			System.out.print(a[i]);
			System.out.print(",");
		}
		System.out.println();
	}
	
	public static void sort(Comparable[] a){
		int N = a.length;
		for(int i = 0; i < N; i++){
			int min = i;
			for(int j = i + 1; j < N;j ++){
				if(less(a[j], a[min])){
					min = j;
				}
			}
			exch(a, min, i);
		}
	}
	
	public static void main(String...strings){
		Integer[] in = Generator.intArray(100000, 1000000);
//		StdArrayIO.print(in);
//		Integer[] in = new Integer[]{3, 2, 1, 5, 9};
		show(in);
		sort(in);
		show(in);
	}
}
