package com.anomalydetection;

public class UniqueCaller {

	private String number;
	private int count;
	public UniqueCaller(String key, int size) {
		this.number = key;
		this.count = size;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
