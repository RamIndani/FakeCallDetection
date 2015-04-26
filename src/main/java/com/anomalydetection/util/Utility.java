package com.anomalydetection.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utility {
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static void writeToCSV(String fileHeader, String fileName, Map<String, HashSet<String>> individualInfo, Map<String, Integer> callDuration) {
		final String COMMA_DELIMITER = ",";
		final String NEW_LINE_SEPARATOR = "\n";
		
		//CSV file header
		final String FILE_HEADER = fileHeader;
		
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter(fileName);

			//Write the CSV file header
			fileWriter.append(FILE_HEADER.toString());
			
			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			//Write a new student object list to the CSV file
			Set<String> keys = individualInfo.keySet();
			for (String key : keys) {
				if (!key.isEmpty()) {
					fileWriter.append(key);
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(individualInfo.get(key).size()));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(callDuration.get(key)));
					fileWriter.append(NEW_LINE_SEPARATOR);
//					System.out.println(key + "\t" + uniqueCalee.get(key).size()+"\t"+uniqueCalleesDuration.get(key));
				
				}
			}

			
			
			System.out.println("CSV file was created successfully !!!");
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
	}
}
