<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<c:set var="pagingurl" value="${pageWrap.requestUrl}"/>
<c:set var="nexturl" value='${pagingurl}${"&cp="}${pageWrap.currentPage + 1}'/>
<c:set var="preurl" value='${pagingurl}${"&cp="}${pageWrap.currentPage - 1}'/>
<c:set var="totalpage" value="${pageWrap.totalPage}"/>
<c:if test="${totalpage>1}">
	<form action="${pagingurl}" method="get" onsubmit="return changeCurrentPage();">
<p class="tp nob"> 
	<c:if test="${pageWrap.currentPage>1}"><a href="${pagingurl}">首页</a></c:if>
	<c:if test="${pageWrap.currentPage<=1}">首页</c:if>
	<c:if test="${pageWrap.currentPage>1}"><a href="${preurl}">上页</a></c:if>
	<c:if test="${pageWrap.currentPage<=1}">上页</c:if>
	<c:if test="${pageWrap.currentPage<pageWrap.totalPage}"><a href="${nexturl}">下页</a></c:if>
	<c:if test="${pageWrap.currentPage>=pageWrap.totalPage}">下页</c:if>
	<input type="text" name="cp" id="cp" value="" size="2" />
	<input name="按钮" type="button" value="跳页" onclick="changeCurrentPage()"/>
页数:${pageWrap.currentPage}/${pageWrap.totalPage}&nbsp;&nbsp;共${pageWrap.totalCount}"条</p>
</form>
</c:if>
<script language="javascript">
function checkPaginationform(){
	var cp=	document.getElementById('cp');
	var tp="${pageWrap.totalPage}";
	if(cp.value>parseInt(tp))
	{
		cp.value=tp;
	}
	return true;
}
function changeCurrentPage(){
	checkPaginationform();
	var cp=	document.getElementById('cp');
	var url = "${pagingurl}&cp=" +cp.value;
	document.location=url;
	return false;
}
</script>
