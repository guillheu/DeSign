<%@page import="core.DeSignAppLauncher"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

    
<% 
String configFilePath = "./config.properties";
DeSignAppLauncher launcher = new DeSignAppLauncher(configFilePath); 
int documentID = Integer.parseInt(request.getParameter("id"));
String result = "Failed to export signature";
if(launcher.exportSigProof(documentID, launcher.getDefaultFilePath())){
	result = "successfully exported proof of signature at " + launcher.getDefaultFilePath() +"sigProof.json";
}
response.sendRedirect("../?result="+result);
%>
<meta charset="UTF-8">
<title>signing document volume</title>
</head>
<body>

</body>
</html>