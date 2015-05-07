
package com.anomalydetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anomaly.detection.HomeController;
import com.anomalydetection.util.Utility;

/**
 * Calculate the total number of unique callers and calees
 * This program have dependency on Hackathon_CDR_Sample.csv data file
 * @author Team12
 *
 */
public class DataModeling {

	private static final Logger logger = LoggerFactory.getLogger(DataModeling.class);
	
	public static void main(String[] args) {
		DataModeling.uniqueCallee("Hackathon_CDR_Sample.csv");
		DataModeling.uniqueCallers("Hackathon_CDR_Sample.csv");

	}

	public DataModeling(){
		
	}
	/**
	 * uniqueCallers method calculates total number of unique calls that are made from any single number
	 */
	public static String uniqueCallers(String fileName) {
		Map<String, HashSet<String>> uniqueCallers = new HashMap<String, HashSet<String>>();
		Map<String, Integer> uniqueCallersDuration = new HashMap<String,Integer>();
		List<UniqueCaller> uniqueCaller = new ArrayList<UniqueCaller>();
//		String csvFile = "Hackathon_CDR_Sample.csv";
		String csvFile = fileName;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String resultFileName = null;
		String resultFileNameForest = null;
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
//			System.out.println("Unique Callers" + "\t" + "TotalUniqueCallers");//+"\t"+"TotalCallDuration");
//			Set<String> keys = uniqueCallers.keySet();
//			for (String key : keys) {
//
//				// if (count > 0) {
//				if (!key.isEmpty()) {
//					UniqueCaller uniqueCallerObj = new UniqueCaller(key,uniqueCallers.get(key).size());
//					uniqueCaller.add(uniqueCallerObj);
//					System.out.println(key + "\t"
//							+ uniqueCallers.get(key).size());//+"\t"+uniqueCallersDuration.get(key));
//					// count--;
//				}
//				// } else {
//				// break;
//				// }
//			}
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath);
			logger.info("unique caller create file----------------");
			String fileHeader = "UniqueCallers,TotalUniqueCallers,TotalCallDuration";
			resultFileName = rootPath+"/UniqueCallers.csv";
			Utility.writeToCSV(fileHeader, resultFileName, uniqueCallers, uniqueCallersDuration);
			
			String fileHeaderForest = "UniqueCallers,TotalUniqueCallers,CallDuration";
			resultFileNameForest = rootPath+"/randomForestTestData.csv";
			Utility.writeToCSV(fileHeaderForest, resultFileNameForest, uniqueCallers, uniqueCallersDuration);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			
		}finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return resultFileName;
	}

	public List<CallerModel> totalUniqueCallers(String fileName){

		Map<String, HashSet<String>> uniqueCallers = new HashMap<String, HashSet<String>>();
		Map<String, Integer> uniqueCallersDuration = new HashMap<String,Integer>();
		List<UniqueCaller> uniqueCaller = new ArrayList<UniqueCaller>();
//		String csvFile = "Hackathon_CDR_Sample.csv";
		String csvFile = fileName;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String resultFileName = null;
		String resultFileNameForest = null;
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
//			System.out.println("Unique Callers" + "\t" + "TotalUniqueCallers");//+"\t"+"TotalCallDuration");
//			Set<String> keys = uniqueCallers.keySet();
//			for (String key : keys) {
//
//				// if (count > 0) {
//				if (!key.isEmpty()) {
//					UniqueCaller uniqueCallerObj = new UniqueCaller(key,uniqueCallers.get(key).size());
//					uniqueCaller.add(uniqueCallerObj);
//					System.out.println(key + "\t"
//							+ uniqueCallers.get(key).size());//+"\t"+uniqueCallersDuration.get(key));
//					// count--;
//				}
//				// } else {
//				// break;
//				// }
//			}
			/*logger.info("unique caller create file*************");
			String fileHeader = "UniqueCallers,TotalUniqueCallers,TotalCallDuration";
			resultFileName = "UniqueCallers.csv";
			Utility.writeToCSV(fileHeader, resultFileName, uniqueCallers, uniqueCallersDuration);
			
			String fileHeaderForest = "UniqueCallers,TotalUniqueCallers,CallDuration";
			resultFileNameForest = "randomForestTestData.csv";
			Utility.writeToCSV(fileHeaderForest, resultFileNameForest, uniqueCallers, uniqueCallersDuration);*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Set<String> keys = uniqueCallers.keySet();
		List<CallerModel> listCaller = new ArrayList<CallerModel>();
		
		for(String uniqueCallr: keys){
			CallerModel callerModel = new CallerModel(uniqueCallr, uniqueCallers.get(uniqueCallr).size());
			listCaller.add(callerModel);
		}
		
		
		return listCaller;
	
	}
	/**
	 * uniqueCalee method calculates total number of unique calls received on all the calees
	 */
	public static String uniqueCallee(String fileName) {
		logger.info("unique callee create file*************"+fileName);
		Map<String, HashSet<String>> uniqueCalee = new HashMap<String, HashSet<String>>();
		Map<String, Integer> uniqueCalleesDuration = new HashMap<String,Integer>();
//		String csvFile = "Hackathon_CDR_Sample.csv";
		String csvFile = fileName;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String resultFileName = null;
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
//			Set<String> keys = uniqueCalee.keySet();
//			// int count = 99;
//			System.out.println("UniqueCalee" + "\t" + "TotalUniqueCalee"+"\t"+"TotalCallDuration");
//			for (String key : keys) {
//				// if (count > 0) {
//				if (!key.isEmpty()) {
//					System.out.println(key + "\t" + uniqueCalee.get(key).size()+"\t"+uniqueCalleesDuration.get(key));
//					// count--;
//				}
//				// } else {
//				// break;
//				// }
//			}
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath);
			logger.info("unique callee create file*************");
			String fileHeader = "UniqueCalee,TotalUniqueCalee,TotalCallDuration";
			resultFileName = rootPath+"/UniqueCallee.csv";
			Utility.writeToCSV(fileHeader, resultFileName, uniqueCalee, uniqueCalleesDuration);
		} catch (FileNotFoundException e) {
			logger.info("file not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			logger.info("");
		}finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return resultFileName;
	}

	public List<CallerModel> totalUniqueCallees(String fileName) {


		Map<String, HashSet<String>> uniqueCalee = new HashMap<String, HashSet<String>>();
		Map<String, Integer> uniqueCalleesDuration = new HashMap<String,Integer>();
//		String csvFile = "Hackathon_CDR_Sample.csv";
		String csvFile = fileName;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String resultFileName = null;
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
//			Set<String> keys = uniqueCalee.keySet();
//			// int count = 99;
//			System.out.println("UniqueCalee" + "\t" + "TotalUniqueCalee"+"\t"+"TotalCallDuration");
//			for (String key : keys) {
//				// if (count > 0) {
//				if (!key.isEmpty()) {
//					System.out.println(key + "\t" + uniqueCalee.get(key).size()+"\t"+uniqueCalleesDuration.get(key));
//					// count--;
//				}
//				// } else {
//				// break;
//				// }
//			}
			/*logger.info("unique callee create file ------------");
			String fileHeader = "UniqueCalee,TotalUniqueCalee,TotalCallDuration";
			resultFileName = "UniqueCalee.csv";
			Utility.writeToCSV(fileHeader, resultFileName, uniqueCalee, uniqueCalleesDuration);
			*/// System.out.println("Average = "+total/uniqueCallers.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			
		}finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Set<String> keys = uniqueCalee.keySet();
		List<CallerModel> listCaller = new ArrayList<CallerModel>();
		
		for(String uniqueCallr: keys){
			CallerModel callerModel = new CallerModel(uniqueCallr, uniqueCalee.get(uniqueCallr).size());
			listCaller.add(callerModel);
		}
		
		
		return listCaller;
		
	
	}

}