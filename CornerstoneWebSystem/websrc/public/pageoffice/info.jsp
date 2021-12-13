<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="renderer" content="webkit">
    <title>CORNERSTONE</title>
    <link rel="icon" href="favicon.ico">
    <script type="text/javascript" src="<c:out value="${webUrl}" />pageoffice.js"></script>
    <style>

        *,
        *:before,
        *:after {
            box-sizing: border-box;
            -moz-box-sizing: border-box;
            -webkit-box-sizing: border-box;
        }

        body {
            -moz-osx-font-smoothing: grayscale;
            -webkit-font-smoothing: antialiased;
            text-rendering: optimizeLegibility;
            font-family: Arial, -apple-system, system-ui, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen-Sans, Ubuntu, Cantarell, 'Helvetica Neue', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol', sans-serif;
            font-size: 14px;
            font-weight: 400;
            color: #333;
            user-select: none;
        }

        html, body {
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

        .tips {
            padding: 100px 20px;
            text-align: center;
        }

        .tips a {
            color: #3064f5;
            text-decoration: none;
        }

        .tips a:hover {
            text-decoration: underline;
        }

    </style>
</head>
<body>
<div class="tips">
    加载完成后会自动打开，您可以<a href="javascript:__openWindowModeless__()">点击这里</a>立即打开或再次打开
</div>
</body>
<script>
    window.__afterDocumentOpened__ = function () {
        console.log("__afterDocumentOpened__ invoke");
        window.__invokeParentFunc__("onPageOfficeOpenWindowModeless");
    };
    window.__invokeParentFunc__ = function (windowFuncName) {
        if (!window.parent || !windowFuncName) {
            return;
        }
        var func = window.parent.window[windowFuncName];
        console.log(func)
        if (!func || (typeof func !== 'function')) {
            return;
        }
        func();
    };
    window.__openWindowModeless__ = function () {
        POBrowser.openWindowModeless('/p/po/action?fileId=<c:out value="${fileId}"/>&name=<c:out value="${name}" />');

    };
    (function () {
        window.__openWindowModeless__();
    })();
</script>
</html>
