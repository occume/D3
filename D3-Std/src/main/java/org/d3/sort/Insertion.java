package org.d3.sort;

import org.d3.std.Generator;

public class Insertion {

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
		for(int i = 1; i < N; i++){
			for(int j = i; j > 0 && less(a[j], a[j - 1]); j--){
				exch(a, j, j - 1);
			}
		}
	}
	

	public static void main(String[] args) {
//		Integer[] in = new Integer[]{3, 2, 1, 5, 9};
		Integer[] in = Generator.intArray(100000, 1000000);
		show(in);
		sort(in);
		show(in);
	}

}
