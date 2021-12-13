var serverAddr = [window.location.protocol, '//', window.location.host].join('');
//
function setCookie(name, value) {
	var expireMinutes = 60 * 24 * 30;
	var exdate = new Date(new Date().getTime() + expireMinutes * 60 * 1000)
	document.cookie = name + "=" + escape(value) + ";expires=" + exdate.toGMTString();
}

function getUuId() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}

function getCookie(name) {
	var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
	if (arr = document.cookie.match(reg)) {
		return unescape(arr[2]);
	} else {
		return null;
	}
}
//
function deleteCookie(name) {
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval = getCookie(name);
	if (cval != null) {
		document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
	}
}
//
function getSearchObject(url) {
	var urlElement = document.createElement("a");
	urlElement.href = url;
	var search = urlElement.search.substring(1);
	if (search != null && search != "") {
		try {
			return JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g, '":"') + '"}')
		} catch (e) {
			return {};
		}
	} else {
		return {};
	}
}
