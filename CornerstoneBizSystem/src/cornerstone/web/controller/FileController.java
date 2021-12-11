package cornerstone.web.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import cornerstone.biz.BizData;
import cornerstone.biz.FileServiceManager;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.ChangeLog.ChangeLogInfo;
import cornerstone.biz.domain.ProjectArtifact.ProjectArtifactInfo;
import cornerstone.biz.domain.WikiPage.WikiPageDetailInfo;
import cornerstone.biz.sftp.SftpChannel;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.tool.markdown.MarkDown2HtmlWrapper;
import cornerstone.biz.tool.markdown.MarkdownMarked;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.FileDownloadUtil;
import cornerstone.biz.util.StringUtil;
import cornerstone.web.view.*;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.mvc.*;
import jazmin.util.DumpUtil;
import jazmin.util.FileUtil;
import jazmin.util.IOUtil;
import jazmin.util.JSONUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author cs
 */
@Controller(id = "file")
public class FileController extends BaseController {
    //
    private static Logger logger = LoggerFactory.get(FileController.class);

    //
    //
    private void checkSize(long size) {
        long mSize = Integer.parseInt(GlobalConfig.getValue("system.maxUploadFileSize"));
        long maxSize = mSize * 1024 * 1024;
        if (size > maxSize) {
            logger.error("file size limit size {}>maxSize {}", size, maxSize);
            throw new AppException("文件过大，上传失败。最大可上传" + mSize + "M");
        }
        if (size <= 0) {
            logger.error("file size 0");
            throw new AppException("文件内容不能为空");
        }
    }

    //
    @Service(id = "upload_file", method = HttpMethod.POST)
    public void uploadFile(Context ctx) throws IOException, ServletException {
        HttpServletRequest req = ctx.request().raw();
        String token = ctx.getString("token");
        String reverse = ctx.getString("reverse", false);
        String fileName = "upload_file";
        Part part = req.getPart("file");
        String header = part.getHeader("content-disposition");
        if (!StringUtil.isEmpty(header)) {
            fileName = getFileName(header);
        }
        UploadFileResult result = new UploadFileResult();
        result.reverse = reverse;
        try {
            Attachment bean = new Attachment();
            bean.name = fileName;
            bean.size = part.getSize();
            result.size = bean.size;
            checkSize(bean.size);
            //
            FileInfo info = FileServiceManager.get().upload(part.getInputStream(), bean.name);
            info.fileName = bean.name;
            result.success = true;
            result.file_path = "/p/file/get_file/" + info.fileId;
            result.full_path = GlobalConfig.webUrl + "p/file/get_file/" + info.fileId;
            //
            bean.uuid = info.fileId;
            bean = BizData.bizAction.addAttachment(token, bean);
            result.attachment = bean;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.success = false;
        }
        ctx.view(new PlainTextView(JSONUtil.toJson(result)));
    }


    /**
     * 合并分片上传文件
     *
     * @param ctx
     */
    @Service(id = "merge_chunk_file", method = HttpMethod.POST)
    public void mergeChunkFile(Context ctx) {
        String token = ctx.getString("token");
        String fileName = ctx.getString("filename");
        //file md5
        String identifier = ctx.getString("identifier");
        int totalChunks = ctx.getInteger("totalChunks");
        int chunkNumber = ctx.getInteger("chunkNumber");
        long totalSize = ctx.getInteger("totalSize");
        logger.info("chunk file ,token:{},name:{},md5:{},totalChunks:{},currentChunkNumber:{}", token, fileName, identifier, totalChunks, chunkNumber);

        //合并文件
        UploadFileResult result = new UploadFileResult();
        try {
            Attachment bean = new Attachment();
            bean.name = fileName;
            bean.size = totalSize;
            result.size = bean.size;
            checkSize(bean.size);
            //

            FileInfo info = FileServiceManager.get().mergeChunkUpload(identifier, fileName, totalChunks);
            info.fileName = bean.name;
            result.success = true;
            result.file_path = "/p/file/get_file/" + info.fileId;
            result.full_path = GlobalConfig.webUrl + "p/file/get_file/" + info.fileId;
            //
            bean.uuid = info.fileId;
            bean = BizData.bizAction.addAttachment(token, bean);
            result.attachment = bean;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.success = false;
            result.attachment = null;
        }
        ctx.view(new PlainTextView(JSONUtil.toJson(result)));
    }


    /**
     * 上传分片文件
     *
     * @param ctx
     */
    @Service(id = "upload_chunk_file", method = HttpMethod.POST)
    public void uploadChunkFile(Context ctx) throws IOException, ServletException {
        HttpServletRequest req = ctx.request().raw();
        String token = ctx.getString("token");
        String fileName = ctx.getString("filename");
        //file md5
        String identifier = ctx.getString("identifier");
        int totalChunks = ctx.getInteger("totalChunks");
        int chunkNumber = ctx.getInteger("chunkNumber");
        logger.info("merge chunk file,token:{},name:{},md5:{},totalChunks:{},currentChunkNumber:{}", token, fileName, identifier, totalChunks, chunkNumber);

        Part part = req.getPart("file");
        if (null == part) {
            part = req.getPart("upfile");
        }
        if (totalChunks > 1) {
            //存到暂存区
            FileServiceManager.get().saveChunkFile(part.getInputStream(), identifier, chunkNumber);
            Map<String, Object> result = new HashMap<>();
            result.put("needMerge", chunkNumber == totalChunks);
            result.put("identifier", identifier);
            result.put("fileName", fileName);
            ctx.view(new PlainTextView(JSONUtil.toJson(result)));
        } else {
            UploadFileResult result = new UploadFileResult();
            try {
                Attachment bean = new Attachment();
                bean.name = fileName;
                bean.size = part.getSize();
                result.size = bean.size;
                checkSize(bean.size);
                //
                FileInfo info = FileServiceManager.get().upload(part.getInputStream(), bean.name);
                info.fileName = bean.name;
                result.success = true;
                result.file_path = "/p/file/get_file/" + info.fileId;
                result.full_path = GlobalConfig.webUrl + "p/file/get_file/" + info.fileId;
                //
                bean.uuid = info.fileId;
                bean = BizData.bizAction.addAttachment(token, bean);
                result.attachment = bean;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                result.success = false;
                result.attachment = null;
            }
            ctx.view(new PlainTextView(JSONUtil.toJson(result)));
        }


    }


    /**
     * 上传问卷调查附件
     *
     * @param ctx
     */
    @Service(id = "upload_surveys_file", method = HttpMethod.POST)
    public void uploadSurveysFile(Context ctx) throws IOException, ServletException {
        HttpServletRequest req = ctx.request().raw();
        String token = ctx.getString("token");
        String reverse = ctx.getString("reverse", false);
        String surveysDefineUuid = ctx.getString("surveysDefineUuid", true);
        String fileName = "upload_file";
        Part part = req.getPart("file");
        String header = part.getHeader("content-disposition");
        if (!StringUtil.isEmpty(header)) {
            fileName = getFileName(header);
        }
        UploadFileResult result = new UploadFileResult();
        result.reverse = reverse;
        try {
            Attachment bean = new Attachment();
            bean.name = fileName;
            bean.size = part.getSize();
            result.size = bean.size;
            checkSize(bean.size);
            //
            FileInfo info = FileServiceManager.get().upload(part.getInputStream(), bean.name);
            info.fileName = bean.name;
            result.success = true;
            result.file_path = "/p/file/get_file/" + info.fileId;
            result.full_path = GlobalConfig.webUrl + "p/file/get_file/" + info.fileId;
            //
            bean.uuid = info.fileId;
            bean = BizData.surveysAction.addSurveysAttachment(token, surveysDefineUuid, bean);
            result.attachment = bean;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.success = false;
        }
        ctx.view(new PlainTextView(JSONUtil.toJson(result)));
    }

    @Service(id = "upload", method = HttpMethod.POST)
    public void upload(Context ctx) throws IOException, ServletException {
        HttpServletRequest req = ctx.request().raw();
        String reverse = ctx.getString("reverse", false);
        String fileName = "upload_file";
        Part part = req.getPart("file");
        String header = part.getHeader("content-disposition");
        if (!StringUtil.isEmpty(header)) {
            fileName = getFileName(header);
        }
        UploadFileResult result = new UploadFileResult();
        result.reverse = reverse;
        try {
            Attachment bean = new Attachment();
            bean.name = fileName;
            bean.size = part.getSize();
            result.size = bean.size;
            checkSize(bean.size);
            //
            FileInfo info = FileServiceManager.get().upload(part.getInputStream(), bean.name);
            info.fileName = bean.name;
            result.success = true;
            result.file_path = "/p/file/get_file/" + info.fileId;
            result.full_path = GlobalConfig.webUrl + "p/file/get_file/" + info.fileId;
            //
            bean.uuid = info.fileId;
            result.attachment = bean;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.success = false;
        }
        ctx.view(new PlainTextView(JSONUtil.toJson(result)));
    }

    @Service(id = "upload_temp_file", method = HttpMethod.POST)
    public void uploadTempFile(Context ctx) throws IOException, ServletException {
        HttpServletRequest req = ctx.request().raw();
        String token = ctx.getString("token");
        String reverse = ctx.getString("reverse", false);
        String fileName = "upload_file";
        Part part = req.getPart("file");
        String header = part.getHeader("content-disposition");
        if (!StringUtil.isEmpty(header)) {
            fileName = getFileName(header);
        }
        UploadFileResult result = new UploadFileResult();
        result.reverse = reverse;
        try {
            Attachment bean = new Attachment();
            bean.name = fileName;
            bean.size = part.getSize();
            result.size = bean.size;
            checkSize(bean.size);
            //
            FileInfo info = upload(part.getInputStream(), fileName);
            result.success = true;
            //
            bean.uuid = info.fileId;
            result.attachment = bean;
            bean.uuid = info.fileId;
            bean = BizData.bizAction.addAttachment(token, bean);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.success = false;
        }
        ctx.view(new PlainTextView(JSONUtil.toJson(result)));
    }

    private FileInfo upload(InputStream is, String fileName) {
        FileInfo info = new FileInfo();
        info.fileId = UUID.randomUUID().toString().replaceAll("-", "");
        String fileSuffix = "";
        if (!StringUtil.isEmpty(fileName)) {
            int pos = fileName.lastIndexOf(".");
            if (pos != -1) {
                fileSuffix = fileName.substring(pos).toLowerCase();
            }
        }
        info.fileId += fileSuffix;
        String parent = info.fileId.charAt(0) + "";
        String child = info.fileId.charAt(1) + "";
        File file = null;
        try {
            String filePath = parent + File.separator + child + File.separator + info.fileId;
            file = new File(GlobalConfig.fileServiceHomePath, filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            saveContent(is, file);
            info.fileAbsolutePath = file.getAbsolutePath();
            info.length = file.length();
            return info;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (file != null && file.exists()) {
                file.delete();
            }
            throw new AppException(e.getMessage());
        }
    }

    public void saveContent(InputStream is, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            IOUtil.copy(is, fos);
        }
    }

    /**
     * Content-Disposition: form-data; name="file"; filename="logo.jpg"
     *
     * @param header
     * @return
     */
    public String getFileName(String header) {
        String[] tempArr1 = header.split(";");
        String[] tempArr2 = tempArr1[2].split("=");
        String fileName = tempArr2[1].substring(tempArr2[1].lastIndexOf("\\") + 1).replaceAll("\"", "");
        return fileName;
    }

    //
    @Service(id = "get_file", queryCount = 3, method = HttpMethod.GET)
    public void getFile(Context ctx) {
        String fileId = ctx.request().querys().get(2);
        String token = ctx.getString("token");
        if (token == null) {
            token = ctx.request().cookie("token");
        }
        this.writeFile(ctx, fileId, token);
    }

    @Service(id = "get_system_file", method = HttpMethod.GET)
    public void getSystemFile(Context ctx) {
        String type = ctx.getString("type");
        String fileId ;
        switch (type) {
            case "01":
                fileId = GlobalConfig.getValue("system.logo");
                break;
            default:
                error(ctx, "未知系统文件类型");
                return;
        }
        if (StrUtil.isBlank(fileId)) {
            ctx.view(new ErrorView(404));
            return;
        }
        this.writeFile(ctx, fileId, null);
    }

    /**
     * 写文件
     * @param ctx
     * @param fileId
     * @param token
     */
    private void writeFile(Context ctx, String fileId, String token) {
        Attachment attachment = BizData.bizAction.queryAttachmentByUuid(token, fileId);
        if (attachment != null && attachment.isDelete) {
            ctx.view(new ErrorView(404));
            return;
        }
        File file = FileServiceManager.get().download(fileId);
        String fileName = ctx.getString("name");
        if (!StringUtil.isEmpty(fileName)) {
            ctx.view(new ExFileView(file, fileName));
        } else {
            if (null != attachment && !StringUtil.isEmpty(attachment.name)) {
                ctx.view(new ExFileView(file, attachment.name));
            } else {
                ctx.view(new ExFileView(file));
            }
        }
    }

    //
    @Service(id = "get_file_ex", queryCount = 3, method = HttpMethod.GET)
    public void getFileEx(Context ctx) {
        try {
            String fileId = ctx.request().querys().get(2);
            String token = ctx.getString("token");
            if (token == null) {
                token = ctx.request().cookie("token");
            }
            // 校验下载权限
            BizData.bizAction.checkFileDownloadPermission(token, fileId);
            Attachment attachment = BizData.bizAction.getAttachmentByUuid(token, fileId);
            if (attachment != null && attachment.isDelete) {
                ctx.view(new ErrorView(404));
                return;
            }
            File file = FileServiceManager.get().download(fileId);
            String fileName = ctx.getString("name");
            if (!StringUtil.isEmpty(fileName)) {
                ctx.view(new ExFileView(file, fileName));
            } else {
                if (null != attachment && !StringUtil.isEmpty(attachment.name)) {
                    ctx.view(new ExFileView(file, attachment.name));
                } else {
                    ctx.view(new ExFileView(file));
                }
            }
        }catch (AppException e) {
            logger.error("get_file_ex error:{}", e.getMessage(), e);
            error(ctx, e.getMessage());
            return;
        }
    }

    //
    @Service(id = "download_attachment", queryCount = 3, method = HttpMethod.GET)
    public void downloadAttachment(Context ctx) {
        String fileId = ctx.request().querys().get(2);
        String token = ctx.getString("token");
        if (token == null) {
            token = ctx.request().cookie("token");
        }
        Attachment attachment = BizData.bizAction.getAttachmentByUuid(token, fileId);
        if (attachment == null || attachment.isDelete) {
            ctx.view(new ErrorView(404));
            return;
        }
        File file = FileServiceManager.get().download(fileId);
        String fileName = attachment.name;
        if (!StringUtil.isEmpty(fileName)) {
            ctx.view(new DownloadFileView(file, fileName));
        } else {
            ctx.view(new DownloadFileView(file));
        }
    }

    // 查看PDF版本
    @Service(id = "download_wiki", queryCount = 3)
    public void downloadWiki(Context ctx) {
        String token = ctx.getString("token");
        String uuid = ctx.request().querys().get(2);
        WikiPageDetailInfo info = BizData.bizAction.getWikiPageDetailInfoByUuid(token, uuid);
        String css = null;
        if (info.type == WikiPage.TYPE_markdown) {
            info.content = "<div>" + MarkdownMarked.md2Html(info.content)+ "</div>";
            css = MarkDown2HtmlWrapper.MD_CSS;
        }
        ctx.view(new PdfFileView(info.name + ".pdf", info.content, css));
    }

    private String handlerImage(String content) {
        if (StringUtil.isEmptyWithTrim(content)) {
            return content;
        }
        Document document = Jsoup.parse(content, GlobalConfig.webUrl);
        Element body = document.body();
        Elements elements = body.getElementsByTag("img");
        elements.forEach(element -> {
            element.attr("style", "width:100%;height:auto;");
            String src = element.attr("src");
            if (StringUtil.isEmpty(src)) {
                return;
            }
            if (src.startsWith("http:") || src.startsWith("https:")) {
                return;
            }
            if (src.startsWith("/")) {
                src = GlobalConfig.webUrl.concat(src.substring(1));
            } else {
                src = GlobalConfig.webUrl.concat(src);
            }
            element.attr("src", src);
        });
        return body.html();
    }

    @Service(id = "get_task_info", queryCount = 3)
    public void getTaskInfo(Context ctx) {
        try {
            String token = ctx.getString("token");
            String uuid = ctx.request().querys().get(2);
            TaskPdfInfo info = BizData.bizAction.getTaskPdfInfoByUuid(token, uuid);
            if (!StringUtil.isEmptyWithTrim(info.task.content)) {
                info.task.content = handlerImage(info.task.content);
            }
            for (ChangeLogInfo e : info.changeLogList) {
                e.itemsInfo = BizService.create(e);
            }
            Map<Integer, String> memberMap = new HashMap<>();
            if (info.memberList != null) {
                info.memberList.forEach(member -> {
                    memberMap.put(member.accountId, member.accountName);
                });
            }
            Map<Integer, String> categoryMap = new HashMap<>();
            if (info.categories != null) {
                info.categories.forEach(item -> {
                    categoryMap.put(item.id, item.name);
                });
            }
            StringBuilder categoryBuilder = new StringBuilder();
            if (info.task.categoryIdList != null) {
                for (Integer categoryId : info.task.categoryIdList) {
                    if (!categoryMap.containsKey(categoryId)) {
                        return;
                    }
                    categoryBuilder.append(",").append(categoryMap.get(categoryId));
                }
            }
            if (categoryBuilder.length() > 0) {
                categoryBuilder.deleteCharAt(0);
            }
            List<ProjectCustomFieldBriefInfo> customFields = new ArrayList<>();
            List<ProjectFieldDefine> fieldDefines = info.projectFieldDefineList;
            if (info.task.customFields != null && !info.task.customFields.isEmpty()) {
                info.task.customFields.forEach((fieldId, value) -> {
                    Optional<ProjectFieldDefine> optional = fieldDefines.stream()
                            .filter(define -> fieldId.equals(define.field)).findFirst();
                    if (!optional.isPresent()) {
                        return;
                    }
                    ProjectFieldDefine fieldDefine = optional.get();
                    ProjectCustomFieldBriefInfo briefInfo = new ProjectCustomFieldBriefInfo();
                    briefInfo.fieldId = fieldId;
                    briefInfo.fieldName = fieldDefine.name;
                    briefInfo.fieldType = fieldDefine.type;
                    if (value != null) {
                        if (fieldDefine.type == ProjectFieldDefine.TYPE_复选框) {
                            JSONArray array = (JSONArray) value;
                            List<String> values = array.toJavaList(String.class);
                            briefInfo.fileValue = BizUtil.appendFields(values);
                        } else if (fieldDefine.type == ProjectFieldDefine.TYPE_团队成员选择) {
                            JSONArray array = (JSONArray) value;
                            List<Integer> accountIds = array.toJavaList(Integer.class);
                            List<String> values = new ArrayList<>();
                            accountIds.forEach(accountId -> {
                                if (!memberMap.containsKey(accountId)) {
                                    return;
                                }
                                values.add(memberMap.get(accountId));
                            });
                            briefInfo.fileValue = BizUtil.appendFields(values);
                        } else if (fieldDefine.type == ProjectFieldDefine.TYPE_日期) {
                            briefInfo.fileValue = new Date((Long) value);
                        } else {
                            briefInfo.fileValue = value;
                        }
                    }
                    customFields.add(briefInfo);
                });
            }
            if (info.commentList != null) {
                info.commentList.forEach(item -> item.comment = handlerImage(item.comment));
            }
            if (categoryBuilder.length() > 0) {
                ctx.put("categories", categoryBuilder.toString());
            }
            ctx.put("customFields", customFields);
            ctx.put("info", info);
            ctx.view(new ResourceView("/page/jsp/task_pdf.jsp"));
        } catch (Exception e) {
            logger.error("get_task_info ERR", e);
        }
    }

    // 查看PDF版本
    @Service(id = "download_task_pdf", queryCount = 3)
    public void downloadTaskPdf(Context ctx) {
        String token = ctx.getString("token");
        String uuid = ctx.request().querys().get(2);
        TaskSimpleInfo task = BizData.bizAction.getTaskSimpleInfoByUuid(token, uuid);
        String url = GlobalConfig.pdfUrl + "p/file/get_task_info/" + uuid + "?token=" + token;
        ctx.view(new TaskPdfFileView(task.serialNo + ".pdf", url));
    }


    @Service(id = "get_report_info", queryCount = 3)
    public void getReportInfo(Context ctx) {
        String token = ctx.getString("token");
        int id = Integer.parseInt(ctx.request().querys().get(2));
        Report.ReportInfo info = BizData.bizAction.getReportById(token, id);
        ctx.put("info", info);
        ctx.view(new ResourceView("/page/jsp/report_pdf.jsp"));
    }


    @Service(id = "download_report_pdf", queryCount = 3)
    public void downloadReportPdf(Context ctx) {
        String token = ctx.getString("token");
        int id = Integer.parseInt(ctx.request().querys().get(2));
        Report.ReportInfo report = BizData.bizAction.getReportById(token, id);
        String url = GlobalConfig.pdfUrl + "p/file/get_report_info/" + id + "?token=" + token;
        ctx.view(new TaskPdfFileView("汇报_" + report.id + ".pdf", url));
    }

    @Service(id = "download_artifact", queryCount = 3)
    public void download(Context ctx) {
        String token = ctx.getString("token");
        String uuid = ctx.request().querys().get(2);
        ProjectArtifactInfo info = BizData.bizAction.getProjectArtifactInfoByUuid(token, uuid);
        String fileId = ctx.request().querys().get(2);
        File file = BizUtil.getArtifactFile(fileId);
        String fileName = info.originalName;
        if (StringUtil.isEmpty(fileName)) {
            fileName = info.name;
        }
        if (!StringUtil.isEmpty(fileName)) {
            ctx.view(new ExFileView(file, fileName));
        } else {
            ctx.view(new ExFileView(file));
        }
    }

    /**
     *
     */
    @Service(id = "download_file", queryCount = 3, method = HttpMethod.GET)
    public void downloadFile(Context ctx) {
        try {
            if (ctx.request().querys().size() < 3) {
                error(ctx, "参数错误");
                return;
            }
            String fileId = ctx.request().querys().get(2);
            String token = ctx.getString("token");
            cornerstone.biz.domain.File bizFile = BizData.bizAction.getFileByUuid(token, fileId);
            if (null != bizFile && bizFile.isDirectory) {// 如果是目录特殊处理
                downloadFileList0(ctx, fileId);
                return;
            }
            BizData.bizAction.checkFileDownloadPermission(token, fileId);
            File file = FileServiceManager.get().download(fileId);
            if (null != bizFile && !StringUtil.isEmpty(bizFile.name)) {
                ctx.view(new DownloadFileView(file, bizFile.name));
            } else {
                //Attachment查找文件名
                Attachment attachment = BizData.bizAction.getAttachmentByUuid(token, fileId);
                if (null != attachment && !StringUtil.isEmpty(attachment.name)) {
                    ctx.view(new DownloadFileView(file, attachment.name));
                } else {
                    ctx.view(new DownloadFileView(file));
                }
            }
        }catch (AppException e) {
            logger.error("download_file error:{}", e.getMessage(), e);
            error(ctx, e.getMessage());
            return;
        }

    }

    /**
     * 批量下载文件
     *
     * @param ctx
     * @throws IOException
     */
    @Service(id = "download_file_list", method = HttpMethod.GET)
    public void downloadFileList(Context ctx) throws IOException {
        try {
            if (ctx.request().querys().size() < 3) {
                error(ctx, "参数错误");
                return;
            }
            String token = ctx.getString("token");
            if (StrUtil.isBlank(token)) {
                error(ctx, "TOKEN不能为空");
                return;
            }

            String uuidList = ctx.request().querys().get(2);
            if (StrUtil.isBlank(uuidList)) {
                error(ctx, "文件ID不能为空");
                return;
            }
            String[] uuids = uuidList.split("_");
            if (ArrayUtil.isEmpty(uuids)) {
                error(ctx, "文件ID不能为空");
                return;
            }
            BizData.bizAction.checkFileDownloadPermission(token, uuids[0]);

            downloadFileList0(ctx, uuidList);
        }catch (AppException e) {
            logger.error("downloadFileList error:{}", e.getMessage(), e);
            error(ctx, e.getMessage());
            return;
        }

    }

    private void downloadFileList0(Context ctx, String fileUuidList) {
        File rootDir = null;
        try {
            String token = ctx.getString("token");
            String[] uuids = fileUuidList.split("_");
            Set<String> uuidList = new HashSet<>(Arrays.asList(uuids));
            rootDir = new File(GlobalConfig.fileServiceHomePath + File.separator + "tmp" + File.separator
                    + UUID.randomUUID().toString());
            rootDir.mkdirs();
            List<FileTree> fileList = BizData.bizAction.getFileTree(token, uuidList);
            for (FileTree e : fileList) {
                createFile(rootDir, e);
            }
            FileDownloadUtil.downloadFileList(rootDir.getAbsolutePath(), ctx.response().raw());
        } catch (Exception e) {
            logger.error("show error message {}", e.getMessage());
            error(ctx, e.getMessage());
        } finally {
            if (rootDir != null) {
                boolean deleteResult = FileUtil.deleteDirectory(rootDir);
                logger.info("deleteDirectory deleteResult:{} absolutePath:{}", deleteResult, rootDir.getAbsolutePath());
            }
        }
    }

    //
    private void createFile(File parent, FileTree tree) {
        if (tree.isDirectory) {
            File f = new File(parent, tree.name);
            if (!f.exists()) {
                f.mkdir();
            }
            for (FileTree child : tree.children) {
                createFile(f, child);
            }
        } else {
            File t = new File(parent, tree.name);
            File file = FileServiceManager.get().download(tree.uuid);
            BizUtil.fileChannelCopy(file, t);
        }
    }

    //
    @Service(id = "sftp_download")
    public void sftpDownload(Context ctx) {
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        Map<String, String> map = JSONUtil.fromJsonMap(arg, String.class, String.class);
        String token = map.get("token");
        String machineLoginToken = map.get("machineLoginToken");
        String filePath = token = map.get("filePath");
        long fileSize = Long.valueOf(map.get("fileSize"));
        logger.info("sftpDownload map:{}", DumpUtil.dump(map));
        ConnectionDetailInfo conn = BizData.bizAction.getConnectionDetailInfo(token, machineLoginToken);
        SftpChannel channel = SftpChannel.create(conn.connectionInfo);
        InputStream is = channel.get(filePath);
        String fileName = BizUtil.getFileName(filePath);
        ctx.view(new InputSteamFileView(is, fileName, fileSize, channel));
    }

    //
    @Service(id = "sftp_upload", method = HttpMethod.POST)
    public void sftpUpload(Context ctx) throws IOException, ServletException {
        HttpServletRequest req = ctx.request().raw();
        String arg = ctx.getString("arg", true);
        arg = new String(Base64.getDecoder().decode(arg));
        Map<String, String> map = JSONUtil.fromJsonMap(arg, String.class, String.class);
        String token = map.get("token");
        String machineLoginToken = map.get("machineLoginToken");
        String filePath = token = map.get("filePath");
        String fileName = "upload_file";
        Part part = req.getPart("file");
        String header = part.getHeader("content-disposition");
        if (!StringUtil.isEmpty(header)) {
            fileName = getFileName(header);
        }
        ConnectionDetailInfo conn = BizData.bizAction.getConnectionDetailInfo(token, machineLoginToken);
        SftpChannel channel = null;
        try {
            channel = SftpChannel.create(conn.connectionInfo);
            channel.put(part.getInputStream(), filePath + File.separator + fileName);
        } finally {
            if (channel != null) {
                channel.exit();
            }
        }
        ctx.view(new PlainTextView("success"));
    }

    @Service(id = "logfile", method = HttpMethod.GET)
    public void logFile(Context ctx) {
        String fileId = ctx.getString("file");
        BizUtil.asserts(!BizUtil.isNullOrEmpty(fileId), "文件名不存在");
        try {
            String logHome = GlobalConfig.getValue("jazmin.server.log", "/cshome/jazmin_server_jdk10/log/");
            BizUtil.asserts(!BizUtil.isNullOrEmpty(fileId), "文件名不存在");
            fileId = BizUtil.filterSpecialChars(fileId);
            BizUtil.asserts(!BizUtil.isNullOrEmpty(fileId), "文件名不存在");
            if (fileId.endsWith(".log.gz")) {
                String[] ss = fileId.split("-");
                if (!BizUtil.isNullOrEmpty(ss)) {
                    String pt = ss[1];
                    String[] sts = pt.split("\\.");
                    if (!BizUtil.isNullOrEmpty(sts)) {
                        logHome += sts[0];
                    }
                }
            }
            File file = new File(logHome + fileId);
            if (!file.exists()) {
                error(ctx, "日志文件不存在");
            } else {
                ctx.view(new DownloadFileView(file, file.getName()));
            }
        } catch (Exception e) {
            error(ctx, e.getMessage());
        }
    }

}
