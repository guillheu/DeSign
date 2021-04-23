
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Sign document volume</title>
</head>
<body>
<form action="./run/import.jsp" method="post">
Select documents to add to the database<br>
<input type="file" name="documents" multiple><br>
<input type="submit" value="Submit"><br>
<br>
</form><br>

<form action="./">
<input type="submit" value="back"><br>
</form><br>


</body>
</html>