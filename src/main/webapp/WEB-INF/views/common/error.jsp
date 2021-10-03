<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title> 에러 페이지 </title>
</head>
<body>
	<h2 align="center"> ${errorMsg} </h2>

	<div align="center">
		<button type="button" onclick="history.back();">이전 페이지로 이동</button>
		<button type="button" onclck="location.href='${ pageContext.servletContext.contextPath }';">메인 페이지로 이동</button>
	</div>
</body>
</html>