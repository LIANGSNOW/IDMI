<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Picture Upload</title>
<style>
body{
	font-family: "Microsoft YaHei";
	background-color: #00868B;
}

	#Picture {
	width: 700px;
	margin-left: auto;
	margin-right: auto;
	}
	
	#Label{
	width: 660px;
    height: 30px;
    font-size: 25px;
    margin-left: auto;
	margin-right: auto;
	padding-bottom: 20px;
	padding-top: 15px;
	color: white}
	#PictureDiv{
	width:360px;
	margin-left: auto;
	margin-right: auto;
	padding-top: 20px
	}
	
	.btn{
	    width:210px;
		background: #1874CD;
		color: white;
		border: 2px solid;
		text-align: center;
		margin: 10px;
		font-weight: 400;
		font-size: 17px;
		padding: 4px;
		
	}
	#Logo{
    width: 300px;
    font-size: 20px;
    margin-left: auto;
    margin-right: auto;
    padding-top: 20px;
	}
	
</style>
	<script>
	function showMsg(){
	  alert("confirm to submit?");	
	
	}
	
</script>
</head>
<body>
	<Div id="Main">
		<div id = "Label">Please upload the building which you want to identify !</div>
		<div id = "Picture"><img src="upload/<s:property value='getImgname()'/>" style = "width: 700px;"></div>
		<div id = "PictureDiv">
		<center>
		<s:form action="UploadAction" method ="POST" enctype="multipart/form-data">
			<s:file name="file" class="btn" width="280px" ></s:file>
	 		<s:submit value=" submit " class="btn" onclick="showMsg()"></s:submit>
		</s:form>
			
		<h2><s:property value='getLocation()'/><h2>
			
        </center>
		</div>
		<div id = "Logo"><img src = "img/logo.png" style = "width: 300px;"></div>
	</Div>
</body>
</html>			