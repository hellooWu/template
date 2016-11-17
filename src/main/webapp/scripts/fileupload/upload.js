$(document).ready(function() {
					// for old version
					$('#postShare').on("click",function() {
										$(this).attr("disabled", true);
										$(this).val("正在上传...");
										uploadFile({
											suffix : 'jpg',
											filename : "shareFile",
											button : "postShare",
											link : "pre_link",
											display : "pre_display",
											url : "picUrl",
											thumbs : "tv_big:632:632;tv_middle:480:480;tv_small:312:312;tel_m:132:132;tel_s:96:96"
										});
									})
					$('#shareFile').on("change", function() {
						var file = this.files[0];
						var name = file.name;
						var size = file.size;
						var type = file.type;
					});

					// for new version
					$('input.postShare').live("click",function() {
										$(this).attr("disabled", true);
										$(this).val("正在上传...");
										uploadFile({
											clickbtn : $(this),
											link : "pre_link",
											display : "pre_display",
											url : "picUrl",
											suffix : 'jpg',
											filename : "shareFile",
											thumbs : "tv_big:632:632;tv_middle:480:480;tv_small:312:312;tel_m:132:132;tel_s:96:96"
										});
									})
					$('input.shareFile').live("change", function() {
						var file = this.files[0];
						var name = file.name;
						var size = file.size;
						var type = file.type;
					});

					if ($("#pickerStart").length > 0
							&& $("#validStart").length > 0) {
						var startTime = $("#validStart").val();
						if (null != startTime && startTime != "") {
							$("#pickerStart").val(
									formatTime(new Number(startTime)));
						}
					}
					if ($("#pickerEnd").length > 0 && $("#validEnd").length > 0) {
						var endTime = $("#validEnd").val();
						if (null != endTime && endTime != "") {
							$("#pickerEnd")
									.val(formatTime(new Number(endTime)));
						}
					}

					if ($(".sortUpdate").length > 0) {
						$(".sortUpdate").click(
								function() {
									var ids = $(this).attr("id").split("_");
									var contentId = ids[0];
									var sortIndex = $(
											"#" + contentId + "_index").val();

									var params = {
										method : "sortUpdate",
										id : contentId,
										sortIndex : sortIndex
									};
									updateContentItem(params);
								});
					}

					if ($(".pushUpdate").length > 0) {
						$(".pushUpdate").click(function() {
							var ids = $(this).attr("id").split("_");
							var updateUrl = $(this).attr("update");
							var contentId = ids[0];
							var type = ids[2];

							var params = {
								method : "Push",
								id : contentId,
								type : type
							};
							updateContentItem(updateUrl, params);
						});
					}
					if ($(".unpushUpdate").length > 0) {
						$(".unpushUpdate").click(function() {
							var ids = $(this).attr("id").split("_");
							var updateUrl = $(this).attr("update");
							var contentId = ids[0];
							var type = ids[2];

							var params = {
								method : "UnPush",
								id : contentId,
								type : type
							};
							updateContentItem(updateUrl, params);
						});
					}

					if ($("#linkType").length > 0
							&& $("#linkParams").length > 0) {
						$("#linkType").change(function() {
							updateLinkValue();
						});
						$("#linkParams").change(function() {
							updateLinkValue();
						});
					}
				});

function uploadFile(params) {
	var parent = this;
	var clickbtn = params.clickbtn || $("#" + params.button);
	var parentdiv = clickbtn.parent();
	var display = (parentdiv.find("img." + params.display).length > 0) ? $(parentdiv.find("img." + params.display)): $("#" + params.display);
	var link = (parentdiv.find("a." + params.link).length > 0) ? $(parentdiv.find("a." + params.link)) : $("#" + params.link);
	var url = (parentdiv.find("input." + params.url).length > 0) ? $(parentdiv.find("input." + params.url)) : $("#" + params.url);
	var fileObj = (parentdiv.find("input." + params.filename).length > 0) ? $(parentdiv.find("input." + params.filename)): $("#" + params.filename);
	// 为了上传文件构建一个formdata(传递二进制流)
	var fd = new FormData($("#fileinfo"));
	fd.append("suffix", params.suffix);
	fd.append("thumbs", params.thumbs);
	fd.append("filename", fileObj.get(0).files[0]);
	$.ajax({
		url : 'uploadImg', // Server script to process data
		type : 'POST',
		dataType : 'json',
		success : function(data) {
			clickbtn.removeAttr('disabled');
			clickbtn.val("成功，换个新的");
			display.attr("src", data.url);
			link.attr("href", data.url);
			url.val(data.url);
		},
		error : function(data) {
			clickbtn.removeAttr('disabled');
			clickbtn.val("上传失败，请重传");
		},
		// Form data
		data : fd,
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

function updateContentItem(url, params) {
	$.ajax({
		type : "POST",
		url : url,
		data : params
	}).success(function(data) {
		alert("操作成功");
		window.location.reload();
	}).error(function() {
		console.log("error");
	});
}