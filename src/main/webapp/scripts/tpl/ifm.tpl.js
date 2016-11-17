$(document).ready(function() {					
					//阻止编辑框a标签响应
					$("a").live("click", function(event) {
						event.preventDefault();
					});
					//编辑器添加 +- 按钮
					addEditorIcon();		
					//点击 - 事件,移除component
					$(".remove").live("click",removeComponent);					
					//点击 + 事件,弹出添加模板界面
					$(".add").live("click",addComponentSelector);
						 
					//点击父页面,取消component的选中效果,input,textarea,select和dialog除外.
					//其中判断dialog,下列不能只为*,前面需指定body
					$("body *", parent.document).on("click", function(e)  {
						if(e.target.tagName=="INPUT"||e.target.tagName=="TEXTAREA"||e.target.tagName=="SELECT"||
								$(this).parent().attr("role")=="dialog") return;
						$(".extraLayer").parent().removeClass("active");
						$(".extraLayer").hide();
					});
					
					if ($('.oc-component').length > 0) {
						$('.oc-component .bro').live("click",showEditor);
						// append plus button
						var btnplus = $("<div class='btn-plus' title=\"尾部插入\" alt=\"尾部插入\">+</div>");						
						btnplus.click(addComponentSelector);
						$('body').append(btnplus);
						}
						/*	function() {
							var parent = $('.oc-component').parent();
							var child = parent.children()[0];
							var newComponent = $(child).clone(true);
							var size = $('.oc-component').length;
							newComponent.attr("name", newComponent.attr("name")+ "-" + size);
							newComponent.removeClass("active");
							newComponent.find(".extraLayer").hide();
							$('.oc-component').parent().append(newComponent);
						}	*/												
					
});

function initInput(params) {
	$.ajax({
		url : 'input?currentTemplate=' + params.currentTemplate + '&cmpType='
				+ params.cmpType + '&cmpName=' + params.cmpName + '&ajax=true', // Server
		// script to process data
		type : 'POST',
		dataType : 'html',
		success : function(data) {
			window.parent.showEdit(data);
		},
		error : function(data) {
			alert("initInput error.");
		},
		processData : false,
		contentType : false
	});
}

function formatTime(time) {
	var t = new Date(time);
	year = t.getFullYear();
	month = t.getMonth() + 1;
	date = t.getDate() < 10 ? '0' + t.getDate() : t.getDate();
	hour = t.getHours() < 10 ? '0' + t.getHours() : t.getHours();
	min = t.getMinutes() < 10 ? '0' + t.getMinutes() : t.getMinutes();
	second = t.getSeconds() < 10 ? '0' + t.getSeconds() : t.getSeconds();

	return year + "-" + month + "-" + date + " " + hour + ":" + min + ":"
			+ second;
}

function updateValidStart() {
	$("#validStart").val(Date.parse($('#pickerStart').val()));
}

function updateValidEnd() {
	$("#validEnd").val(Date.parse($('#pickerEnd').val()));
}

function updateLinkValue() {
	var val = {
		linkType : $("#linkType").val(),
		id : $("#linkParams").val()
	};
	$("#link").val(JSON.stringify(val));
}

function updateContentItem(params) {
	$.ajax({
		type : "POST",
		url : "contentupdate",
		data : params
	}).success(function(data) {
		window.location.reload();
	}).error(function() {
		console.log("error");
	});
}

function showEditor(){
	var type = $(this).parent().attr("type");
	var name = $(this).parent().attr("name");
	var template = $("#currentTemplate").val();
	$(".extraLayer").parent().removeClass("active");
	$(".extraLayer").hide();
	$(this).parent().addClass("active");
	$(this).nextAll().show(300);
	event.stopPropagation();// 阻止冒泡
	initInput({
		currentTemplate : template,
		cmpType : type,
		cmpName : name,
		suffix : 'jpg',
		filename : "shareFile",
		button : "postShare",
		link : "pre_link",
		display : "pre_display",
		url : "picUrl",
		thumbs : "tv_big:632:632;tv_middle:480:480;tv_small:312:312;tel_m:132:132;tel_s:96:96"
	});
}

//添加 + - 图标
function addEditorIcon(){
	$(".oc-component").each(function(){
//		console.log($(this).children());
		if(!$(this).children().hasClass("extraLayer")){//防止重复插入
		$(this).append(
				"<img title=\"删除模板\" alt=\"删除模板\" class=\"remove extraLayer\" src=\"images/delete.png\"/>"+
				"<img title=\"向上添加模板\" alt=\"向上添加模板\" class=\"add extraLayer\" src=\"images/add.png\" />"
		); 
		}
	});	
}
function removeComponent(){
	
		if($(".oc-component").length==1){
			alert("sorry,删不了");
			return;
		}
		$(this).parent().remove();
		//写cookie,效率有些问题
		var comp=$("#wrapper").find(".oc-component");
//		var values = $.cookie("component-vals") || "{}";
		var values = "{}";
		values = JSON.parse(values); 
		for(var i=0;i<comp.length;i++){
			var compName=$(comp[i]).attr("name");
			var valueObjs =$(comp[i]).find(".component-value");								
			var newVal={};
			for(var j = 0; j < valueObjs.length; j++){
				var key = $(valueObjs[j]).attr("name");
				var val = $(valueObjs[j]).attr("value");
				newVal[key] = val;
			}
			values[compName] = newVal;															
		}  
		console.log(JSON.stringify(values));	
		$.cookie("component-vals", JSON.stringify(values), {path : '/'} );

}

function addComponentSelector(){
	var lastCmp=$('.oc-component :last');
	var plus;
	if($(this).is('.btn-plus')){
		plus="downAdd";
		var currentCmp=lastCmp;
		console.log(lastCmp);
	}
	else{
		plus="upAdd";
		var currentCmp=$(this).parent();
		console.log(currentCmp);
	}
		var size=$('.oc-component').length;
		var dialog=parent.$( "#add_dialog" ).dialog({
			modal: true,
			draggable:false,
			resizable:false,
			height: 250,
			width: 350,							
			buttons:
			{							
				 "ok":function (){ 
					 var tpl=parent.$('#add_sel').val();
					 $.ajax({
							type : "get",
							url : "addComponent",
							data : {"tpl":tpl,"size":size},
							contentType: "application/x-www-form-urlencoded; charset=utf-8",
							dataType : 'html'
						}).success(function(data) {
							if(plus=="downAdd"){
								currentCmp.after(data);
							}
							else{
								currentCmp.before(data);
							}
							addEditorIcon();
							//改名字(服务端写了),写cookie,关闭dialog(参见现在的方法)
							
							//写cookie,效率有些问题
							var comp=$("#wrapper").find(".oc-component");
//							var values = $.cookie("component-vals") || "{}";
							var values = "{}";
							values = JSON.parse(values); 
							for(var i=0;i<comp.length;i++){
								var compName=$(comp[i]).attr("name");
								var valueObjs =$(comp[i]).find(".component-value");								
								var newVal={};
								for(var j = 0; j < valueObjs.length; j++){
									var key = $(valueObjs[j]).attr("name");
									var val = $(valueObjs[j]).attr("value");
									newVal[key] = val;
								}
								values[compName] = newVal;															
							}  
							console.log(JSON.stringify(values));	
							$.cookie("component-vals", JSON.stringify(values), {path : '/'} );
							dialog.dialog("close");
						}).error(function() {
							console.log("add error");
						});
				 }
			},
						
		});
}