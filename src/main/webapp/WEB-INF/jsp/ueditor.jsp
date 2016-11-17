<%@ include file="/common/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="menu" content="ContentMenu" />
   <script type="text/javascript" charset="utf-8" src="ueditor/ueditor.config.js"></script>
   <script type="text/javascript" charset="utf-8" src="ueditor/ueditor.all.min.js"> </script>
   <!--建议手动加在语言，避免在ie下有时因为加载语言失败导致编辑器加载失败-->
   <!--这里加载的语言文件会覆盖你在配置项目里添加的语言类型，比如你在配置项目里配置的是英文，这里加载的中文，那最后就是中文-->
   <script type="text/javascript" charset="utf-8" src="ueditor/lang/zh-cn/zh-cn.js"></script>
   <link href="/template/styles/tpl.css" rel='stylesheet' type="text/css">
<title>编辑推荐首页</title>
</head>
<body>

	<c:if test="${not empty status.errorMessages}">
		<div class="alert alert-error fade in">
			<a href="#" data-dismiss="alert" class="close">&times;</a>
			<c:forEach var="error" items="${status.errorMessages}">
				<c:out value="${error}" escapeXml="false" />
				<br/>
			</c:forEach>
		</div>
	</c:if>

	<c:if test="${not empty url}">
		<div class="alert fade in">发布成功：${url}</div>
	</c:if>

	<form action="saveUeditor" method="post" id="formID">
		<input type="hidden" id="content" name="content">
		<table width="90%" class="data" style="margin-bottom:10px;">
			<tr>
				<td >文件名</td>
				<td ><input name="filename" value="${page.filename}"class="form-editor" /></td>
			</tr>
			<tr>
				<td >标题</td>
				<td ><input name="title" value="${page.title}"class="form-editor" /></td>
			</tr>
			<tr>
				<td >模板</td>
				<td ><select id="selCat" onchange='selectTemplate(this.value)'>
						<c:forEach items="${allTemplate}" var="template">
							<option value="${template}" ${template==page.tpl?'selected':''}>${template}&nbsp;&nbsp;&nbsp;&nbsp;</option>
						</c:forEach>
				</select></td>
			</tr>
		</table>
		<div class="editor" >
		<script id="editor" name="txtContent" type="text/plain" style="width:100%;height:650px;"></script>
		</div>
		<div  class="data" style="text-align:center;margin:0 auto;margin-top:20px;">
			<input name="Submit" type="submit" value="生成页面" onclick="submitForm()" class="button white"/>
		</div>
	</form>

	<p>&nbsp;</p>
</body>
</html>

<script type="text/javascript">
//在表单提交前,将编辑器完整html内容赋值给input提交后台
var ue = UE.getEditor('editor');
function submitForm(){
	var content=ue.getAllHtml();
	console.log(content);
	document.getElementById("content").value = content;
	document.getElementById("formID").submit();
}
function selectTemplate(value) {
	if("ueditor"==value){		
		location.href = "ueditor";
	}
	else
		location.href = "newpage?tpl=" + value;
}
</script>