<%@ include file="/common/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="menu" content="ContentMenu" />
<link href='/template/jquery-ui-1.12.1/jquery-ui.css' rel='stylesheet' type='text/css'>
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

	<form action="save" method="post">
		<input type="hidden" name="tpl" value="${page.tpl}"> <input
			type="hidden" name="currentTemplate" value="${currentTemplate}">
		<table width="90%" class="data" style="margin-bottom:10px;">
			<tr>
				<td style="text-align:center">文件名</td>
				<td ><input name="filename" value="${page.filename}" class="form-editor"/></td>
			</tr>
			<tr>
				<td style="text-align:center">标题</td>
				<td ><input name="title" value="${page.title}" class="form-editor"/></td>
			</tr>
			<tr>
				<td style="text-align:center">模板</td>
				<td ><select id="selCat" onchange='selectTemplate(this.value)'>
						<c:forEach items="${allTemplate}" var="template">
							<option value="${template}" ${template==page.tpl?'selected':''}>${template}&nbsp;&nbsp;&nbsp;&nbsp;</option>
						</c:forEach>
				</select></td>
			</tr>
		</table>
		<table width="90%" class="data">
			<tr>
				<td width="70%"><iframe id="editframe"
						src="show?tpl=${page.tpl}&ajax=true" scrolling='yes'
						frameborder='0' width='100%' height='600px'
						style='border: 0px solid red;'></iframe></td>
				<td width="35%">
					<div id="edit-pool">《-左边图上点一点</div>
				</td>
			</tr>
		</table>
		<table width="90%" class="data">
			<tr>
				<td width="15%">&nbsp;</td>
				<td width="35%"><input name="Submit" type="submit" value="生成页面" class="button white"/></td>
			</tr>
		</table>
	</form>

	<p>&nbsp;</p>
	<!-- iframe中添加模板,在父页面中显示弹出框 -->
	<div style="display: none;text-align:center;margin:0 auto;" id="add_dialog" class="ui-dialog-content ui-widget-content">
		模板:&nbsp;&nbsp;
		<select id="add_sel">				
				<c:choose>
					<c:when test="${page.tpl eq 'article' || page.tpl eq 'imgs'}">
					<option value="${page.tpl}" selected}>${page.tpl}&nbsp;&nbsp;&nbsp;&nbsp;</option>
					</c:when>
					<c:otherwise>
					<c:forEach items="${allTemplate}" var="template">
						<c:if test="${template!='ueditor'}" >	
						<option value="${template}"${template==page.tpl?'selected':''}>${template}&nbsp;&nbsp;&nbsp;&nbsp;</option>
						</c:if>
					</c:forEach>
					</c:otherwise>
				</c:choose>							
		</select>
	</div>
</body>
<script type="text/javascript" src="/template/scripts/lib/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="/template/scripts/fileupload/upload.js"></script>
<script type="text/javascript" src="/template/scripts/tpl/tpl.js"></script>
<script type="text/javascript" src="/template/scripts/lib/plugins/jquery.cookie.js"></script>
<script type="text/javascript" src="/template/jquery-ui-1.12.1/jquery-ui.js"></script>
</html>

<script type="text/javascript">
	function selectTemplate(value) {
		if("ueditor"==value){
			location.href = "ueditor";
		}
		else
			location.href = "newpage?tpl=" + value;
	}
</script>