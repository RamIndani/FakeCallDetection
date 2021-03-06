package com.anomaly.detection;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.anomalydetection.CallerModel;
import com.anomalydetection.DataModeling;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
	HashSet<String> markedFrauds = new HashSet<String>();
	List<NormalElements> totalFrauds = new ArrayList<NormalElements>();
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index(){
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath);
		File name = new File(dir, rootPath);
		DataModeling.uniqueCallee(rootPath+"/testdata.csv");
		DataModeling.uniqueCallers(rootPath+"/testdata.csv");
		return "index";
	}
	
	@RequestMapping(value="/home", method=RequestMethod.GET)
	public String home(){
		return "home";
	}
	
	@RequestMapping(value="/getfraud", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<NormalElements> getfraud(){
		totalFrauds.clear();
		getRandomForestUniqueCallers();
		normalDistributionCallees();
		normalDistribution();
		return totalFrauds;
	}
	
	@RequestMapping(value = "/fraud/{number}", method = RequestMethod.GET)
	@ResponseBody
	public String recordFraud(@PathVariable String number) {
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath);
		File name = new File(dir, "randomForestTrainData.csv");
		FileWriter fileWriter = null;
		if (!dir.exists())
			dir.mkdirs();
		logger.info(dir.getAbsolutePath());
		try {
			fileWriter = new FileWriter(name,true);
			fileWriter.append("\n")
			.append(number)
			.append(",")
			.append("100")
			.append(",")
			.append("1000")
			.append(",")
			.append("yes");
			markedFrauds.add(number);
		} catch (Exception e) {
			return "{success:false}";
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				logger.info("Error while flushing/closing fileWriter !!!");

			}
		}

		return "{success:true}";
	}
	
	/*@RequestMapping(value="/cda", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String doCDA(@RequestParam String filePath){
		//System.out.println("Hello there");
		//DataModeling.uniqueCallers(filePath);
		//DataModeling.uniqueCallee(filePath);
		return "{success:true}";
	}*/
	
	@RequestMapping(value="/uniquecallers", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<CallerModel> getCDA(){
		//System.out.println("Hello there");
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath);
		File name = new File(rootPath+"/testdata.csv");
		//DataModeling.uniqueCallers(name.getAbsolutePath());
		logger.info("File PATH : uniqucallers : "+name.getAbsolutePath());
		DataModeling dataModeling = new DataModeling();
		List<CallerModel> list = new ArrayList<CallerModel>();
		list = dataModeling.totalUniqueCallers(name.getAbsolutePath());
		Collections.sort(list);
		//list.add();
		//return "{success:true}";
		if(list.size()>25){
			return list.subList(0, 25);
		}else{
			return list;
		}
	}
	
	@RequestMapping(value="/uniquecallee", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<CallerModel> getCDAUC(){
		//System.out.println("Hello there");
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath);
		File name = new File(rootPath+"/testdata.csv");
		//DataModeling.uniqueCallee(name.getAbsolutePath());
		DataModeling dataModeling = new DataModeling();
		List<CallerModel> list = new ArrayList<CallerModel>();
		logger.info("File PATH : uniqucallee"+name.getAbsolutePath());
		list = dataModeling.totalUniqueCallees(name.getAbsolutePath());
		Collections.sort(list);
		//list.add();
		//return "{success:true}";
		if(list.size()>25){	
		return list.subList(0, 25);
		}else{
			return list;
		}
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(
            @RequestParam("file") MultipartFile file){
		//String name="testdata.csv";
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath);
		File name = new File( rootPath+"/testdata.csv");
        if (!file.isEmpty()) {
        	if (!dir.exists())
                dir.mkdirs();
        	logger.info(dir.getAbsolutePath());
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(name));
                stream.write(bytes);
                stream.close();
                return "File uploaded!";
            } catch (Exception e) {
                return "Upload failed";
            }
        } else {
            return "Empty file";
        }
    }

	
	@RequestMapping(value="/kmeansUniqueCallers", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String getKMeansUniqueCallers() {
		try {
			// setup r session and it's working dir
			//Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath);
			String rWorkingDir = "/Users/ramnivasindani/git/FakeCallDetection/src/main/r";
			String filePath = rootPath+"/UniqueCallers.csv";
			r.eval("setwd('"+rWorkingDir+"')");
			r.eval("filePath <- '"+filePath+"'");
			r.eval("library(rjson)");
			logger.info("filepath absolute"+filePath);
			// execute kmeans.r script
			r.eval("source('kmeans.R')");
			
			// collect result
			REXP xvalExp = r.eval("dataCSVJSON <- toJSON(dataCSV)");
			
			//JSONObject json = new JSONObject(xvalExp.toString());
			return xvalExp.asString();
		} catch(Exception e) {
			System.out.println("Error in kmeans clustering...");
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value="/kmeansUniqueCallee", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String getKMeansUniqueCallee() {
		try {
			// setup r session and it's working dir
			//Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
			String rootPath = System.getProperty("catalina.home");
			//File dir = new File(rootPath+"/upload");
			String rWorkingDir = "/Users/ramnivasindani/git/FakeCallDetection/src/main/r";
			String filePath = rootPath+"/UniqueCallee.csv";
			r.eval("setwd('"+rWorkingDir+"')");
			r.eval("filePath <- '"+filePath+"'");
			r.eval("library(rjson)");
			
			// execute kmeans.r script
			r.eval("source('kmeans.R')");
			
			// collect result
			REXP xvalExp = r.eval("dataCSVJSON <- toJSON(dataCSV)");
			//JSONObject json = new JSONObject(xvalExp.toString());
			return xvalExp.asString();
		} catch(Exception e) {
			System.out.println("Error in kmeans clustering...");
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value="/randomForestUniqueCallers", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public HashMap<String, String> getRandomForestUniqueCallers( ) {
		try
		{
			//Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
			// random forest train data
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath);
			String fileTrainPath = rootPath+"/randomForestTrainData.csv";
			String fileTestPath = rootPath+"/randomForestTestData.csv";
			// commented area is the code to set the default directory for R, which will not be needed
			// as we are not going to store anything in R
			//r.eval("setwd(\"~/Documents/SJSU course documents/CMPE 239/Team Project/Project Data\")");
			r.eval("library(randomForest)");
			r.eval("library(rjson)");
			r.eval("project_train <- read.csv('"+fileTrainPath+"')");
			r.eval("fit <- randomForest(as.factor(Fraud) ~ UniqueCallers+TotalUniqueCallers+CallDuration, data=project_train, mtry=3,importance=TRUE, ntree=200)");
			r.eval("project_test <- read.csv('"+fileTestPath+"')");
			r.eval("Prediction <- predict(fit, project_test)");
			r.eval("submit <- data.frame(UniqueCallers = project_test$UniqueCallers, Fraud = Prediction)");
//			r.eval("write.csv(submit, file = \"testforest_1.csv\", row.names = FALSE)");
			REXP xvalExp = r.eval("dataTest <- toJSON(submit)");
			
			//This is the JSON returned by R language, it contains some not needed strings,
			//so i have taken a substring down 
			String parseJSON = xvalExp.toString();
			parseJSON = parseJSON.substring(parseJSON.indexOf(" ")+2,parseJSON.length()-2);
//			System.out.println(parseJSON);
			
			// Just created the HashMap to Add the Number and whether it is 
			// Fraud or Not to the HashMap
			HashMap<String, String> data = new HashMap<String, String>();
			JSONObject obj;
			
			// JSON parsing done here
			try
			{
				obj = new JSONObject(parseJSON);
				final JSONArray uniqueCallers = obj.getJSONArray("UniqueCallers");
				final JSONArray fraud = obj.getJSONArray("Fraud");
				for (int i = 0; i < uniqueCallers.length(); i++) {
					String temp1 =  uniqueCallers.get(i).toString();
					String temp2 =  fraud.get(i).toString();
					System.out.println(temp1 +","+temp2);
					if(temp2.equalsIgnoreCase("yes")){
						NormalElements nEle = new NormalElements(temp1, 100);
						if(!totalFrauds.contains(nEle)){
							totalFrauds.add(nEle);
						}
					}
				}
				return data;
			} catch (JSONException e)
			{
				System.out.println("Error in retrieving the JSON Object");
				e.printStackTrace();
				return null;
			}
		} catch(Exception e)
		{
			System.out.println("Error in retrieving the Random Forest results");
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	@RequestMapping(value="/randomForestUniqueCallee", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public HashMap<String, String> getRandomForestUniqueCallee(@RequestParam String fileTestPath) {
		try
		{
		//	Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
			// random forest train data
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath);
			String fileTrainPath = rootPath+"/randomForestTrainData.csv";
			// commented area is the code to set the default directory for R, which will not be needed
			// as we are not going to store anything in R
			//r.eval("setwd(\"~/Documents/SJSU course documents/CMPE 239/Team Project/Project Data\")");
			r.eval("library(randomForest)");
			r.eval("library(rjson)");
			r.eval("project_train <- read.csv('"+fileTrainPath+"')");
			r.eval("fit <- randomForest(as.factor(Fraud) ~ UniqueCallee+TotalUniqueCallee+CallDuration, data=project_train, mtry=3,importance=TRUE, ntree=200)");
			r.eval("project_test <- read.csv('"+fileTestPath+"')");
			r.eval("Prediction <- predict(fit, project_test)");
			r.eval("submit <- data.frame(UniqueCallee = project_test$UniqueCallee, Fraud = Prediction)");
//			r.eval("write.csv(submit, file = \"testforest_1.csv\", row.names = FALSE)");
			REXP xvalExp = r.eval("dataTest <- toJSON(submit)");
			
			//This is the JSON returned by R language, it contains some not needed strings,
			//so i have taken a substring down 
			String parseJSON = xvalExp.toString();
			parseJSON = parseJSON.substring(parseJSON.indexOf(" ")+2,parseJSON.length()-2);
//			System.out.println(parseJSON);
			
			// Just created the HashMap to Add the Number and whether it is 
			// Fraud or Not to the HashMap
			HashMap<String, String> data = new HashMap<String, String>();
			JSONObject obj;
			
			// JSON parsing done here
			try
			{
				obj = new JSONObject(parseJSON);
				final JSONArray uniqueCallers = obj.getJSONArray("UniqueCallee");
				final JSONArray fraud = obj.getJSONArray("Fraud");
				for (int i = 0; i < uniqueCallers.length(); i++) {
					String temp1 =  uniqueCallers.get(i).toString();
					String temp2 =  fraud.get(i).toString();
					System.out.println(temp1 +","+temp2);
					data.put(temp1, temp2);
				}
				return data;
			} catch (JSONException e)
			{
				System.out.println("Error in retrieving the JSON Object");
				e.printStackTrace();
				return null;
			}
		} catch(Exception e)
		{
			System.out.println("Error in retrieving the Random Forest results");
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	
	
	@RequestMapping(value="/normal", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<NormalElements> normalDistribution(){
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath);
		File name = new File(dir, "testdata.csv");
		DataModeling dataModeling = new DataModeling();
		List<CallerModel> list = new ArrayList<CallerModel>();
		list = dataModeling.totalUniqueCallers(name.getAbsolutePath());
		Collections.sort(list);
		double[] totalElements = new double[list.size()];
		double mean = 0d;
		long total = 0l;
		int count = 0;
		for(CallerModel callerModel : list){
			total+=callerModel.getvalue();
			totalElements[count]=callerModel.getvalue();
			count++;
			if(count==100)
				break;
		}
		mean = total/count;
		StandardDeviation sd = new StandardDeviation();
		double variance = sd.evaluate(totalElements,mean);
		logger.info("variance is : "+variance+" mean is : "+mean);
		NormalDistribution nd = new NormalDistribution(mean,variance);
		List<NormalElements> normalElements = new ArrayList<NormalElements>();
		for(CallerModel callerModel : list){
			if(!markedFrauds.contains(callerModel.getkey()) && !callerModel.getkey().isEmpty()){
			NormalElements nElements = new NormalElements(callerModel.getkey(), (int)Math.round(nd.cumulativeProbability(callerModel.getvalue())*100));
			normalElements.add(nElements);	
			}else if(!callerModel.getkey().isEmpty() && !totalFrauds.contains(callerModel)){
				NormalElements nElements = new NormalElements(callerModel.getkey(), (int)Math.round(nd.cumulativeProbability(callerModel.getvalue())*100));
				if(!totalFrauds.contains(nElements)){
				totalFrauds.add(nElements);	
				logger.info("Number normal: "+callerModel.getkey());
				}
			}
		}
		if(normalElements.size()>100){
			return normalElements.subList(0, 100);
		}else{
			return normalElements;
		}
	}
	
	
	@RequestMapping(value="/normalCallee", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<NormalElements> normalDistributionCallees(){
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath);
		File name = new File(dir, "testdata.csv");
		DataModeling dataModeling = new DataModeling();
		List<CallerModel> list = new ArrayList<CallerModel>();
		list = dataModeling.totalUniqueCallees(name.getAbsolutePath());
		Collections.sort(list);
		double[] totalElements = new double[list.size()];
		double mean = 0d;
		long total = 0l;
		int count = 0;
		for(CallerModel callerModel : list){
			total+=callerModel.getvalue();
			totalElements[count]=callerModel.getvalue();
			count++;
			if(count==100)
				break;
		}
		mean = total/count;
		StandardDeviation sd = new StandardDeviation();
		double variance = sd.evaluate(totalElements,mean);
		logger.info("variance is : "+variance+" mean is : "+mean);
		NormalDistribution nd = new NormalDistribution(mean,variance);
		List<NormalElements> normalElements = new ArrayList<NormalElements>();
		for(CallerModel callerModel : list){
			if (!markedFrauds.contains(callerModel.getkey())
					&& !callerModel.getkey().isEmpty()) {
				NormalElements nElements = new NormalElements(
						callerModel.getkey(),
						(int) Math.round(nd.cumulativeProbability(callerModel
								.getvalue()) * 100));
				normalElements.add(nElements);
			} else if(!callerModel.getkey().isEmpty() && !totalFrauds.contains(callerModel)){
				NormalElements nElements = new NormalElements(
						callerModel.getkey(),
						(int) Math.round(nd.cumulativeProbability(callerModel
								.getvalue()) * 100));
				logger.info("Number : normalCallee"+callerModel.getkey());
				if(!totalFrauds.contains(nElements)){
					totalFrauds.add(nElements);
				}
			}
		}
		if(normalElements.size()>100){
		return normalElements.subList(0, 100);
		}else{
			return normalElements;
		}
		
		
	}
}
