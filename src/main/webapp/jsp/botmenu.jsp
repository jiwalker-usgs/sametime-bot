<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome to the Internets</title>
</head>
<body>
	<h4>Here are your options:</h4>
	<form action="bot" method="post">
		<input type="hidden" name="action" value="logout" />
		<input type="submit" value="Shutdown" />
	</form>
	<form action="log" method="post">
		<input type="hidden" name="action" value="list" />
		<input type="submit" value="View Logs" />
	</form>
</body>
</html>