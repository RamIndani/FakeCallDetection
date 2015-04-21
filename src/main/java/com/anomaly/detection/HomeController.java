package com.anomaly.detection;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	
}
