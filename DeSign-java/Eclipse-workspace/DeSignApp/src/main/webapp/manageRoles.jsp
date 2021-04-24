
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Role management</title>
</head>
<body>
<form action="./run/manageRoles.jsp">
Action to take <br> <br>
Check if the address is signatory <br>
<input type="radio" name="action" value="isSig"><br>
Check if the address is default admin <br>
<input type="radio" name="action" value="isAdmin"><br>
Grant address the signatory role (requires admin role from the caller) <br>
<input type="radio" name="action" value="makeSig"><br>
Revoke signatory role from the address (requires admin role from the caller) <br>
<input type="radio" name="action" value="revokeSig"><br><br>
Address :<br>
<input type="text" name="address" maxlength="66" ><br> <br>
<input type="submit" value="Submit"><br>
<br>
</form><br>

<form action="./">
<input type="submit" value="back"><br>
</form><br>


</body>
</html>