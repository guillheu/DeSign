<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<form action="./run/sign.jsp">
Index of the document volume to sign :<br>
<input type="text" name="index"/><br>
Validity time (in days)<br>
<input type="number" name="validityTime"/><br><br>
<input type="submit" name="sign"/><br><br>
</form>
<form action=".">
    <input type="submit" value="back" />
</form>
</body>
</html>