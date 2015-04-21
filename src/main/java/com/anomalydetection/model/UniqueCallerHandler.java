package com.anomalydetection.model;

import java.util.ArrayList;
import java.util.List;

import com.anomalydetection.UniqueCaller;

public class UniqueCallerHandler {

	List<UniqueCaller> totalCallCounter = new ArrayList<UniqueCaller>();

	public List<UniqueCaller> getTotalCallCounter() {
		return totalCallCounter;
	}

	public void setTotalCallCounter(List<UniqueCaller> totalCallCounter) {
		this.totalCallCounter = totalCallCounter;
	}
}
