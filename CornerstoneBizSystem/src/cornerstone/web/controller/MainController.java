package cornerstone.web.controller;

import com.google.common.base.Strings;
import cornerstone.biz.BizData;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.domain.TaskWorkTimeLog.TaskWorkTimeLogInfo;
import cornerstone.biz.domain.TaskWorkTimeLog.TaskWorkTimeLogQuery;
import cornerstone.biz.poi.TableData;
import cornerstone.biz.poi.TableDataInfo;
import cornerstone.biz.util.ExportBeanToExcelUtil;
import cornerstone.biz.util.StringUtil;
import cornerstone.web.view.KaptchaView;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.mvc.*;
import jazmin.util.JSONUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * main
 *
 * @author cs
 */
@Controller(id = "main", index = true)
public class MainController extends BaseController {
    //
    private static Logger logger = LoggerFactory.get(MainController.class);

    //
    @Service(id = "kaptcha")
    public void kaptcha(Context ctx) {
        String sign = ctx.getString("sign", true);
        KaptchaView kv = new KaptchaView();
        String code = null;
        try {
            code = kv.getCode();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        BizData.bizAction.kaptcha(sign, code);
        ctx.view(kv);
    }

    @Service(id = "send_mobile_code")
    public void sendMobileCode(Context ctx) {
        String mobileNo = ctx.getString("mobileNo", true);
        String sign = ctx.getString("sign", true);
        String kaptchaCode = ctx.getString("kaptchaCode", true);
        String ip = null;
        if (ctx.request() != null) {
            ip = ctx.request().ip();
        }
        try {
            BizData.bizAction.sendMobileCode(mobileNo, ip, sign, kaptchaCode);
            ctx.put("code", 0);
            ctx.put("msg", "OK");
        } catch (Exception e) {
            ctx.put("code", -1);
            ctx.put("msg", e.getMessage());
            logger.error(e.getMessage(), e);
        }
        ctx.view(new JsonView());
    }

    @Service(id = "get_client_ip")
    public void getClientIp(Context ctx) {
        ctx.put("ip", ctx.request().ip());
        logger.info("getClientIp ip:{}", ctx.request().ip());
        ctx.view(new JsonView());
    }

    /**
     * 下载任务导入模板
     *
     * @param ctx
     */
    @Service(id = "download_task_template")
    public void downloadImportTaskExcelTemplate(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        TaskImportTemplateReq req = JSONUtil.fromJson(arg, TaskImportTemplateReq.class);
        TableData data = BizData.bizAction.downloadImportTaskExcelTemplate(req.token, req.projectId, req.objectType);
        ExportBeanToExcelUtil.exportExcel(ctx, "template.xlsx", "sheet1", data.headers, data.contents, false);
        ctx.view(new JsonView());
    }

    @Service(id = "confirm_bind_email", queryCount = 3)
    public void confirmBindEmail(Context ctx) {
        String sign = ctx.request().querys().get(2);
        try {
            BizData.bizAction.confirmBindEmail(sign);
            error(ctx, "绑定成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            error(ctx, e.getMessage());
        }

    }

    @Service(id = "export_task_list")
    public void exportTaskListToExcel(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        TaskExportReq req = JSONUtil.fromJson(arg, TaskExportReq.class);
        ExportData data = BizData.bizAction.exportTaskListToExcel(req.token, req.query, req.fields);
        ExportBeanToExcelUtil.exportExcel(ctx, data.fileName, "sheet1",
                data.tableData.columnWidth, data.tableData.headers, data.tableData.contents, false);
        ctx.view(new JsonView());
    }

    @Service(id = "export_supplier_member")
    public void exportSupplierMemberListToExcel(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        SupplierMemberExportReq req = JSONUtil.fromJson(arg, SupplierMemberExportReq.class);
        ExportData data = BizData.bizAction.exportSupplierMemberListToExcel(req.token, req.query);
        ExportBeanToExcelUtil.exportExcel(ctx, data.fileName, "sheet1",
                data.tableData.columnWidth, data.tableData.headers, data.tableData.contents, false);
        ctx.view(new JsonView());
    }

    @Service(id = "export_company_version_repository")
    public void exportCompanyVersionRepositoryListToExcel(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        CompanyVersionRepositoryExportReq req = JSONUtil.fromJson(arg, CompanyVersionRepositoryExportReq.class);
        ExportData data = BizData.bizAction.exportCompanyVersionRepositoryListToExcel(req.token, req.query);
        ExportBeanToExcelUtil.exportExcel(ctx, data.fileName, "sheet1", data, false);
        ctx.view(new JsonView());
    }

    /**
     * 导出数据表格
     *
     * @param ctx
     */
    @Service(id = "export_data_table")
    public void exportDataTable(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        DataTableExportReq req = JSONUtil.fromJson(arg, DataTableExportReq.class);
        TableDataInfo tableInfo = BizData.dataTableAction.exportToExcel(req.token, req.id, req.query);
        ExportBeanToExcelUtil.exportMoreSheetsExcel(ctx, tableInfo.fileName + ".xlsx", tableInfo.tableDatas, false);
        ctx.view(new JsonView());
    }

    /**
     * 导出数据表格
     *
     * @param ctx
     */
    @Service(id = "export_workflow_instance")
    public void exportWorkflowInstance(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        WorkflowInstanceExportReq req = JSONUtil.fromJson(arg, WorkflowInstanceExportReq.class);
        ExportData data = BizData.workflowAction.exportWorkflowInstancesToExcel(req.token, req.query);
        ExportBeanToExcelUtil.exportExcel(ctx, data.fileName, "sheet1",
                data.tableData.columnWidth, data.tableData.headers, data.tableData.contents, false);
        ctx.view(new JsonView());
    }

    /**
     * 导出成员工时统计报表
     *
     * @param ctx
     */
    @Service(id = "export_account_worktime_list")
    public void exportAccountWorktime(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        TaskWorkTimeLogInfoExportReq req = JSONUtil.fromJson(arg, TaskWorkTimeLogInfoExportReq.class);
        checkTaskWorkTimeLogInfoExportReq(req);
        Map<String, Object> map = BizData.bizAction.getAllTaskWorkTimeLogList(req.token, req.query);
        @SuppressWarnings("unchecked")
        List<TaskWorkTimeLogInfo> list = (List<TaskWorkTimeLogInfo>) map.get("list");
        List<TaskWorkTimeLogMember4Excel> excelDatas = new ArrayList<>();
        for (TaskWorkTimeLogInfo e : list) {
            excelDatas.add(TaskWorkTimeLogMember4Excel.create(e));
        }
        ExportBeanToExcelUtil.exportExcel(ctx, "成员工时报表.xlsx", TaskWorkTimeLogMember4Excel.class, excelDatas, false);
        ctx.view(new JsonView());
    }

    /**
     * 导出项目工时统计报表
     *
     * @param ctx
     */
    @Service(id = "export_project_worktime_list")
    public void exportProjectWorktime(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        TaskWorkTimeLogInfoExportReq req = JSONUtil.fromJson(arg, TaskWorkTimeLogInfoExportReq.class);
        checkTaskWorkTimeLogInfoExportReq(req);
        List<TaskWorkTimeLogInfo4Excel> list = BizData.bizAction.exportProjectWorkTimeLogList(req.token, req.query);
        ExportBeanToExcelUtil.exportExcel(ctx, "项目工时报表.xlsx", TaskWorkTimeLogInfo4Excel.class, list, false);
        ctx.view(new JsonView());
    }

    private void checkTaskWorkTimeLogInfoExportReq(TaskWorkTimeLogInfoExportReq req) {
        if (StringUtil.isEmpty(req.token)) {
            throw new AppException("TOKEN不能为空");
        }
        if (req.query == null) {
            req.query = new TaskWorkTimeLogQuery();
        }
        req.query.pageSize = 10000;//最多返回10000条记录
    }

    /**
     * 导出成员任务报表
     *
     * @param ctx
     */
    @Service(id = "export_account_task_list")
    public void exportAccountTaskList(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        AccountTaskExportReq req = JSONUtil.fromJson(arg, AccountTaskExportReq.class);
        List<TaskInfo> list = BizData.bizAction.getAccountsTaskList(req.token, req.type, req.accountIds);
        List<AccountTask4Excel> excelDatas = new ArrayList<>();
        for (TaskInfo e : list) {
            excelDatas.add(AccountTask4Excel.create(e));
        }
        ExportBeanToExcelUtil.exportExcel(ctx, "成员任务报表.xlsx", AccountTask4Excel.class, excelDatas, false);
        ctx.view(new JsonView());
    }

    /**
     * 导出迭代进度报表
     *
     * @param ctx
     */
    @Service(id = "export_iteration_progress_report")
    public void exportIterationProgressReport(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        ProgressReportExportReq req = JSONUtil.fromJson(arg, ProgressReportExportReq.class);
        List<ProgressReport> excelDatas = BizData.bizAction.getIterationProgressReportList(req.token);
        ExportBeanToExcelUtil.exportExcel(ctx, "迭代进度报表.xlsx", ProgressReport.class, excelDatas, false);
        ctx.view(new JsonView());
    }

    /**
     * 导出项目进度报表
     *
     * @param ctx
     */
    @Service(id = "export_project_progress_report")
    public void exportProjectProgressReport(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        ProgressReportExportReq req = JSONUtil.fromJson(arg, ProgressReportExportReq.class);
        List<ProgressReport> excelDatas = BizData.bizAction.getProjectProgressReportList(req.token, req.query);
        ExportBeanToExcelUtil.exportExcel(ctx, "项目进度报表.xlsx", ProgressReport.class, excelDatas,
                Arrays.asList("iterationName", "strIterationStatus", "startDate", "endDate"),
                false);
        ctx.view(new JsonView());
    }

    /**
     * 导出问卷调查
     *
     * @param ctx
     */
    @Service(id = "export_surveys_instance")
    public void exportSurvleysInstance(Context ctx) {
        String arg = null;
        try {
            arg = ctx.getString("arg", true);
            arg = new String(Base64.getDecoder().decode(arg));
            SurveysInstanceExportReq req = JSONUtil.fromJson(arg, SurveysInstanceExportReq.class);
            ExportData data = BizData.surveysAction.exportSurveysInstancesToExcel(req.token, req.query);
            ExportBeanToExcelUtil.exportExcel(ctx, data.fileName, "sheet1",
                    data.tableData.columnWidth, data.tableData.headers, data.tableData.contents, false);
            ctx.view(new JsonView());
        } catch (Exception e) {
            logger.error("arg:{}", arg);
            logger.error(e.getMessage(), e);
            error(ctx, e.getMessage());
        }
    }

    //
    @Service(id = "ping_biz")
    public void pingBiz(Context ctx) {
        String result = BizData.bizAction.pingBiz();
        ctx.view(new PlainTextView(result));
    }


    @Service(id = "registerNew")
    public void registerNew(Context context) {
        String ip = getIpAddr(context.request().raw());
        RegisterInfo info = JSONUtil.fromJson(context.getString("arg0"), RegisterInfo.class);
        info.ip = ip;
        try {
            String result = BizData.bizAction.registerNew(info);
            context.view(new PlainTextView(JSONUtil.toJson(result)));
        } catch (AppException e) {
            String rsp = "\n" + "AppException,0," + e.getMessage();
            context.view(new PlainTextView(rsp));
        }
    }


    /**
     * 获取系统编译时间
     */
    @Service(id = "buildVersion")
    public void buildVersion(Context context) {
        String jarName = "CornerstoneBizSystem.jar";
        String jazminName = "jazmin.jar";
        JarFile jf;
        String path = System.getProperty("java.class.path");
        logger.info("------------>" + path);
        long manifestTime = 0;
        try {
            jf = new JarFile("../lib/" + jarName);
            ZipEntry manifest = jf.getEntry(JarFile.MANIFEST_NAME);
            manifestTime = manifest.getTime();
        } catch (Exception e) {
            logger.error("e0------>" + e.getMessage());
            String[] splits = path.split(";");
            for (String split : splits) {
                logger.info(split);
                if (split.endsWith("jazmin.jar")) {
                    try {
                        String basePath = split.split(jazminName)[0] + "webapp/CornerstoneWebSystem/webapp/WEB-INF/lib/" + jarName;
                        jf = new JarFile(new File(basePath));
                        ZipEntry manifest = jf.getEntry(JarFile.MANIFEST_NAME);
                        manifestTime = manifest.getTime();
                    } catch (Exception ioException) {
                        logger.error("e1----->" + ioException.getMessage());
                    }
                    break;
                }
            }
        }
        Date d = new Date();
        d.setTime(manifestTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        context.put("biz", sdf.format(d));
        context.view(new JsonView());

    }

    private String getIpAddr(HttpServletRequest request) {
        String UNKNOW = "unknow";
        try {
            String ip = request.getHeader("x-forwarded-for");
            if (Strings.isNullOrEmpty(ip) || (UNKNOW.equalsIgnoreCase(ip))) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if ((ip == null) || (ip.length() == 0) || (UNKNOW.equalsIgnoreCase(ip))) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if ((ip == null) || (ip.length() == 0) || (UNKNOW.equalsIgnoreCase(ip))) {
                ip = request.getRemoteAddr();
            }
            if (ip == null || ip.length() == 0 || UNKNOW.equalsIgnoreCase(ip)) {
                ip = request.getHeader("http_client_ip");
            }
            if (ip == null || ip.length() == 0 || UNKNOW.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }


            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                //根据网卡取本机配置的IP
                try {
                    InetAddress addr = InetAddress.getLocalHost();
                    ip = addr.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error("request ip parse ERR", e);
                }
            }
            if (ip != null && ip.length() > 15) {
                if (ip.indexOf(",") > 0) {
                    ip = ip.substring(0, ip.indexOf(","));
                }
            }
            return ip;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ;
            return "";
        }
    }
}
