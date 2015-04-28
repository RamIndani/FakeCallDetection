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
	
	HashSet<String> markedFrauds = new HashSet<String>();
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index(){
		return "index";
	}
	
	@RequestMapping(value = "/fraud/{number}", method = RequestMethod.GET)
	@ResponseBody
	public String recordFraud(@PathVariable String number) {
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath + "/upload");
		File name = new File(dir, "markfraud.csv");
		FileWriter fileWriter = null;
		if (!dir.exists())
			dir.mkdirs();
		logger.info(dir.getAbsolutePath());
		try {
			fileWriter = new FileWriter(name);
			fileWriter.write(number + "," + "100" +","+ "yes");
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
	
	@RequestMapping(value="/cda", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public String doCDA(@RequestParam String filePath){
		//System.out.println("Hello there");
		DataModeling.uniqueCallers(filePath);
		DataModeling.uniqueCallee(filePath);
		return "{success:true}";
	}
	
	@RequestMapping(value="/uniquecallers", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<CallerModel> getCDA(){
		//System.out.println("Hello there");
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath+"/upload");
		File name = new File(dir, "testdata.csv");
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
		File dir = new File(rootPath+"/upload");
		File name = new File(dir, "testdata.csv");
		DataModeling dataModeling = new DataModeling();
		List<CallerModel> list = new ArrayList<CallerModel>();
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
		File dir = new File(rootPath+"/upload");
		File name = new File(dir, "testdata.csv");
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
			Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
			String rWorkingDir = "FakeCallDetection/src/main/r";
			String filePath = "FakeCallDetection/UniqueCallers.csv";
			r.eval("setwd('"+rWorkingDir+"')");
			r.eval("filePath <- '"+filePath+"'");
			r.eval("library(rjson)");
			
			// execute kmeans.r script
			r.eval("source('kmeans.R')");
			
			// collect result
			REXP xvalExp = r.eval("dataCSVJSON <- toJSON(dataCSV)");
			JSONObject json = new JSONObject(xvalExp.toString());
			return json.toString();
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
			Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
			String rWorkingDir = "FakeCallDetection/src/main/r";
			String filePath = "FakeCallDetection/UniqueCallee.csv";
			r.eval("setwd('"+rWorkingDir+"')");
			r.eval("filePath <- '"+filePath+"'");
			r.eval("library(rjson)");
			
			// execute kmeans.r script
			r.eval("source('kmeans.R')");
			
			// collect result
			REXP xvalExp = r.eval("dataCSVJSON <- toJSON(dataCSV)");
			JSONObject json = new JSONObject(xvalExp.toString());
			return json.toString();
		} catch(Exception e) {
			System.out.println("Error in kmeans clustering...");
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value="/randomForestUniqueCallers", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public HashMap<String, String> getRandomForestUniqueCallers(@RequestParam String fileTestPath) {
		try
		{
			Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
			// random forest train data
			String fileTrainPath = "FakeCallDetection/src/test/randomForestTrainData.csv";
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
	
	
	@RequestMapping(value="/randomForestUniqueCallee", method=RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public HashMap<String, String> getRandomForestUniqueCallee(@RequestParam String fileTestPath) {
		try
		{
			Rengine r = new Rengine(new String[]{"--no-save"}, false, null);
			// random forest train data
			String fileTrainPath = "FakeCallDetection/src/test/randomForestTrainData.csv";
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
	
	
	@RequestMapping(value="/kmeansucallee", method=RequestMethod.GET, produces="application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String kmeanuCallee(){
		
		return "{\"CallingNumber\":[34653736089,19283431093,34632405747,255000000000,19198270561,13046243017,522889805,19137738128,967778,13606291775,19544243816,13148674390,34625644447,12062852224,19107942249,245000000000,13124919343,18504299688,13174009729,23238830149,554000000000,9720000000000,12037765301,12536067570,19703468148,17086368301,19194671504,12816310065,34602159334,34642386996,811000000000,18636780411,14193009360,41223459139,16157738278,2206210930,34632590177,238895097,611000000000,15167590707,34632027887,18157824191,15092355452,12154120889,34608828237,34602123041,19547224569,34646667087,18149382229,17247961139,34632094329,17026840811,15705597036,19195447187,14343496462,34650746079,16106889098,9726278531,13202516060,12144141039,16304696117,1809521617,23599900185,18479616386,19709479189,526000000000,16316083621,16147774687,14235866235,18475448156,34632909173,13302536772,12037872585,17176502158,35522805013,19416391790,31206404146,14122130689,19197463960,14143322595,265,16605825586,12106459976,34631338964,34609190995,2206664348,826,16106179637,16108291536,14128351032,3354622117,12067676363,19042208002,9166139260,13235226389,16055825651,19102384813,13034707372,13099289850,272000000000,17402861252,18439035408,2348,13369987451,243539031,12813423100,34632699646,13034313627,16105780845,34631277732,17175973896,34632242963,64102298935,19516796367,13867890614,78242356345,16517148783,13602257866,12155792462,14144238282,34640020986,14438824488,34631154670,15402982186,34640020506,34656052691,17149968894,18153984386,94754404649,92300544140,46600000000000,13368712769,332369466,12158625348,12188282154,13016631512,17735685689,13148466090,2350000000000,12316259310,19372882572,17245654711,34631572682,59337613960,16182068891,2644465330,34632833140,16102807386,18146961952,16269651960,34640020073,16148533258,16302798222,18153851380,17084102362,14073818527,16308962897,15126938145,34632684734,16263694496,2206228541,17735385175,2210000000000,19732092984,34632331841,13057525007,34632459372,17242091200,17249047125,18648859745,12033821485,34644619054,12153344246,17863910806,18477401794,34632684744,34631682907,14127989060,646757991,16083487857,15092993148,18695572661,12103596531,34631085149,17734766909,38551286553,12312827007,8556182198,16513421064,14124227697,530000000000,93700000000000,16052560722,12032682318,12036551452,17144837396,22378165571,34620434315,12153651645,12178652875,15122589545,34632807200,19549425574,34616645607,11613730143,19414290847,14144468586,17138648217,17739951311,15166265304,14029345347,17634242674,16466843001,34631056715,19103533090,16144864451,19798361011,12315787798,59399011420,17576893382,48503490724,20774369705,17736226659,17734637950,12063623921,17732688443,12068782762,14147713662,13302520351,18019736151,34632081401,16186926771,22374155812,18475700313,13046232502,34649845733,14405933643,94715942355,17196328712,6417699128,14017382152,16103234132,12392830536,17736266246,17542453277,16163552543,14495674047,34632178999,968000000000,12124912973,18473579772,19416295024,64107,12815905476,16144866326,19107611825,17734341000,12036378380,959700607,357000000000,13093432520,34609244070,9780000000000,13056353430,13369823312,18185475460,19107939931,13103131645,34606252207,17739550086,16075397997,19108223762,244000000000,18159424436,16194452421,19419667168,16104950424,16092830215,94714281159,12252055484,34653856691,13057547089,3021216078,594000000000,12818071127,15096274421,16182889829,12065228916,187000000000,1899058881,12106243465,15733582501,12514738274,355000000000,34644739357,15097845309,17734173603,16057166909,15612288898,19542270080,26712842789,34632081088,34632799818,18132515934,34631431452,4130000000000,16186551238,17732818155,15159673087,17736227175,18173188998,34632370591,37246,18157599489,13213853318,13604387351,18155686736,12033896281,12155799491,54344304029,19195630826,15617841659,34632684681,16144599016,12039331834,17164870869,19109938502,18474979885,34644298882,18159445313,381000000000,34631046557,17175257145,13864234145,13052996090,213000000000,17734178199,2406870098,15619653414,18708649596,17733781234,13369987194,17132900495,18604564609,646345839,17737531247,17088684563,12105259371,34638439656,91981557606,19544380739,37282542805,34616189813,19187448523,17739667958,19529228488,13055563222,12172851375,16302797092,657000000000,16082534364,15099968138,12548338734,5773653873,14258208231,14342959467,1514750252,12158863162,12098458588,418000000000,17659392149,12814962782],\"kclust\":[3,5,3,5,5,5,5,5,5,3,5,5,3,5,5,4,5,5,5,5,1,5,5,5,5,5,3,5,5,3,5,5,5,5,5,2,5,5,5,5,5,5,1,5,3,3,5,1,5,5,3,5,5,5,5,3,5,5,5,5,3,3,1,5,5,5,3,1,5,1,5,5,5,5,5,5,5,5,3,5,1,5,3,5,5,1,3,5,5,5,5,5,5,5,5,5,5,5,5,3,3,5,5,5,5,5,5,5,3,5,5,5,1,5,5,5,5,5,5,5,3,5,3,5,5,3,5,3,5,3,5,5,5,5,5,1,5,5,5,5,5,5,3,1,5,5,5,5,5,5,1,1,3,5,5,3,5,5,5,3,1,5,5,5,5,5,3,5,5,5,5,5,5,5,5,3,5,3,5,5,3,5,5,1,5,5,5,5,5,5,5,1,5,5,5,5,5,3,3,5,5,5,5,3,5,5,5,5,5,5,3,5,5,3,5,5,5,5,5,5,5,5,5,5,5,5,3,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,3,5,5,3,5,5,5,5,3,5,5,5,5,1,5,5,5,3,5,1,5,5,5,5,3,5,5,3,3,5,5,3,3,5,1,5,5,5,5,1,3,5,5,5,1,3,5,5,5,5,5,5,3,3,5,5,5,3,5,3,5,5,3,3,5,5,5,3,5,5,5,5,3,3,5,5,5,3,5,5,5,3,5,5,3,5,5,3,1,5,5,5,5,5,5,3,5,5,5,3,1,5,5,3,5,5,5,5,5,5,3,5,5,5,5,3,5,5,5,5,3,5,5],\"TotalUniqueCalee\":[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],\"TotalCallDuration\":[14084,4089,7696,4930,5566,3219,6695,4247,4608,10412,5293,3351,9307,5880,5747,2203873,4086,5404,4955,3023,23030,3535,3435,5662,4363,6437,9464,4672,4085,12981,4686,5153,5568,4328,4673,70040,4975,3229,2460,5074,4597,4253,15878,5462,8894,7472,3473,15767,5627,5513,9712,5881,3395,3399,5923,10758,5128,5186,6007,5111,9062,7686,15918,4232,6437,4428,10500,16141,5150,16197,4143,4718,5046,4329,2490,5555,3875,3940,9634,5995,18697,3035,10161,4509,3110,20102,7560,3149,5628,4779,3355,6632,3418,5977,4757,3813,5472,6437,3227,9122,9630,6309,4137,3965,3508,3543,5876,5769,13349,5727,3848,5225,32971,5574,5142,5004,5081,5465,4883,5655,10945,6776,13428,5496,6078,11477,4271,8710,3187,7735,5851,3512,2410,3741,6828,17273,6311,3671,3785,5071,6578,5511,9384,23847,3898,6997,6304,5760,3819,3751,17712,15078,7831,4192,3497,11719,4277,4205,4241,10653,23849,3465,5195,3949,4036,3901,9955,6437,6470,5733,6094,4846,4577,4536,3237,8163,5605,8407,4516,4441,7411,4719,3292,15860,5854,6673,3326,6650,3897,4521,4584,14908,6957,4639,5473,5658,4383,10412,8634,3904,4369,4067,7148,9266,6906,5809,4239,3994,4177,4330,9995,5938,3195,8393,4820,3882,4740,4887,5723,3428,5757,4249,3146,4999,3439,5317,11192,4245,6437,3271,3316,6621,5154,6437,5268,4070,5413,4788,5028,7046,3753,4250,4963,4147,4040,3603,4581,5823,7304,10202,3731,3797,12232,4238,5065,4355,6157,10237,5314,6665,5858,6801,16103,4957,3746,3980,10001,6398,18378,5100,3756,4732,4649,11547,3802,4014,9116,10689,3727,4512,13670,9046,4630,25857,4430,3960,6437,6940,19960,8372,5163,6978,3511,31338,9799,3455,5612,5102,3010,3362,3170,8974,7908,3805,4700,3114,12793,4418,9572,3247,3073,9522,8158,3121,3616,5824,14069,5622,4644,5390,4425,11096,8161,5426,4896,3623,8876,5445,3621,5464,10373,4427,5904,10610,4300,6010,8233,18351,4310,3580,4008,3676,4159,4547,9614,3204,4109,4141,10467,18410,6325,6675,9224,4323,4105,6089,3763,4988,4919,10463,4448,5068,3995,5674,9504,3988,5313,4358,6437,8514,5570,5073]}";
	}
	
	@RequestMapping(value="/kmeansucaller", method=RequestMethod.GET, produces="application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String kmeanuCallers(){
		return "{\"CallingNumber\":[34653736089,19283431093,34632405747,255000000000,19198270561,13046243017,522889805,19137738128,967778,13606291775,19544243816,13148674390,34625644447,12062852224,19107942249,245000000000,13124919343,18504299688,13174009729,23238830149,554000000000,9720000000000,12037765301,12536067570,19703468148,17086368301,19194671504,12816310065,34602159334,34642386996,811000000000,18636780411,14193009360,41223459139,16157738278,2206210930,34632590177,238895097,611000000000,15167590707,34632027887,18157824191,15092355452,12154120889,34608828237,34602123041,19547224569,34646667087,18149382229,17247961139,34632094329,17026840811,15705597036,19195447187,14343496462,34650746079,16106889098,9726278531,13202516060,12144141039,16304696117,1809521617,23599900185,18479616386,19709479189,526000000000,16316083621,16147774687,14235866235,18475448156,34632909173,13302536772,12037872585,17176502158,35522805013,19416391790,31206404146,14122130689,19197463960,14143322595,265,16605825586,12106459976,34631338964,34609190995,2206664348,826,16106179637,16108291536,14128351032,3354622117,12067676363,19042208002,9166139260,13235226389,16055825651,19102384813,13034707372,13099289850,272000000000,17402861252,18439035408,2348,13369987451,243539031,12813423100,34632699646,13034313627,16105780845,34631277732,17175973896,34632242963,64102298935,19516796367,13867890614,78242356345,16517148783,13602257866,12155792462,14144238282,34640020986,14438824488,34631154670,15402982186,34640020506,34656052691,17149968894,18153984386,94754404649,92300544140,46600000000000,13368712769,332369466,12158625348,12188282154,13016631512,17735685689,13148466090,2350000000000,12316259310,19372882572,17245654711,34631572682,59337613960,16182068891,2644465330,34632833140,16102807386,18146961952,16269651960,34640020073,16148533258,16302798222,18153851380,17084102362,14073818527,16308962897,15126938145,34632684734,16263694496,2206228541,17735385175,2210000000000,19732092984,34632331841,13057525007,34632459372,17242091200,17249047125,18648859745,12033821485,34644619054,12153344246,17863910806,18477401794,34632684744,34631682907,14127989060,646757991,16083487857,15092993148,18695572661,12103596531,34631085149,17734766909,38551286553,12312827007,8556182198,16513421064,14124227697,530000000000,93700000000000,16052560722,12032682318,12036551452,17144837396,22378165571,34620434315,12153651645,12178652875,15122589545,34632807200,19549425574,34616645607,11613730143,19414290847,14144468586,17138648217,17739951311,15166265304,14029345347,17634242674,16466843001,34631056715,19103533090,16144864451,19798361011,12315787798,59399011420,17576893382,48503490724,20774369705,17736226659,17734637950,12063623921,17732688443,12068782762,14147713662,13302520351,18019736151,34632081401,16186926771,22374155812,18475700313,13046232502,34649845733,14405933643,94715942355,17196328712,6417699128,14017382152,16103234132,12392830536,17736266246,17542453277,16163552543,14495674047,34632178999,968000000000,12124912973,18473579772,19416295024,64107,12815905476,16144866326,19107611825,17734341000,12036378380,959700607,357000000000,13093432520,34609244070,9780000000000,13056353430,13369823312,18185475460,19107939931,13103131645,34606252207,17739550086,16075397997,19108223762,244000000000,18159424436,16194452421,19419667168,16104950424,16092830215,94714281159,12252055484,34653856691,13057547089,3021216078,594000000000,12818071127,15096274421,16182889829,12065228916,187000000000,1899058881,12106243465,15733582501,12514738274,355000000000,34644739357,15097845309,17734173603,16057166909,15612288898,19542270080,26712842789,34632081088,34632799818,18132515934,34631431452,4130000000000,16186551238,17732818155,15159673087,17736227175,18173188998,34632370591,37246,18157599489,13213853318,13604387351,18155686736,12033896281,12155799491,54344304029,19195630826,15617841659,34632684681,16144599016,12039331834,17164870869,19109938502,18474979885,34644298882,18159445313,381000000000,34631046557,17175257145,13864234145,13052996090,213000000000,17734178199,2406870098,15619653414,18708649596,17733781234,13369987194,17132900495,18604564609,646345839,17737531247,17088684563,12105259371,34638439656,91981557606,19544380739,37282542805,34616189813,19187448523,17739667958,19529228488,13055563222,12172851375,16302797092,657000000000,16082534364,15099968138,12548338734,5773653873,14258208231,14342959467,1514750252,12158863162,12098458588,418000000000,17659392149,12814962782],\"kclust\":[4,3,4,3,3,3,3,3,3,4,3,3,4,3,3,5,3,3,3,3,1,3,3,3,3,3,4,3,3,4,3,3,3,3,3,2,3,3,3,3,3,3,1,3,4,4,3,1,3,3,4,3,3,3,3,4,3,3,3,3,4,4,1,3,3,3,4,1,3,1,3,3,3,3,3,3,3,3,4,3,1,3,4,3,3,1,4,3,3,3,3,3,3,3,3,3,3,3,3,4,4,3,3,3,3,3,3,3,4,3,3,3,1,3,3,3,3,3,3,3,4,3,4,3,3,4,3,4,3,4,3,3,3,3,3,1,3,3,3,3,3,3,4,1,3,3,3,3,3,3,1,1,4,3,3,4,3,3,3,4,1,3,3,3,3,3,4,3,3,3,3,3,3,3,3,4,3,4,3,3,4,3,3,1,3,3,3,3,3,3,3,1,3,3,3,3,3,4,4,3,3,3,3,4,3,3,3,3,3,3,4,3,3,4,3,3,3,3,3,3,3,3,3,3,3,3,4,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,4,3,3,4,3,3,3,3,4,3,3,3,3,1,3,3,3,4,3,1,3,3,3,3,4,3,3,4,4,3,3,4,4,3,1,3,3,3,3,1,4,3,3,3,1,4,3,3,3,3,3,3,4,4,3,3,3,4,3,4,3,3,4,4,3,3,3,4,3,3,3,3,4,4,3,3,3,4,3,3,3,4,3,3,4,3,3,4,1,3,3,3,3,3,3,4,3,3,3,4,1,3,3,4,3,3,3,3,3,3,4,3,3,3,3,4,3,3,3,3,4,3,3],\"TotalUniqueCalee\":[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],\"TotalCallDuration\":[14084,4089,7696,4930,5566,3219,6695,4247,4608,10412,5293,3351,9307,5880,5747,2203873,4086,5404,4955,3023,23030,3535,3435,5662,4363,6437,9464,4672,4085,12981,4686,5153,5568,4328,4673,70040,4975,3229,2460,5074,4597,4253,15878,5462,8894,7472,3473,15767,5627,5513,9712,5881,3395,3399,5923,10758,5128,5186,6007,5111,9062,7686,15918,4232,6437,4428,10500,16141,5150,16197,4143,4718,5046,4329,2490,5555,3875,3940,9634,5995,18697,3035,10161,4509,3110,20102,7560,3149,5628,4779,3355,6632,3418,5977,4757,3813,5472,6437,3227,9122,9630,6309,4137,3965,3508,3543,5876,5769,13349,5727,3848,5225,32971,5574,5142,5004,5081,5465,4883,5655,10945,6776,13428,5496,6078,11477,4271,8710,3187,7735,5851,3512,2410,3741,6828,17273,6311,3671,3785,5071,6578,5511,9384,23847,3898,6997,6304,5760,3819,3751,17712,15078,7831,4192,3497,11719,4277,4205,4241,10653,23849,3465,5195,3949,4036,3901,9955,6437,6470,5733,6094,4846,4577,4536,3237,8163,5605,8407,4516,4441,7411,4719,3292,15860,5854,6673,3326,6650,3897,4521,4584,14908,6957,4639,5473,5658,4383,10412,8634,3904,4369,4067,7148,9266,6906,5809,4239,3994,4177,4330,9995,5938,3195,8393,4820,3882,4740,4887,5723,3428,5757,4249,3146,4999,3439,5317,11192,4245,6437,3271,3316,6621,5154,6437,5268,4070,5413,4788,5028,7046,3753,4250,4963,4147,4040,3603,4581,5823,7304,10202,3731,3797,12232,4238,5065,4355,6157,10237,5314,6665,5858,6801,16103,4957,3746,3980,10001,6398,18378,5100,3756,4732,4649,11547,3802,4014,9116,10689,3727,4512,13670,9046,4630,25857,4430,3960,6437,6940,19960,8372,5163,6978,3511,31338,9799,3455,5612,5102,3010,3362,3170,8974,7908,3805,4700,3114,12793,4418,9572,3247,3073,9522,8158,3121,3616,5824,14069,5622,4644,5390,4425,11096,8161,5426,4896,3623,8876,5445,3621,5464,10373,4427,5904,10610,4300,6010,8233,18351,4310,3580,4008,3676,4159,4547,9614,3204,4109,4141,10467,18410,6325,6675,9224,4323,4105,6089,3763,4988,4919,10463,4448,5068,3995,5674,9504,3988,5313,4358,6437,8514,5570,5073]}";
	}
	
	
	@RequestMapping(value="/normal", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<NormalElements> normalDistribution(){
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath+"/upload");
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
		File dir = new File(rootPath+"/upload");
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
			if(!markedFrauds.contains(callerModel.getkey()) && !callerModel.getkey().isEmpty()){
			NormalElements nElements = new NormalElements(callerModel.getkey(), (int)Math.round(nd.cumulativeProbability(callerModel.getvalue())*100));
			normalElements.add(nElements);	
			}
		}
		if(normalElements.size()>100){
		return normalElements.subList(0, 100);
		}else{
			return normalElements;
		}
		
		
	}
}
