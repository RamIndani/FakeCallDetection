package com.anomalydetection;

public class CallerModel implements Comparable<CallerModel>{

	int value;
	String key;
	public CallerModel() {
		
	}
	public CallerModel(String key, int value) {
		this.key = key;
		this.value = value;
	}
	public int getvalue() {
		return value;
	}
	public void setvalue(int value) {
		this.value = value;
	}
	public String getkey() {
		return key;
	}
	public void setkey(String key) {
		this.key = key;
	}
	
	@Override
	public int compareTo(CallerModel o) {
	
		return o.value-this.value;
		
	}
	
}
