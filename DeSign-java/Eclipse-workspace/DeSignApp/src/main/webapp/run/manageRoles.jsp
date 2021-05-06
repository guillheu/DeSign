<%@page import="core.DeSignAppLauncher"%>
<%@page import="web.WebHelper"%>
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
String action = request.getParameter("action");
String address = request.getParameter("address");
String result="";
try{
switch(action){
case "isSig":
	if(DeSignAppLauncher.isSignatory(address))
		result = "The address " + address + " is signatory";
	else
		result = "The address " + address + " is not signatory";
	break;
case "isAdmin":
		if(DeSignAppLauncher.isDefaultAdmin(address))
			result = "The address " + address + " is default admin";
		else
			result = "The address " + address + " is not default admin";
	break;
case "makeSig":
	DeSignAppLauncher.makeSignatory(address);
	result = "Successfully granted signatory role to " + address+ "<br>";
	result += WebHelper.printTransaction(DeSignAppLauncher.getLastTransaction(), DeSignAppLauncher.getNetworkID());
	break;
case "revokeSig":
	DeSignAppLauncher.revokeSignatory(address);
	result = "Successfully revoked signatory role from " + address+ "<br>";
	result += WebHelper.printTransaction(DeSignAppLauncher.getLastTransaction(), DeSignAppLauncher.getNetworkID());
	break;
default:
	result="unknown action";
	break;
}


}

catch(Exception e){
	result="bad request";
}



%>
<meta charset="UTF-8">
<title>manage roles</title>
</head>
<body>

<%= result %>
<form action="../">
<input type="submit" value="back"><br>
</form><br>
</body>
</html>