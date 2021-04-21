<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="./signOnChain.jsp">
    <input type="submit" value="sign a stored index onto the blockchain" /><br>
</form>
<form action="./checkSignatureAgainstLocalStorage.jsp">
    <input type="submit" value="Check a signature between the local storage and the blockchain" /><br>
</form>
<form action="./exportSignatureProof.jsp">
    <input type="submit" value="export a signature proof" /><br>
</form>
<form action="./importDocument.jsp">
    <input type="submit" value="Import and index a document into the database" /><br>
</form>

</body>
</html>