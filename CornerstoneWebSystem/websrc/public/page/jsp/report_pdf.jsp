<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions/local" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8"/>
    <title>
        <c:out value="${info.name}" default="--"/>
    </title>
    <style>
        body {
            color: #333;
            background-color: #fff;
            font-size: 14px;
        }

        table {
            border-spacing: 0;
        }

        div {
            padding-top: 6px;
        }

        div > div {
            padding-top: 0;
        }

        del {
            color: #888;
        }

        fieldset {
            margin-top: 20px;
            border: 1px solid #b2b2b2;
            padding: 15px;
        }

        fieldset legend {
            font-weight: bold;
        }

        .html img {
            max-width: 100%;
            height: auto;
        }

        ul {
            list-style: none;
            padding: 0;
        }

        ul li {
            margin-bottom: 10px;
            border-bottom: 1px solid #eee;
        }

        .content-li >div >table {
            width: 100%;
        }

        .content-li>div>table  th {
            border: 1px solid #cccccc
        }

        .content-li>div>table  td {
            border: 1px solid #cccccc;
            border-top: 0;
            margin-right: -1px;
        }

    </style>
</head>
<body style="font-family:Microsoft YaHei;font-size: 14px;">
<div>
    <h2 style="text-align: center">
        <c:out value="${info.name}" default="--"/>
    </h2>
    <fieldset>
        <legend>基本信息</legend>
        <table style="width: 100%">
            <tr>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">汇报名称：</span>
                </td>
                <td style="padding: 5px">
                    <c:out value="${info.name}" default="未设置"/>
                </td>
                <td style="width: 180px;">
                    <span style="font-size:14px;font-weight:400;">汇报类型：</span>
                </td>
                <td style="padding: 5px">
                    <c:choose>
                        <c:when test="${ info.period == 1 }">
                            <span>日报</span>
                        </c:when>
                        <c:when test="${ info.period == 2 }">
                            <span>周报</span>
                        </c:when>
                        <c:when test="${ info.period == 3 }">
                            <span>月报</span>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">汇报人：</span>
                </td>
                <td style="padding: 5px">
                    <c:out value="${info.submitterName}" default="未设置"/>
                </td>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">审核人：</span>
                </td>
                <td style="padding: 5px">
                    <c:if test="${empty info.auditorList}">
                        未设置
                    </c:if>
                    <c:if test="${ not empty info.auditorList}">
                        <c:forEach var="item" varStatus="status" items="${info.auditorList}">
                            <c:if test="${status.index > 0}">
                                /
                            </c:if>
                            <c:out value="${item.name}"/>(<c:out value="${item.userName}"/>)
                        </c:forEach>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">关联项目：</span>
                </td>
                <td style="padding: 5px">
                    <c:out value="${info.projectName}" default="未设置"/>
                </td>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">状态：</span>
                </td>
                <td style="padding: 5px">
                    <c:choose>
                        <c:when test="${ info.status == 1 }">
                            <span>待汇报</span>
                        </c:when>
                        <c:when test="${ info.status == 2 }">
                            <span>待审核</span>
                        </c:when>
                        <c:when test="${ info.status == 3 }">
                            <span>已审核</span>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">汇报时间：</span>
                </td>
                <td style="padding: 5px">
                    <c:out value="${info.reportTime}"/>
                </td>
                <td style="width: 180px;">
                    <span style="font-size:14px;font-weight:400;">创建时间：</span>
                </td>
                <td style="padding: 5px">
                    ${fn:fmtDate(info.updateTime)}
                </td>
            </tr>
        </table>
        <br> <br>
        <legend>汇报内容</legend>
        <c:if test="${not empty info.reportContentList}">
            <div>
                <c:forEach var="content" items="${info.reportContentList}">
                    <br>
                    <div class="content-li">
                            <span style="font-size:14px;font-weight:600;">
                                <c:if test="${ content.type == 1 }">
                                    <c:out value="${content.title}"/>：
                                </c:if>
                               </span>
                        <br>
                        <div>
                            <c:choose>
                                <c:when test="${ content.type == 1 }">
                                    ${content.content}
                                </c:when>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>
        <legend>回复内容</legend>
        <c:if test="${not empty info.reportContentList}">
            <c:forEach var="content" items="${info.reportContentList}">
                <div class="content-li1">
                            <span style="font-size:14px;font-weight:400;">
                                <c:if test="${ content.type == 2 }">
                                    <c:out value="${content.createAccountName}"/>  &nbsp;&nbsp;  ${fn:fmtDate(content.createTime)}：
                                </c:if>
                               </span>
                    <span>
                            <c:choose>
                                <c:when test="${ content.type == 2 }">
                                    ${content.content}
                                </c:when>
                            </c:choose>
                        </span>
                </div>
            </c:forEach>
            </ul>
        </c:if>
    </fieldset>
</div>
</body>
</html>
