<%@page import="core.DeSignAppLauncher"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

    
<% 
String configFilePath = "./config.properties";
DeSignAppLauncher launcher = new DeSignAppLauncher(configFilePath); 
String index = request.getParameter("index");
int days = Integer.parseInt(request.getParameter("daysOfValidity"));
String result="";
if(launcher.signDocumentVolume(index, days)){
	result="Signature successful!";
}
else{
	result = "Signature failed";
}
response.sendRedirect("../?result=" + result);
%>
<meta charset="UTF-8">
<title>signing document volume</title>
</head>
<body>

</body>
</html>