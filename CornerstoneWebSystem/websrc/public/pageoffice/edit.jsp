<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>CORNERSTONE文件编辑</title>
    <script type="text/javascript" src="<c:out value="${webUrl}" />pageoffice.js"></script>
    <script type="text/javascript">
        function Save() {
            document.getElementById("PageOfficeCtrl1").WebSave();
            // POBrowser.closeWindow();
            alert("保存成功")
            // window.parent.window.onSaveCompeleted();
            // window.top.onSaveCompeleted();
        }
    </script>
</head>
<body>
    <div style="width:auto;height:700px;" >
        <c:out value="${pageoffice}" escapeXml="false"/>
    </div>
</body>
</html>
