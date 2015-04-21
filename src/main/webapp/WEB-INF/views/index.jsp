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
<script type="text/javascript" src="js/bootstrap.js"></script>

<script src="http://d3js.org/d3.v3.min.js"></script>
</head>
<body class="content">
	<nav class="navbar">
  		<div class="container-fluid">
  			<div class="navbar-header">
      			<a class="navbar-brand" href="#">
      				<h3 class="brand" style="color:#FFFFFF"><b>FakeCallDetection!</b></h3>
      			</a>
    		</div>
  		  	<ul class="nav navbar-nav navbar-right">
            <li class="navbar-text">
          <p class="navbar-text">
            <a href="#">Charts</a>
          </p>
        </li>
				<li class="navbar-text">
					<form class="navbar-form navbar-left" role="search">
            <div class="form-group">
              <label style="color:#FFFFFF">Select Fake Call Possibility Filter(%)</label>
              <input type="number" class="form-control" placeholder="Select Fake Call Possibility Filter" min="1" max="100">
            </div>
            <button type="submit" class="btn btn-default">Go!</button>
          </form>
				</li>
			</ul>
  		</div>
	</nav>

	<div class="row booksgrid">
  		<div class="col-md-1 indibook uniqucallers" id="uniqucallers">
  		</div>
  		<div class="col-md-1 indibook uniqucallee" id="uniqucallee">
  			
  		</div>
      <br/>
  		<div class="col-md-1 indibook">
  			<img src="img/pca.png" width="515px" height="650px"/>
  		</div>
  		<div class="col-md-1 indibook">
  			<img src="img/cluster.png" width="515px" height="650px"/>
  		</div>

  			<br/><br/><br/><br/><br/><br/>
	</div>
</div>
	<div class="footer">
		<b> All Rights Reserved Team12!</b>
	</div>
<script type="text/javascript">
var data = [
{ key:19854698828, value:92},
{ key:22376977724, value:2},
{ key:19792309484, value:3},
{ key:34629501882, value:1},
{ key:212523000000, value:1},
{ key:5, value:47},
{ key:94725808193, value:1},
{ key:33638788526, value:1},
{ key:441111000000, value:3},
{ key:21620307945, value:1},
{ key:1422856494, value:1},
{ key:19857784839, value:18},
{ key:2104790336, value:4},
{ key:5153290965, value:3},
{ key:4123512893, value:3},
{ key:8341699388, value:1},
{ key:19792302584, value:4},
{ key:13123127787, value:2},
{ key:197878000000000000, value:7},
{ key:13094740868, value:15},
{ key:212619000000, value:1},
{ key:442071000000, value:2},
{ key:963496123, value:1},
{ key:934472101, value:1},
{ key:19896832644, value:8},
{ key:465567000000, value:1},
{ key:497273000000, value:2}
];

var calleeData =[
{ key:2349100000000, value:4},
{ key:59337613960, value:3},
{ key:9779620000000, value:2},
{ key:13056353430, value:1},
{ key:13369823312, value:1},
{ key:18185475460, value:1},
{ key:19107939931, value:1},
{ key:13103131645, value:1},
{ key:14144238282, value:1},
{ key:34640020986, value:1},
{ key:14438824488, value:1},
{ key:17245654711, value:1},
{ key:34631572682, value:1},
{ key:14144238282, value:1},
{ key:34640020986, value:1},
{ key:14438824488, value:1},
{ key:17245654711, value:1},
{ key:34631572682, value:1},
{ key:14144238282, value:1},
{ key:34640020986, value:1},
{ key:14438824488, value:1},
{ key:17245654711, value:1},
{ key:34631572682, value:1},
{ key:14144238282, value:1},
{ key:34640020986, value:1},
{ key:14438824488, value:1},
{ key:17245654711, value:1},
{ key:34631572682, value:1}
]
;
var w = 515;
var h = 660;
var x = d3.scale.linear()
  .domain([0, d3.max(data, function(d) { return d.value; })])
  .range([0, w]);
var y = d3.scale.ordinal()
  .domain(d3.range(data.length))
  .rangeBands([0, h], 0.1);
  
var color = d3.scale.ordinal()
.range(["red", "blue"]);

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
  
  
  
var w = 515;
var h = 660;
var x = d3.scale.linear()
  .domain([0, d3.max(calleeData, function(d) { return d.value; })])
  .range([0, w]);
var y = d3.scale.ordinal()
  .domain(d3.range(calleeData.length))
  .rangeBands([0, h], 0.1);
  
var color = d3.scale.ordinal()
.range(["red", "blue"]);

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
  
</script>
</body>
</html>