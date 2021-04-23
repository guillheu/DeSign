<%@page import="core.DeSignAppLauncher"%>
<%@page import="core.DeSignCore"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<% 
String configFilePath = "./config.properties";
DeSignAppLauncher launcher = new DeSignAppLauncher(configFilePath); %>  
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>DeSign web admin</title>
</head>
<body>
<%
String result;

result = request.getParameter("result");
if(result == null){
	result = "";
}

%>
<%= result %><br>

<br>
 Node URL :  <%= launcher.getNodeURL() %><br>
 User address :  <%= launcher.getUserAddress() %><br>
 User account balance :  <%= launcher.getAccountBalance() %><br>
 Node URL :  <%=launcher.getContractAddress()%><br><br>
 Available actions : <br>
<form action="./import.jsp">
Import documents into the database <br>
<input type="submit" value="Submit" disabled> not yet implemented, use CLI client or some other means<br>
</form><br>
<form action="./sign.jsp">
Sign a document volume <br>
<input type="submit" value="Submit"><br>
</form><br>
<form action="./check.jsp">
Check the integrity of the database against a blockchain signature<br>
<input type="submit" value="Submit"><br>
</form><br>
<form action="./export.jsp">
Export a proof of signature <br>
<input type="submit" value="Submit"><br>
</form><br>
</body>
</html>