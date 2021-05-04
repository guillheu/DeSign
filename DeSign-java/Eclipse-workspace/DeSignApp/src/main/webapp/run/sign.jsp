<%@page import="core.DeSignAppLauncher"%>
<%@page import="org.web3j.protocol.core.methods.response.Log"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="../style.css" media="screen" />
<% 
if(DeSignAppLauncher.getNodeURL() == null)
	DeSignAppLauncher.initFromWeb(); %>
    
<% 
String index = request.getParameter("index");
int days = Integer.parseInt(request.getParameter("daysOfValidity"));
String result="";
if(DeSignAppLauncher.signDocumentVolume(index, days)){
	result="Signature successful!<br><br>";
	result += "Transaction hash : "+DeSignAppLauncher.getLastTransaction().getTransactionHash()+ "<br>";
	result += "Block number : "+DeSignAppLauncher.getLastTransaction().getBlockNumber() + "<br>";
	result += "Gas used : "+DeSignAppLauncher.getLastTransaction().getCumulativeGasUsed()+ "<br>";
}
else{
	result = "Signature failed";
}


%>


<meta charset="UTF-8">
<title>signing document volume</title>
</head>
<body>
<%= result %><br>
<form action="../">
<input type="submit" value="back"><br>
</form><br>


</body>
</html>