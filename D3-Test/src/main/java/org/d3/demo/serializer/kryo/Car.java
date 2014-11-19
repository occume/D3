package org.d3.demo.serializer.kryo;

public class Car {
	
	private int id;
	private String brand;
	private int size;
	
	public Car(int id, String brand, int size) {
		this.id = id;
		this.brand = brand;
		this.size = size;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "Car [id=" + id + ", brand=" + brand + ", size=" + size + "]";
	}
	
}
