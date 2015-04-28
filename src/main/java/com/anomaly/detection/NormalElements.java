package com.anomaly.detection;



public class NormalElements  implements Comparable<NormalElements>{
	
	String number;
	int probability;
	
	public NormalElements(){
		
	}
	
	public NormalElements(String number, int prob){
		this.number=number;
		this.probability=prob;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public int getProbability() {
		return probability;
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
	@Override
	public int compareTo(NormalElements o) {
		// TODO Auto-generated method stub
		return (int) (o.probability-this.probability);
	}
	
	@Override
	  public boolean equals(Object other)
	        {
	        if(this==other) return true;
	        if(other==null || !(other instanceof NormalElements)) return false;
	        return this.number == (NormalElements.class.cast(other).number);
	        }
	  
	
}
