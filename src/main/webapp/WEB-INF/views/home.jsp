<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>Converta seus vídeos em formato .dv para MP4</h1>
	<form:form action="${s:mvcUrl('converter').build()}" method="POST"
		commandName="video" enctype="multipart/form-data">
		<div class="form-group">
			<label>Video:</label>
			<input name="fileToConvert" type="file" class="form-control" accept=".dv">
			<button type="submit" class="btn btn-primary">Converter para MP4</button>		
		</div>
	</form:form>
	<c:if test = "${resultUrl != '' && resultUrl != null}">
		<h1>Resultado: </h1> 
		<a href="${resultUrl }" target="_blank">${resultUrl }</a>
	</c:if>
</body>
</html>