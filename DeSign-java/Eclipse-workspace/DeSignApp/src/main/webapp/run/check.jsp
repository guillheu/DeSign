<%@page import="core.DeSignAppLauncher"%>
<%@page import="org.web3j.tuples.generated.Tuple3"%>
<%@page import="java.math.BigInteger"%>

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
String result = "";
if(DeSignAppLauncher.checkSignature(index)){
	result = "Signature matched documents!<br><br>";
	Tuple3<byte[], BigInteger, String> r = DeSignAppLauncher.getIndexInfo(index);
	result += "Signed by : " + r.component3() + "<br>";
	result += "Valid for : " + r.component2().floatValue()/86400 + " days<br>";
}
%>
<meta charset="UTF-8">
<title>signature check</title>
</head>
<body>

<%= result %><br>
<form action="../">
<input type="submit" value="back"><br>
</form><br>
</body>
</html>