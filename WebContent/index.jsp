<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Index</title>
</head>
<body>
This will be where we start.
<s:form action="LoginAction">
       <s:textfield label="username" name="username"/><br>
       <s:password  label="pwd" name="pwd"/><br>
       <s:submit value="login" />
       
</s:form>

<s:form action="UploadAction" method ="POST" enctype="multipart/form-data">


	<s:file name="file" label ="Image File" ></s:file>
	 <td><s:submit value=" submit "></s:submit></td>
</s:form>

</body>
</html>