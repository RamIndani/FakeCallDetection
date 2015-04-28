<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
<title>FakeCallDetection!</title>
<link rel="stylesheet" href="css/bootstrap.css"></link>
<link rel="stylesheet" href="css/anomaly.css"></link>
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/jsrender.js"></script>
<script type="text/javascript" src="js/bootstrap.js"></script>
<script type="text/javascript" src="js/jquery.tablesorter.js"></script> 

<script src="http://d3js.org/d3.v3.min.js"></script>

<script type="text/javascript" src="http://mbostock.github.com/d3/d3.v2.js"></script>
<script type="text/javascript">
function reload(){
	window.location.reload();
}
function fraud(number){
	
	$.ajax({
		  url: "/fraud/"+number
		}).done(function(result) {
			alert("fraud number : "+number+ " marked");
			var button = document.getElementById(number).className="btn btn-success";
			
		});
}
</script>
</head>
<body class="content">
	<nav class="navbar">
  		<div class="container-fluid">
  			<div class="navbar-header">
      			<a class="navbar-brand" href="/home"">
      				<h3 class="brand" style="color:#FFFFFF"><b>FakeCallDetection!</b></h3>
      			</a>
    		</div>
  		  	<ul class="nav navbar-nav navbar-right">
          <!--
				<li class="navbar-text">
					<form class="navbar-form" role="search">
            <div class="form-group">
              <label style="color:#FFFFFF">Select Fake Call Possibility Filter(%)</label>
              <input type="number" class="form-control" placeholder="Select Fake Call Possibility Filter" min="1" max="100">
            </div>
            <button type="submit" class="btn btn-default">Go!</button>
          </form>
          
				</li>-->
				<li class="col-md-10 well navbar-text">
				<form enctype="multipart/form-data" name="fileinfo">
					<fieldset>
						<legend> Upload test file  </legend>
						<input type="file" name="file"/>
						 <div><input type="submit" class="btn btn-primary alignleft" value="Upload" />
						 <input type="button" class="btn btn-primary alignleft" value="test" onclick="javascript:reload();"/> </div>
					 </fieldset><br/>
					 </form>
					<div id="output"></div>
	</li>
			</ul>
  		</div>
	</nav>

	<div class="row booksgrid">
		
  		<div class="col-md-2 indibook uniqucallers" id="uniqucallers" style="width:80%;">
  		<h3 class="text-center">UniqueCallers</h3>
  		</div>
  		<div class="col-md-2 indibook uniqucallee" id="uniqucallee" style="width:80%;">
  			<h3 class="text-center">UniqueCallees</h3>
  		</div>
  		<br/>
      <br/>
  		<div class="col-md-1 indibook uniqucallee" id="kmeans" style="width:80%;">
		<h3 class="text-center">K-Means Unique Callers Cluster</h3>
<!-- <img src="img/pca.png" width="515px" height="650px" /> -->
  		</div>
  		<br/>
  		<div class="col-md-1 indibook uniquecallee" id="scatter" style="width:80%;">
  		<h3 class="text-center">K-Means Unique Callees Cluster</h3>
  			<!-- <img src="img/cluster.png" width="515px" height="650px"/> -->
  		</div>

  			<br/><br/><br/><br/><br/><br/>
	</div>

	
<script type="text/javascript">
$.ajax({
	  url: "/uniquecallers"
	}).done(function(result) {
	  var data = result;
	  var w = 515;
	  var h = 660;
	  var x = d3.scale.linear()
	    .domain([0, d3.max(data, function(d) { return d.value; })])
	    .range([0, w]);
	  var y = d3.scale.ordinal()
	    .domain(d3.range(data.length))
	    .rangeBands([0, h], 0.1);
	 
	  var color = d3.scale.ordinal()
	  .range(["mediumseagreen", "cornflowerblue"]);

	  var svg = d3.select("#uniqucallers")
	  .append("svg")
	    .attr("width", w)
	    .attr("height", h)
	  .append("g")
	    .attr("transform", "translate(0,0)");
	  var bars = svg.selectAll(".bar")
	  .data(data)
	  .enter().append("g")
	  .attr("class", "bar")
	  .attr("transform", function(d, i) { return "translate(" + 0 + "," + y(i+1) + ")"; });

	  bars.append("rect")
	    .attr("fill", function(d, i) { return color(i%2); })
	    .attr("width", function(d) { return x(d.value); })
	    .attr("height", y.rangeBand());
	    
	  bars.append("text")
	    .attr("x", function(d) { return x(d.value); })
	    .attr("y", 0 + y.rangeBand() / 2)
	    .attr("dx", -6)
	    .attr("dy", ".35em")
	    .attr("text-anchor", "end")
	    .text(function(d) { return d.value; });
	    
	});
	

$.ajax({
	  url: "/uniquecallee"
	}).done(function(result) {
		var calleeData = result;
		var w = 515;
		var h = 660;
		var x = d3.scale.linear()
		  .domain([0, d3.max(calleeData, function(d) { return d.value; })])
		  .range([0, w]);
		var y = d3.scale.ordinal()
		  .domain(d3.range(calleeData.length))
		  .rangeBands([0, h], 0.1);
		  
		var color = d3.scale.ordinal()
		.range(["mediumseagreen", "mediumpurple"]);

		var svg = d3.select("#uniqucallee")
		.append("svg")
		  .attr("width", w)
		  .attr("height", h)
		.append("g")
		  .attr("transform", "translate(0,0)");
		var bars = svg.selectAll(".bar")
		.data(calleeData)
		.enter().append("g")
		.attr("class", "bar")
		.attr("transform", function(d, i) { return "translate(" + 0 + "," + y(i+1) + ")"; });

		bars.append("rect")
		  .attr("fill", function(d, i) { return color(i%2); })
		  .attr("width", function(d) { return x(d.value); })
		  .attr("height", y.rangeBand());
		  
		bars.append("text")
		  .attr("x", function(d) { return x(d.value); })
		  .attr("y", 0 + y.rangeBand() / 2)
		  .attr("dx", -6)
		  .attr("dy", ".35em")
		  .attr("text-anchor", "end")
		  .text(function(d) { return d.value; });
	});
	
	
$.ajax({
	  url: "/kmeansUniqueCallee",
	  
	  //contentType: "application/json",
	  dataType: "json"
	}).done(function(result) {
		var jsonData = result;
/*
 * scatter plot
 */
 
var margin = {top: 20, right: 20, bottom: 30, left: 40},
    width = 960 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

/* 
 * value accessor - returns the value to encode for a given data object.
 * scale - maps value to a visual display encoding, such as a pixel position.
 * map function - maps from data value to display value
 * axis - sets up axis
 */ 

// setup x 
var xValue = function(d) { return d[0];}, // data -> value
    xScale = d3.scale.linear().range([0, width]), // value -> display
    xMap = function(d) { return xScale(xValue(d));}, // data -> display
    xAxis = d3.svg.axis().scale(xScale).orient("bottom");

// setup y
var yValue = function(d) { return d[1];}, // data -> value
    yScale = d3.scale.linear().range([height, 0]), // value -> display
    yMap = function(d) { return yScale(yValue(d));}, // data -> display
    yAxis = d3.svg.axis().scale(yScale).orient("left");

// setup fill color
var cValue = function(d) { return d[2];},
    color = d3.scale.category10();

// add the graph canvas to the body of the webpage
var svg = d3.select("#kmeans").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

// add the tooltip area to the webpage
var tooltip = d3.select("body").append("div")
    .attr("class", "tooltip")
    .style("opacity", 0);



// load data
//var jsonData={"CallingNumber":[34653736089,19283431093,34632405747,255000000000,19198270561,13046243017,522889805,19137738128,967778,13606291775,19544243816,13148674390,34625644447,12062852224,19107942249,245000000000,13124919343,18504299688,13174009729,23238830149,554000000000,9720000000000,12037765301,12536067570,19703468148,17086368301,19194671504,12816310065,34602159334,34642386996,811000000000,18636780411,14193009360,41223459139,16157738278,2206210930,34632590177,238895097,611000000000,15167590707,34632027887,18157824191,15092355452,12154120889,34608828237,34602123041,19547224569,34646667087,18149382229,17247961139,34632094329,17026840811,15705597036,19195447187,14343496462,34650746079,16106889098,9726278531,13202516060,12144141039,16304696117,1809521617,23599900185,18479616386,19709479189,526000000000,16316083621,16147774687,14235866235,18475448156,34632909173,13302536772,12037872585,17176502158,35522805013,19416391790,31206404146,14122130689,19197463960,14143322595,265,16605825586,12106459976,34631338964,34609190995,2206664348,826,16106179637,16108291536,14128351032,3354622117,12067676363,19042208002,9166139260,13235226389,16055825651,19102384813,13034707372,13099289850,272000000000,17402861252,18439035408,2348,13369987451,243539031,12813423100,34632699646,13034313627,16105780845,34631277732,17175973896,34632242963,64102298935,19516796367,13867890614,78242356345,16517148783,13602257866,12155792462,14144238282,34640020986,14438824488,34631154670,15402982186,34640020506,34656052691,17149968894,18153984386,94754404649,92300544140,46600000000000,13368712769,332369466,12158625348,12188282154,13016631512,17735685689,13148466090,2350000000000,12316259310,19372882572,17245654711,34631572682,59337613960,16182068891,2644465330,34632833140,16102807386,18146961952,16269651960,34640020073,16148533258,16302798222,18153851380,17084102362,14073818527,16308962897,15126938145,34632684734,16263694496,2206228541,17735385175,2210000000000,19732092984,34632331841,13057525007,34632459372,17242091200,17249047125,18648859745,12033821485,34644619054,12153344246,17863910806,18477401794,34632684744,34631682907,14127989060,646757991,16083487857,15092993148,18695572661,12103596531,34631085149,17734766909,38551286553,12312827007,8556182198,16513421064,14124227697,530000000000,93700000000000,16052560722,12032682318,12036551452,17144837396,22378165571,34620434315,12153651645,12178652875,15122589545,34632807200,19549425574,34616645607,11613730143,19414290847,14144468586,17138648217,17739951311,15166265304,14029345347,17634242674,16466843001,34631056715,19103533090,16144864451,19798361011,12315787798,59399011420,17576893382,48503490724,20774369705,17736226659,17734637950,12063623921,17732688443,12068782762,14147713662,13302520351,18019736151,34632081401,16186926771,22374155812,18475700313,13046232502,34649845733,14405933643,94715942355,17196328712,6417699128,14017382152,16103234132,12392830536,17736266246,17542453277,16163552543,14495674047,34632178999,968000000000,12124912973,18473579772,19416295024,64107,12815905476,16144866326,19107611825,17734341000,12036378380,959700607,357000000000,13093432520,34609244070,9780000000000,13056353430,13369823312,18185475460,19107939931,13103131645,34606252207,17739550086,16075397997,19108223762,244000000000,18159424436,16194452421,19419667168,16104950424,16092830215,94714281159,12252055484,34653856691,13057547089,3021216078,594000000000,12818071127,15096274421,16182889829,12065228916,187000000000,1899058881,12106243465,15733582501,12514738274,355000000000,34644739357,15097845309,17734173603,16057166909,15612288898,19542270080,26712842789,34632081088,34632799818,18132515934,34631431452,4130000000000,16186551238,17732818155,15159673087,17736227175,18173188998,34632370591,37246,18157599489,13213853318,13604387351,18155686736,12033896281,12155799491,54344304029,19195630826,15617841659,34632684681,16144599016,12039331834,17164870869,19109938502,18474979885,34644298882,18159445313,381000000000,34631046557,17175257145,13864234145,13052996090,213000000000,17734178199,2406870098,15619653414,18708649596,17733781234,13369987194,17132900495,18604564609,646345839,17737531247,17088684563,12105259371,34638439656,91981557606,19544380739,37282542805,34616189813,19187448523,17739667958,19529228488,13055563222,12172851375,16302797092,657000000000,16082534364,15099968138,12548338734,5773653873,14258208231,14342959467,1514750252,12158863162,12098458588,418000000000,17659392149,12814962782],"kclust":[4,3,4,3,3,3,3,3,3,4,3,3,4,3,3,5,3,3,3,3,2,3,3,3,3,3,4,3,3,4,3,3,3,3,3,1,3,3,3,3,3,3,2,3,4,4,3,2,3,3,4,3,3,3,3,4,3,3,3,3,4,4,2,3,3,3,4,2,3,2,3,3,3,3,3,3,3,3,4,3,2,3,4,3,3,2,4,3,3,3,3,3,3,3,3,3,3,3,3,4,4,3,3,3,3,3,3,3,4,3,3,3,2,3,3,3,3,3,3,3,4,3,4,3,3,4,3,4,3,4,3,3,3,3,3,2,3,3,3,3,3,3,4,2,3,3,3,3,3,3,2,2,4,3,3,4,3,3,3,4,2,3,3,3,3,3,4,3,3,3,3,3,3,3,3,4,3,4,3,3,4,3,3,2,3,3,3,3,3,3,3,2,3,3,3,3,3,4,4,3,3,3,3,4,3,3,3,3,3,3,4,3,3,4,3,3,3,3,3,3,3,3,3,3,3,3,4,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,4,3,3,4,3,3,3,3,4,3,3,3,3,2,3,3,3,4,3,2,3,3,3,3,4,3,3,4,4,3,3,4,4,3,2,3,3,3,3,2,4,3,3,3,2,4,3,3,3,3,3,3,4,4,3,3,3,4,3,4,3,3,4,4,3,3,3,4,3,3,3,3,4,4,3,3,3,4,3,3,3,4,3,3,4,3,3,4,2,3,3,3,3,3,3,4,3,3,3,4,2,3,3,4,3,3,3,3,3,3,4,3,3,3,3,4,3,3,3,3,4,3,3],"TotalUniqueCalee":[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],"TotalCallDuration":[14084,4089,7696,4930,5566,3219,6695,4247,4608,10412,5293,3351,9307,5880,5747,22038,4086,5404,4955,3023,23030,3535,3435,5662,4363,6437,9464,4672,4085,12981,4686,5153,5568,4328,4673,70040,4975,3229,2460,5074,4597,4253,15878,5462,8894,7472,3473,15767,5627,5513,9712,5881,3395,3399,5923,10758,5128,5186,6007,5111,9062,7686,15918,4232,6437,4428,10500,16141,5150,16197,4143,4718,5046,4329,2490,5555,3875,3940,9634,5995,18697,3035,10161,4509,3110,20102,7560,3149,5628,4779,3355,6632,3418,5977,4757,3813,5472,6437,3227,9122,9630,6309,4137,3965,3508,3543,5876,5769,13349,5727,3848,5225,32971,5574,5142,5004,5081,5465,4883,5655,10945,6776,13428,5496,6078,11477,4271,8710,3187,7735,5851,3512,2410,3741,6828,17273,6311,3671,3785,5071,6578,5511,9384,23847,3898,6997,6304,5760,3819,3751,17712,15078,7831,4192,3497,11719,4277,4205,4241,10653,23849,3465,5195,3949,4036,3901,9955,6437,6470,5733,6094,4846,4577,4536,3237,8163,5605,8407,4516,4441,7411,4719,3292,15860,5854,6673,3326,6650,3897,4521,4584,14908,6957,4639,5473,5658,4383,10412,8634,3904,4369,4067,7148,9266,6906,5809,4239,3994,4177,4330,9995,5938,3195,8393,4820,3882,4740,4887,5723,3428,5757,4249,3146,4999,3439,5317,11192,4245,6437,3271,3316,6621,5154,6437,5268,4070,5413,4788,5028,7046,3753,4250,4963,4147,4040,3603,4581,5823,7304,10202,3731,3797,12232,4238,5065,4355,6157,10237,5314,6665,5858,6801,16103,4957,3746,3980,10001,6398,18378,5100,3756,4732,4649,11547,3802,4014,9116,10689,3727,4512,13670,9046,4630,25857,4430,3960,6437,6940,19960,8372,5163,6978,3511,31338,9799,3455,5612,5102,3010,3362,3170,8974,7908,3805,4700,3114,12793,4418,9572,3247,3073,9522,8158,3121,3616,5824,14069,5622,4644,5390,4425,11096,8161,5426,4896,3623,8876,5445,3621,5464,10373,4427,5904,10610,4300,6010,8233,18351,4310,3580,4008,3676,4159,4547,9614,3204,4109,4141,10467,18410,6325,6675,9224,4323,4105,6089,3763,4988,4919,10463,4448,5068,3995,5674,9504,3988,5313,4358,6437,8514,5570,5073]};

var callingNumber=jsonData.CallingNumber;
var kclust=jsonData.kclust;
var TotalUniqueCalee=jsonData.TotalUniqueCalee;
var TotalCallDuration=jsonData.TotalCallDuration;
var data=[];
for(var i=0;i<TotalUniqueCalee.length;i++){
  var input=[TotalCallDuration[i],TotalUniqueCalee[i],kclust[i]];
  data.push(input);
}

  // don't want dots overlapping axis, so add in buffer to data domain
  xScale.domain([d3.min(data, xValue)-1, d3.max(data, xValue)+1]);
  yScale.domain([d3.min(data, yValue)-1, d3.max(data, yValue)+1]);

  // x-axis
  svg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis)
    .append("text")
      .attr("class", "label")
      .attr("x", width)
      .attr("y", -6)
      .style("text-anchor", "end")
      .text("Call Duration");

  // y-axis
  svg.append("g")
      .attr("class", "y axis")
      .call(yAxis)
    .append("text")
      .attr("class", "label")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("unique Callee");

  // draw dots
  svg.selectAll(".dot")
      .data(data)
    .enter().append("circle")
      .attr("class", "dot")
      .attr("r", 3.5)
      .attr("cx", xMap)
      .attr("cy", yMap)
      .style("fill", function(d) { return color(cValue(d));}) 
      .on("mouseover", function(d) {
          tooltip.transition()
               .duration(200)
               .style("opacity", .9);
          tooltip.html(d[2] + "<br/> (" + xValue(d) 
	        + ", " + yValue(d) + ")")
               .style("left", (d3.event.pageX + 5) + "px")
               .style("top", (d3.event.pageY - 28) + "px");
      })
      .on("mouseout", function(d) {
          tooltip.transition()
               .duration(500)
               .style("opacity", 0);
      });

  // draw legend
  var legend = svg.selectAll(".legend")
      .data(color.domain())
    .enter().append("g")
      .attr("class", "legend")
      .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });

  // draw legend colored rectangles
  legend.append("rect")
      .attr("x", width - 18)
      .attr("width", 18)
      .attr("height", 18)
      .style("fill", color);

  // draw legend text
  legend.append("text")
      .attr("x", width - 24)
      .attr("y", 9)
      .attr("dy", ".35em")
      .style("text-anchor", "end")
      .text(function(d) { return d;});
	});


$(document).ready(function() 
    { 
        $("#anomalyTable").tablesorter(); 
    }); 

$.ajax({
	  url: "/normal"
	}).done(function(result) {
		$("#anomaly").html(
        		
	            $("#anomalyTemplate").render(result)
	        );
});

</script>
<br/>
<div class="col-md-2 well" style="width:80%;margin-left:50px;">
	<h3 class="text-center">Possible Anomalies Unique Callers</h3>
	<table style="width:100%;" id="anomalyTable" class="table table-striped"> 
	<thead> 
	<tr> 
    	<th>Number</th> 
    	<th>Probability</th> 
    	<th>Mark Fraud</th>  
	</tr> 
	</thead>
	<tbody id="anomaly">
	</tbody>
	
</table>
</div>
<br/><br/>
<script id="anomalyTemplate" type="text/x-jsrender"> 
		<tr> 
    		<td>{{:number}}</td> 
    		<td>{{:probability}}</td> 
    		<td><input type="button" value="Fraud" class="btn btn-danger" id={{:number}} onclick="javascript:fraud({{:number}});"></input></td>
		</tr>
	</script>

<script type="text/javascript">
$(document).ready(function() 
    { 
        $("#anomalyCalleeTable").tablesorter(); 
    }); 

$.ajax({
	  url: "/normalCallee"
	}).done(function(result) {
		$("#anomalyCallee").html(
        		
	            $("#anomalyTemplate").render(result)
	        );
});

</script>
<br/>
<div class="col-md-2 well" style="width:80%;margin-left:50px;">
	<h3 class="text-center">Possible Anomalies Unique Callees</h3>
	<table style="width:100%;" id="anomalyCalleeTable" class="table table-striped"> 
	<thead> 
	<tr> 
    	<th>Number</th> 
    	<th>Probability</th> 
    	<th>Mark Fraud</th>  
	</tr> 
	</thead>
	<tbody id="anomalyCallee">
	</tbody>
	
</table>
</div>

<script id="anomalyTemplate" type="text/x-jsrender"> 
		<tr> 
    		<td>{{:number}}</td> 
    		<td>{{:probability}}</td> 
    		<td><input type="button" value="Fraud" class="btn btn-danger" onclick="javascript:fraud({{:number}});"></input></td>
		</tr>
	</script>


<script type="text/javascript" src="js/anomaly.js"></script>
<script type="text/javascript" src="js/scatterchart.js"></script>
<div class="footer">
		<b> All Rights Reserved Team12!</b>
	</div>
</body>
</html>