<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions/local" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8"/>
    <title>
        <c:out value="${info.task.name}" default="--"/>
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
        td{
            margin-right: -1px;
            margin-top: -1px;
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

    </style>
</head>
<body style="font-family:Microsoft YaHei;font-size: 14px;">
<div>
    <h2 style="text-align: center">
        <c:out value="${info.task.name}" default="--"/>
    </h2>
    <fieldset>
        <legend>基本信息</legend>
        <table style="width: 100%">
            <tr>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">责任人：</span>
                </td>
                <td style="padding: 5px">
                    <c:if test="${empty info.task.ownerAccountList}">
                        未设置
                    </c:if>
                    <c:if test="${ not empty info.task.ownerAccountList }">
                        <c:forEach var="item" varStatus="status" items="${info.task.ownerAccountList}">
                            <c:if test="${status.index > 0}">
                                /
                            </c:if>
                            <c:out value="${item.name}"/>(<c:out value="${item.userName}"/>)
                        </c:forEach>
                    </c:if>
                </td>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">状态：</span>
                </td>
                <td style="padding: 5px">
                    <c:out value="${info.task.statusName}" default="未设置"/>
                </td>
            </tr>
            <tr>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">优先级：</span>
                </td>
                <td style="padding: 5px">
                    <c:out value="${info.task.priorityName}" default="未设置"/>
                </td>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">分类：</span>
                </td>
                <td style="padding: 5px">
                    <c:out value="${categories}" default="未设置"/>
                </td>
            </tr>
            <tr>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">当前迭代：</span>
                </td>
                <td style="padding: 5px">
                    <c:out value="${info.task.iterationName}" default="未设置"/>
                </td>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">创建：</span>
                </td>
                <td style="padding: 5px">
                    <c:out value="${info.task.createAccountName}" default="未设置"/>
                    &nbsp;
                    ${fn:fmtDate(info.task.createTime)}
                    <%--                    <fmt:formatDate value="${info.task.createTime}" pattern="yyyy-MM-dd HH:mm"/>--%>
                </td>
            </tr>
            <tr>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">开始时间：</span>
                </td>
                <td style="padding: 5px">
                    ${fn:fmtDate(info.task.startDate)}
                    <%--                    <fmt:formatDate value="${info.task.startDate}" pattern="yyyy-MM-dd HH:mm"/>--%>
                </td>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">截止时间：</span>
                </td>
                <td style="padding: 5px">
                    ${fn:fmtDate(info.task.endDate)}
                    <%--                    <fmt:formatDate value="${info.task.endDate}" pattern="yyyy-MM-dd HH:mm"/>--%>
                </td>
            </tr>
            <tr>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">预计工时：</span>
                </td>
                <td style="padding: 5px">
                    <c:out value="${info.task.expectWorkTime}" default="0"/> h
                </td>
                <td style="width: 100px;">
                    <span style="font-size:14px;font-weight:400;">更新时间：</span>
                </td>
                <td style="padding: 5px">
                    ${fn:fmtDate(info.task.updateTime)}
                    <%--                    <fmt:formatDate value="${info.task.updateTime}" pattern="yyyy-MM-dd HH:mm"/>--%>
                </td>
            </tr>
        </table>
        <c:if test="${not empty customFields}">
            <table>
                <c:forEach var="customField" items="${customFields}">
                    <tr>
                        <td style="width: 180px;">
                            <span style="font-size:14px;font-weight:400;"><c:out
                                value="${customField.fieldName}"/>：</span>
                        </td>
                        <td style="padding: 5px">
                            <c:choose>
                                <c:when test="${ customField.fieldType == 1 }">
                                    <c:out value="${customField.fileValue}"/>
                                </c:when>
                                <c:when test="${ customField.fieldType == 3 }">
                                    <c:out value="${customField.fileValue}"/>
                                </c:when>
                                <c:when test="${ customField.fieldType == 4 }">
                                    <c:out value="${customField.fileValue}"/>
                                </c:when>
                                <c:when test="${ customField.fieldType == 6 }">
                                    <c:out value="${customField.fileValue}"/>
                                </c:when>
                                <c:when test="${ customField.fieldType == 7 }">
                                    ${fn:fmtDate(customField.fileValue)}
                                    <%--                                    <fmt:formatDate value="${customField.fileValue}" pattern="yyyy-MM-dd HH:mm"/>--%>
                                </c:when>
                                <c:when test="${ customField.fieldType == 8 }">
                                    <c:out value="${customField.fileValue}"/>
                                </c:when>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </fieldset>
    <c:if test="${not empty info.task.content}">
        <fieldset>
            <legend>详细描述</legend>
            <div class="html">
                <c:out value="${info.task.content}" escapeXml="false"/>
            </div>
        </fieldset>
    </c:if>
    <c:if test="${not empty info.task.subTaskList}">
        <fieldset>
            <legend>子对象</legend>
            <ul>
                <c:forEach var="item" items="${info.task.subTaskList}">
                    <li style="font-size:14px;font-weight:400;">
                        <span>子<c:out value="${item.objectTypeName}" default="对象"></c:out></span>
                        <span style="margin-left: 5px;color:${item.statusColor}"><c:out
                            value="${item.statusName}"></c:out></span>
                        <span style="margin-left: 5px;">
                            <c:out value="${item.name}" escapeXml="false"/>
                        </span>
                        <span style="float:right;">
                            <c:out
                                value="${item.createAccountName}"/> &nbsp;&nbsp; ${fn:fmtDate(item.createTime)}</span>&nbsp;
                    </li>
                </c:forEach>
            </ul>
        </fieldset>
    </c:if>
    <c:if test="${not empty info.task.associatedList}">
        <fieldset>
            <legend>关联对象</legend>
            <ul>
                <c:forEach var="item" items="${info.task.associatedList}">
                    <li style="font-size:14px;font-weight:400;">
                        <span>
                        <c:if test="item.associatedType==2">后置</c:if>
                        <c:if test="item.associatedType==1">前置</c:if>
                            <c:out value="${item.objectTypeName}"></c:out>
                            </span>
                        <span style="margin-left: 5px;color:${item.statusColor}"><c:out
                            value="${item.statusName}"></c:out></span>
                        <span style="margin-left: 5px;">
                            <c:out value="${item.name}" escapeXml="false"/>
                        </span>
                        <span style="float:right;">
                            <c:out value="${item.createAccountName}"/>${fn:fmtDate(item.createTime)}</span>&nbsp;
                    </li>
                </c:forEach>
            </ul>
        </fieldset>
    </c:if>
    <c:if test="${not empty info.commentList}">
        <fieldset>
            <legend>评论</legend>
            <ul>
                <c:forEach var="item" items="${info.commentList}">
                    <li>
                        <span style="font-size:14px;font-weight:400;">
                            <c:out value="${item.createAccountName}"/></span>&nbsp;
                            ${fn:fmtDate(item.createTime)}
                            <%--                            <fmt:formatDate--%>
                            <%--                                value="${item.createTime}" pattern="yyyy-MM-dd HH:mm"/>--%>
                        <blockquote>
                            <c:out value="${item.comment}" escapeXml="false"/>
                        </blockquote>
                    </li>
                </c:forEach>
            </ul>
        </fieldset>
    </c:if>
    <c:if test="${not empty info.task.statusChangeLogList}">
        <fieldset>
            <legend>状态变更</legend>
            <br>
            <table style="width: 100%" border="1" cellspacing="0">
                <thead >
                <tr>
                    <th rowspan="2">状态/停留时间</th>
                    <th style="text-align: center;" colspan="3">详细记录</th>
                </tr>
                <tr>
                    <th>时间段</th>
                    <th>状态变更</th>
                    <th>责任人</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${info.task.statusChangeLogList}">
                    <tr>
                        <td><span style="margin-left: 5px;color:${item.statusColor}"> <c:out value="${item.statusName}"
                                                                                             default="未设置"/></span></td>
                        <td>
                            <span>
                                    ${fn:fmtDate(item.enterTime)}
                                      &nbsp;  ->&nbsp;
                                        <c:if test="null!=item.leaveTime">${fn:fmtDate(item.leaveTime)}</c:if>
                            </span>
                        </td>
                        <td>
                            <c:out value="${item.oldStatusName}"
                                   default="未设置"/>  &nbsp;  ->&nbsp;

                            <c:out value="${item.statusName}" default="未设置"/>
                        </td>
                        <td>
                            <c:if test="${empty item.beforeOwnerList}">未设置</c:if>
                            <c:if test="${not empty item.beforeOwnerList}">
                                <c:forEach var="owner" items="${item.beforeOwnerList}">
                                    <c:out value="${owner.name}" default="未设置"/>
                                </c:forEach>
                            </c:if>
                            &nbsp;  ->&nbsp;
                            <c:if test="${empty item.afterOwnerList}">未设置</c:if>
                            <c:if test="${not empty item.afterOwnerList}">
                                <c:forEach var="owner" items="${item.afterOwnerList}">
                                    <c:out value="${owner.name}" default="未设置"/>
                                </c:forEach>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

        </fieldset>
    </c:if>
    <c:if test="${not empty info.changeLogList}">
        <fieldset>
            <legend>变更记录</legend>
            <ul>
                <c:forEach var="item" items="${info.changeLogList}">
                    <li>
                        <span style="font-size:14px;font-weight:400;"><c:out value="${item.createAccountName}"/></span>&nbsp;
                            ${fn:fmtDate(item.createTime)}
                            <%--                        <fmt:formatDate--%>
                            <%--                            value="${item.createTime}" pattern="yyyy-MM-dd HH:mm"/>--%>
                        <blockquote>
                            <div style="font-size: 12px;">
                                <c:choose>
                                    <c:when test="${item.type == 1}">
                                        <span style="color:#999;">创建了对象</span>
                                    </c:when>
                                    <c:when test="${item.type == 2}">
                                        <div style="color:#999;">编辑属性</div>
                                        <c:if test="${not empty item.itemsInfo && not empty item.itemsInfo.itemList}">
                                            <table
                                                style="border-left:4px solid #ccc;padding-left: 10px;margin-top: 10px;width: 100%">
                                                <c:forEach var="itemInfo" items="${item.itemsInfo.itemList}"
                                                           varStatus="itemInfoStatus">
                                                    <tr>
                                                        <td style="width: 100px;">${itemInfo.name}</td>
                                                        <td>
                                                    <span style="font-size: 12px;padding-left: 20px;">
                                                    <del>
                                                        <c:if test="${empty itemInfo.beforeContent}">
                                                            未设置
                                                        </c:if>
                                                        <c:if test="${not empty itemInfo.beforeContent}">
                                                            <c:out value="${itemInfo.beforeContent}"/>
                                                        </c:if>
                                                    </del>
                                                    </span>
                                                            <span style="font-size: 12px;">
                                                                &nbsp;&nbsp;→&nbsp;&nbsp;
                                                            </span>
                                                            <span style="font-size: 12px;">
                                                        <c:out value="${itemInfo.afterContent}" default="未设置"/>
                                                    </span>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </table>
                                        </c:if>
                                    </c:when>
                                    <c:when test="${item.type == 3}">
                                        <span style="color: #999">上传附件</span>
                                        <c:if test="${not empty item.itemsInfo && not empty item.itemsInfo.attachment}">
                                            <span style="padding-left: 20px;"><c:out
                                                value="${item.itemsInfo.attachment.name}"/></span>
                                        </c:if>
                                    </c:when>
                                    <c:when test="${item.type == 4}">
                                        <span style="color: #999">删除附件</span>
                                        <c:if test="${not empty item.itemsInfo && not empty item.itemsInfo.attachment}">
                                            <del>
                                                <span style="padding-left: 20px;"><c:out
                                                    value="${item.itemsInfo.attachment.name}"/></span>
                                            </del>
                                        </c:if>
                                    </c:when>
                                    <c:when
                                        test="${item.type == 5 || item.type == 6 || item.type == 7 || item.type == 8}">
                                        <div>
                                            <c:if test="${item.type == 5}">
                                                <span style="color: #999">新增关联对象</span>
                                            </c:if>
                                            <c:if test="${item.type == 6}">
                                                <span style="color: #999">取消关联对象</span>
                                            </c:if>
                                            <c:if test="${item.type == 7}">
                                                <span style="color: #999">新增子对象</span>
                                            </c:if>
                                            <c:if test="${item.type == 8}">
                                                <span style="color: #999">删除子对象</span>
                                            </c:if>
                                            <c:if test="${not empty item.itemsInfo && not empty item.itemsInfo.task}">
                                                <span style="padding-left: 20px;">
                                                    <c:if test="${item.itemsInfo.task.isDelete}">
                                                        <del>
                                                            <c:out value="${item.itemsInfo.task.name}"/>
                                                        </del>
                                                    </c:if>
                                                    <c:if test="${!item.itemsInfo.task.isDelete}">
                                                        <c:out value="${item.itemsInfo.task.name}"/>
                                                    </c:if>
                                                </span>
                                            </c:if>
                                        </div>
                                    </c:when>
                                    <c:when test="${item.type == 9}">
                                        <span style="color: #999">修改了详细描述</span>
                                    </c:when>
                                    <c:when test="${item.type == 10}">
                                        <span style="color: #999">新增项目成员</span>
                                    </c:when>
                                    <c:when test="${item.type == 11}">
                                        <span style="color: #999">删除项目成员</span>
                                    </c:when>
                                    <c:when test="${item.type == 12}">
                                        <span style="color: #999">新增任务</span>
                                    </c:when>
                                    <c:when test="${item.type == 13}">
                                        <span style="color: #999">删除任务</span>
                                    </c:when>
                                    <c:when test="${item.type == 14}">
                                        <span style="color: #999">新增迭代</span>
                                    </c:when>
                                    <c:when test="${item.type == 15}">
                                        <span style="color: #999">删除迭代</span>
                                    </c:when>
                                    <c:when test="${item.type == 16}">
                                        <span style="color: #999">新增Release</span>
                                    </c:when>
                                    <c:when test="${item.type == 17}">
                                        <span style="color: #999">删除Release</span>
                                    </c:when>
                                    <c:when test="${item.type == 18}">
                                        <span style="color: #999">新增子系统</span>
                                    </c:when>
                                    <c:when test="${item.type == 19}">
                                        <span style="color: #999">删除子系统</span>
                                    </c:when>
                                    <c:when test="${item.type == 20}">
                                        <div style="color: #999">提交了代码</div>
                                        <c:if
                                            test="${not empty item.itemsInfo && not empty item.itemsInfo.scmCommitLog}">
                                            <pre><c:out value="${item.itemsInfo.scmCommitLog.comment}"/></pre>
                                            <pre><c:out value="${item.itemsInfo.scmCommitLog.changed}"/></pre>
                                        </c:if>
                                    </c:when>
                                    <c:when test="${item.type == 21}">
                                        <span style="color: #999">恢复任务</span>
                                    </c:when>
                                    <c:when test="${item.type == 22}">
                                        <span style="color: #999">恢复Release</span>
                                    </c:when>
                                    <c:when test="${item.type == 23}">
                                        <span style="color: #999">恢复子系统</span>
                                    </c:when>
                                    <c:when test="${item.type == 101}">
                                        <span style="color: #999">新增知识库</span>
                                    </c:when>
                                    <c:when test="${item.type == 102}">
                                        <span style="color: #999">删除知识库</span>
                                    </c:when>
                                    <c:when test="${item.type == 103}">
                                        <span style="color: #999">恢复知识库</span>
                                    </c:when>
                                    <c:when test="${item.type == 120}">
                                        <span style="color: #999">新增WIKIPAGE</span>
                                    </c:when>
                                    <c:when test="${item.type == 121}">
                                        <span style="color: #999">删除WIKIPAGE</span>
                                    </c:when>
                                    <c:when test="${item.type == 123}">
                                        <span style="color: #999">恢复WIKIPAGE</span>
                                    </c:when>
                                    <c:when test="${item.type == 131}">
                                        <span style="color: #999">新增主机</span>
                                    </c:when>
                                    <c:when test="${item.type == 132}">
                                        <span style="color: #999">删除主机</span>
                                    </c:when>
                                    <c:when test="${item.type == 133}">
                                        <span style="color: #999">恢复主机</span>
                                    </c:when>
                                    <c:when test="${item.type == 141}">
                                        <span style="color: #999">归档项目</span>
                                    </c:when>
                                    <c:when test="${item.type == 142}">
                                        <span style="color: #999">删除项目</span>
                                    </c:when>
                                    <c:when test="${item.type == 143}">
                                        <span style="color: #999">重新打开项目</span>
                                    </c:when>
                                    <c:when test="${item.type == 150}">
                                        <span style="color: #999">登录主机</span>
                                    </c:when>
                                    <c:when test="${item.type == 160}">
                                        <span style="color: #999">执行PIPELINE</span>
                                    </c:when>
                                    <c:when test="${item.type == 171}">
                                        <span style="color: #999">编辑迭代</span>
                                    </c:when>
                                    <c:when test="${item.type == 180}">
                                        <span style="color: #999">新增文件</span>
                                    </c:when>
                                    <c:when test="${item.type == 181}">
                                        <span style="color: #999">删除文件</span>
                                    </c:when>
                                </c:choose>
                            </div>
                        </blockquote>
                    </li>
                </c:forEach>
            </ul>
        </fieldset>
    </c:if>
</div>
</body>
</html>
