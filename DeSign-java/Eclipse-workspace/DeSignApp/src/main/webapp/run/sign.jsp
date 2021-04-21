<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<% 
String index = request.getParameter("index");
int secondsBeforeExpiration = Integer.parseInt(request.getParameter("validityTime")) * 86400;

/*try {
	coreSQLDB.sign(index, secondsBeforeExpiration);
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}*/


%>
</body>
</html>