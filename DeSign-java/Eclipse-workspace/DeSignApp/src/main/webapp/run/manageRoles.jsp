<%@page import="core.DeSignAppLauncher"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
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
		if(DeSignAppLauncher.isSignatory(address))
			result = "The address " + address + " is default admin";
		else
			result = "The address " + address + " is not default admin";
	break;
case "makeSig":
	DeSignAppLauncher.makeSignatory(address);
	result = "Successfully granted signatory role to " + address;
	break;
case "revokeSig":
	DeSignAppLauncher.revokeSignatory(address);
	result = "Successfully revoked signatory role from " + address;
	break;
default:
	result="unknown action";
	break;
}
}

catch(Exception e){
	result="bad request";
}

response.sendRedirect("../?result=" + result);
%>
<meta charset="UTF-8">
<title>signing document volume</title>
</head>
<body>

</body>
</html>