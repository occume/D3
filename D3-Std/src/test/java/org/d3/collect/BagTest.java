package org.d3.collect;

import org.d3.std.Generator;
import org.d3.std.StdOut;
import org.testng.annotations.Test;

public class BagTest {
	
  @Test
  public void f() {
	  Integer[] source = Generator.intArray(5);
	  Bag<Integer> numbers = new Bag<Integer>();
	  
	  for(Integer i: source){
		  numbers.add(i);
	  }
	  int N = numbers.size();
	  double sum = 0.0;
	  for (double x : numbers)
	  sum += x;
	  double mean = sum/N;
	  sum = 0.0;
	  for (double x : numbers)
	  sum += (x - mean)*(x - mean);
	  double std = Math.sqrt(sum/(N-1));
	  StdOut.printf("Mean: %.2f\n", mean);
	  StdOut.printf("Std dev: %.2f\n", std);
  }
  
}
