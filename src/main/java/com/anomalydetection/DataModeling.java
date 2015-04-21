
package com.anomalydetection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculate the total number of unique callers and calees
 * This program have dependency on Hackathon_CDR_Sample.csv data file
 * @author Team13
 *
 */
public class DataModeling {

	public static void main(String[] args) {
		DataModeling dm = new DataModeling();
		dm.uniqueCalee();

	}

	/**
	 * uniqueCallers method calculates total number of unique calls that are made from any single number
	 */
	public static List<UniqueCaller> uniqueCallers() {
		Map<String, HashSet<String>> uniqueCallers = new HashMap<String, HashSet<String>>();
		Map<String, Integer> uniqueCallersDuration = new HashMap<String,Integer>();
		List<UniqueCaller> uniqueCaller = new ArrayList<UniqueCaller>();
		String csvFile = "Hackathon_CDR_Sample.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] CDRData = line.split(cvsSplitBy);
				if (uniqueCallers.containsKey(CDRData[6])) {
					uniqueCallers.get(CDRData[6]).add(CDRData[7]);
					int total = uniqueCallersDuration.get(CDRData[6]);
					total += Integer.valueOf(CDRData[15]);
					uniqueCallersDuration.put(CDRData[6], total);
				} else {

					uniqueCallers.put(CDRData[6], new HashSet<String>());
					uniqueCallers.get(CDRData[6]).add(CDRData[7]);
					uniqueCallersDuration.put(CDRData[6], Integer.valueOf(CDRData[15]));
				}
			}
			// int count = 99;
			System.out.println("Unique Callers" + "\t" + "TotalUniqueCallers");//+"\t"+"TotalCallDuration");
			Set<String> keys = uniqueCallers.keySet();
			for (String key : keys) {

				// if (count > 0) {
				if (!key.isEmpty()) {
					UniqueCaller uniqueCallerObj = new UniqueCaller(key,uniqueCallers.get(key).size());
					uniqueCaller.add(uniqueCallerObj);
					System.out.println(key + "\t"
							+ uniqueCallers.get(key).size());//+"\t"+uniqueCallersDuration.get(key));
					// count--;
				}
				// } else {
				// break;
				// }
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return uniqueCaller;
	}

	/**
	 * uniqueCalee method calculates total number of unique calls received on all the calees
	 */
	public void uniqueCalee() {

		Map<String, HashSet<String>> uniqueCalee = new HashMap<String, HashSet<String>>();
		Map<String, Integer> uniqueCalleesDuration = new HashMap<String,Integer>();
		String csvFile = "Hackathon_CDR_Sample.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] CDRData = line.split(cvsSplitBy);
				if (uniqueCalee.containsKey(CDRData[7])) {
					uniqueCalee.get(CDRData[7]).add(CDRData[6]);
					int total = uniqueCalleesDuration.get(CDRData[7]);
					total += Integer.valueOf(CDRData[15]);
					uniqueCalleesDuration.put(CDRData[7], total);
				} else {

					uniqueCalee.put(CDRData[7], new HashSet<String>());
					uniqueCalee.get(CDRData[7]).add(CDRData[6]);
					uniqueCalleesDuration.put(CDRData[7], Integer.valueOf(CDRData[15]));
				}
			}
			Set<String> keys = uniqueCalee.keySet();
			// int count = 99;
			System.out.println("Unique Calee" + "\t" + "TotalUniqueCalee");//+"\t"+"TotalCallDuration");
			for (String key : keys) {
				// if (count > 0) {
				if (!key.isEmpty()) {
					System.out
							.println(key + "\t" + uniqueCalee.get(key).size());//+"\t"+uniqueCalleesDuration.get(key));
					// count--;
				}
				// } else {
				// break;
				// }
			}

			// System.out.println("Average = "+total/uniqueCallers.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}