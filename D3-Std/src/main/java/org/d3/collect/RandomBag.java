package org.d3.collect;

import java.util.Iterator;
import java.util.Random;

public class RandomBag<Item> implements Iterable<Item>{
	
	private int N;
	private Item[] a;
	
	public RandomBag(){
		a = (Item[])new Object[2];
	}
	
	public boolean isEmpty(){
		return N == 0;
	}
	
	public void add(Item item){
		if(N >= a.length){
			resizing(a.length * 2);
		}
		a[N++] = item;
	}
	
	private void resizing(int capcity){
		Item[] temp = (Item[]) new Object[capcity];
		for(int i = 0; i < a.length; i++){
			temp[i] = a[i];
		}
		a = temp;
	}

	@Override
	public Iterator<Item> iterator() {
		return new ListIterator();
	}
	
	private class ListIterator<Item> implements Iterator<Item>{

		private int curr;
		private Item[] b;
		
		public ListIterator(){
			b = (Item[]) new Object[N];
			
			for(int j = 0; j < b.length; j++){
				b[j] = (Item) a[j];
			}
			
			Random random = new Random();
			for(int i = 0; i < b.length; i++){
				int r = random.nextInt(b.length);
				Item tp = b[i];
				b[i] = b[r];
				b[r] = tp;
			}
		}
		
		@Override
		public boolean hasNext() {
			return curr < N;
		}

		@Override
		public Item next() {
			return (Item) b[curr++];
		}

		@Override
		public void remove() {
			
		}
	}
	
	public static void main(String...strings){
		RandomBag<Integer> bag = new RandomBag<>();
		for(int i = 0; i < 5; i++){
			bag.add(i);
		}
		
		Iterator<Integer> it = bag.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}

}
