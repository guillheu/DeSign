<%@page import="core.DeSignAppLauncher"%>
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
int documentID = Integer.parseInt(request.getParameter("id"));
String result = "Failed to export signature";
if(DeSignAppLauncher.exportSigProof(documentID, DeSignAppLauncher.getDefaultFilePath())){
	result = "successfully exported proof of signature at " + DeSignAppLauncher.getDefaultFilePath() +"sigProof.json";
}
%>
<meta charset="UTF-8">
<title>proof of signature export</title>
</head>
<body>

<%= result %>
<form action="../">
<input type="submit" value="back"><br>
</form><br>
</body>
</html>