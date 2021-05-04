
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    <link rel="stylesheet" type="text/css" href="style.css" media="screen" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Sign document volume</title>
</head>
<body>
<form action="./run/sign.jsp" method="POST">
Index of the volume to sign <br>
<input type="text" name="index"><br>
days of validity <br>
<input type="number" name="daysOfValidity"><br> <br>
<input type="submit" value="Submit"><br>
<br>
</form><br>

<form action="./">
<input type="submit" value="back"><br>
</form><br>


</body>
</html>