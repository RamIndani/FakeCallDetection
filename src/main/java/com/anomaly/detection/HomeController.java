package com.anomaly.detection;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index(){
		return "index";
	}
	
	@RequestMapping(value="/uniquecallers", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String uniquecallers(){
		System.out.println("Hello there");
		//return DataModeling.uniqueCallers();
		return "{success:true}";
	}
	
	@RequestMapping(value="/kmeans", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String getKMeansClusters() {
		try {
			// setup r session and it's working dir
			Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
			String rWorkingDir = "/Users/Monil/GitHub/FakeCallDetection/src/main/r";
			String filePath = "/Users/Monil/GitHub/FakeCallDetection/src/test";
			r.eval("setwd('"+rWorkingDir+"')");
			r.eval("filePath <- '"+filePath+"'");
			r.eval("library(rjson)");
			
			// execute kmeans.r script
			r.eval("source('kmeans.R')");
			
			// collect result
			REXP xvalExp = r.eval("dataCSVJSON <- toJSON(dataCSV)");
			return xvalExp.asString();
		} catch(Exception e) {
			System.out.println("Error in kmeans clustering...");
			e.printStackTrace();
			return "";
		}
	}
	
	@RequestMapping(value="/randomForest", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public HashMap<String, String> getFraudCallFromRandomForest(@RequestParam String fileTestPath) {
		try
		{
			Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
			// random forest train data
			String fileTrainPath = "/Users/Monil/GitHub/FakeCallDetection/src/test/randomForestTrainData.csv";
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
}
