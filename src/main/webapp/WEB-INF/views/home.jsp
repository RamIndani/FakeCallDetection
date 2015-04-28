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

</head>
<body class="content">
	<nav class="navbar">
  		<div class="container-fluid">
  			<div class="navbar-header">
      			<a class="navbar-brand" href="#">
      				<h3 class="brand" style="color:#FFFFFF"><b>FakeCallDetection!</b></h3>
      			</a>
    		</div>
  		  	<ul class="nav navbar-nav navbar-left">
           <li class="navbar-text">
           <p class="navbar-text">
            <a href="/" style="color:white;">Home</a>
          </p> 
        </li>	
			</ul>
  		</div>
	</nav>
<br/>
<div class="col-md-2 well" style="width:80%;margin-left:50px;">
	<h3 class="text-center">Marked And Detected Anomalies</h3>
	<table style="width:100%;" id="anomalyTable" class="table table-striped"> 
	<thead> 
	<tr> 
    	<th>Number</th> 
    	<th>Probability</th> 
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
		</tr>
	</script>

<script type="text/javascript">
$(document).ready(function() 
	    { 
	        $("#anomalyTable").tablesorter(); 
	    }); 

	$.ajax({
		  url: "/getfraud"
		}).done(function(result) {
			$("#anomaly").html(
	        		
		            $("#anomalyTemplate").render(result)
		        );
	});

</script>
<br/>
<div class="footer">
		<b> All Rights Reserved Team12!</b>
	</div>
</body>
</html>