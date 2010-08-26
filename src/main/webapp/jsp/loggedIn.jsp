<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome to KeithBot</title>
</head>
<body>
	<h4>You successfully logged in</h4>
	<form action="bot" method="post">
		<input type="hidden" name="action" value="logout" />
		<input type="submit" value="Shutdown" />
	</form>
</body>
</html>