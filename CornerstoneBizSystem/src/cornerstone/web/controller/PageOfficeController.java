package cornerstone.web.controller;

import com.zhuozhengsoft.pageoffice.FileSaver;
import com.zhuozhengsoft.pageoffice.OfficeVendorType;
import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import cornerstone.biz.BizData;
import cornerstone.biz.BizExceptionCode;
import cornerstone.biz.FileServiceManager;
import cornerstone.biz.domain.Attachment;
import cornerstone.biz.domain.FileInfo;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.util.BizUtil;
import cornerstone.web.view.ExFileView;
import cornerstone.web.view.WriterView;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.mvc.*;
import jazmin.util.JSONUtil;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * pageOffice集成以支持office的预览和编辑功能
 */
@Controller(id = "po")
public class PageOfficeController extends BaseController {
    private static Logger logger = LoggerFactory.get(PageOfficeController.class);

    /**
     * 获取文件
     */
    @Service(id = "file", method = HttpMethod.GET)
    public void getFile(Context ctx) {
        String fileId = ctx.getString("fileId");
        File file = this.getFile(fileId);
        try {
            new ExFileView(file, file.getName()).render(ctx);
        } catch (IOException e) {
            logger.error("getFile err ", e);
        }
    }

    private File getFile(String fileId) {
        return FileServiceManager.get().getFile(fileId);
    }


    /**
     * 预览
     * 打开PageOffice预览客户端
     *
     * @param ctx
     * @throws Exception
     */
    @Service(id = "info", method = HttpMethod.GET)
    public void info(Context ctx) throws UnsupportedEncodingException {
        String fileId = ctx.getString("fileId", true);
        String token = ctx.getString("token");
        if (token == null) {
            token = ctx.request().cookie("token");
        }
        Attachment attachment = BizData.bizAction.getAttachmentByUuid(token, fileId);
        if (null == attachment || attachment.isDelete) {
            logger.warn("file not existed fileId:{}", fileId);
            ctx.view(new ErrorView(HttpStatus.NOT_FOUND_404));
            return;
        }
        ctx.put("fileId", fileId);
        ctx.put("name", URLEncoder.encode(attachment.name,"utf-8"));
        ctx.put("webUrl", GlobalConfig.webUrl);
        ctx.view(new ResourceView("/pageoffice/info.jsp"));
    }

    /**
     * 保存文件信息
     *
     * @param ctx 直接调用JSP文件
     */
    @Service(id = "save", method = HttpMethod.ALL)
    public void save(Context ctx) {
        HttpServletRequest request = ctx.request().raw();
        HttpServletResponse response = ctx.response().raw();
        String fileId = request.getParameter("fileId");
        BizUtil.asserts(!BizUtil.isNullOrEmpty(fileId),"文件名不存在");
        fileId = BizUtil.filterSpecialChars(fileId);
        BizUtil.asserts(!BizUtil.isNullOrEmpty(fileId),"文件名不存在");
        String name = request.getParameter("name");
        String parent = "" + fileId.charAt(0);
        String child = "" + fileId.charAt(1);
        String filePath = parent + File.separator + child + File.separator + fileId;
        File file = new File(new File(GlobalConfig.fileServiceHomePath), filePath);
        if (!file.exists()) {
            throw new AppException(BizExceptionCode.CODE_系统繁忙, "file not found " + filePath);
        }
        FileSaver fs = new FileSaver(request, response);
        fs.saveToFile(file.getPath());
        File storageFile = new File(file.getAbsolutePath());
        try (FileInputStream fis = new FileInputStream(storageFile)) {
          FileInfo info= FileServiceManager.get().upload(fis, name);
            ctx.view(new WriterView(JSONUtil.toJson(info)));
        } catch (IOException e) {
            logger.error("save err ", e);
            throw new AppException(BizExceptionCode.CODE_系统繁忙, " save file failed ");
        } finally {
            fs.close();
        }

//        ctx.put("status",200);
//        ctx.view(new JsonView());
    }

    /**
     * 总体方案：传入Base64数据
     */
    @Service(id = "action", method = HttpMethod.GET)
    public void action(Context ctx) {
        HttpServletRequest request = ctx.request().raw();
        // Base64文件加密
        String fileId = ctx.getString("fileId", true);
        String name = ctx.getStringOrDefault("name", "private");
        String userName = "CORNERSTONE";
        boolean editable = GlobalConfig.officeEditable;
        File file = getFile(fileId);
        if (!file.exists()) {
            logger.warn("file not existed fileId:{}", fileId);
            ctx.view(new ErrorView(403));
            return;
        }
        String filePath = "file://" + file.getAbsolutePath();
//        String filePath =  file.getAbsolutePath();
        PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
//        poCtrl.setOfficeVendor(OfficeVendorType.WPSOffice);
        poCtrl.setOfficeVendor(OfficeVendorType.AutoSelect);
        request.setAttribute("poCtrl", poCtrl);
        poCtrl.setCaption(name);
        poCtrl.setServerPage(request.getContextPath() + "/poserver.zz");
        OpenModeType openModeType = setupOpenModeType(fileId, editable);
        String viewFile;
        if (editable) {
            //添加保存按钮
            poCtrl.addCustomToolButton("保存", "Save", 1);
            //设置保存页面
            poCtrl.setSaveFilePage("/p/po/save?fileId=" + fileId+"&name="+name);
//            poCtrl.setSaveFilePage("/pageoffice/save.jsp?fileId=" + fileId+"&name="+name);
            //poCtrl.setJsFunction_AfterDocumentOpened("__afterDocumentOpened__");
            poCtrl.setMenubar(false);
            poCtrl.setCustomToolbar(true);
            poCtrl.setOfficeToolbars(false);
            viewFile = "/pageoffice/edit.jsp";
        } else {
//            poCtrl.setJsFunction_AfterDocumentOpened("__afterDocumentOpened__");
            poCtrl.setMenubar(false);
            poCtrl.setCustomToolbar(false);
            poCtrl.setOfficeToolbars(false);
            viewFile = "/pageoffice/preview.jsp";
        }
        poCtrl.webOpen(filePath, openModeType, userName);
        ctx.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        ctx.put("webUrl",GlobalConfig.webUrl);
        ctx.view(new ResourceView(viewFile));
    }

    private OpenModeType setupOpenModeType(String fileId, boolean editable) {
        String fn = fileId.toLowerCase();
        if (fn.endsWith(".xls")||fn.endsWith(".xlm") || fn.endsWith(".xlsx")) {
            return editable ? OpenModeType.xlsNormalEdit : OpenModeType.xlsReadOnly;
        }else if(fn.endsWith(".ppt") || fn.endsWith(".pptx")){
            return editable ? OpenModeType.pptNormalEdit : OpenModeType.pptReadOnly;
        }else if(fn.endsWith(".doc") || fn.endsWith(".docx")){
            return editable ? OpenModeType.docNormalEdit : OpenModeType.docReadOnly;
        }
        return OpenModeType.docReadOnly;
    }

}
