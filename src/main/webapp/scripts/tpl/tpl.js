$(document).ready(function() {

});

function showEdit(data) {
	$("#edit-pool").html(data);
	var component = $("#edit-pool").find(".oc-component");
	var btnSave = $("#edit-pool").find(".component-save");
	var valueObjs = $("#edit-pool").find(".component-value");
	var componentName = $(component).attr("name");
	if (null != btnSave && btnSave.length > 0) {
		$(btnSave[0]).click(function() {
			var values = $.cookie("component-vals") || "{}";
			values = JSON.parse(values); 
			var oldVal = values[componentName];
			var newVal = {};
			for (var i = 0; i < valueObjs.length; i++) {
				var key = $(valueObjs[i]).attr("name");
				var val = $(valueObjs[i]).val();
				newVal[key] = val;
			}

			values[componentName] = newVal;
			$.cookie("component-vals", JSON.stringify(values), {path : '/'} );
			// reload iframe
			$('#editframe').attr('src', $('#editframe').attr('src'));
		});
	}
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