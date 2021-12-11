package cornerstone.web.view;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.DocumentException;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.util.StringUtil;
import cornerstone.biz.util.URLUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.View;
import jazmin.util.IOUtil;

/**
 * @author cs
 */
public class TaskPdfFileView implements View {
    //
    private static Logger logger = LoggerFactory.get(TaskPdfFileView.class);
    //
    private String url;
    private String fileName;

    //
    public TaskPdfFileView(String fileName, String url) {
        this.url = url;
        this.fileName = fileName;
    }

    @Override
    public void render(Context ctx)  {
        OutputStream os = null;
        try {
            String disposition = "inline";
            HttpServletResponse response = ctx.response().raw();
            os = response.getOutputStream();
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
            genPDF(url, os);
        }catch (Exception e){
            logger.error(e.getMessage(),e);;
        }finally {
            IOUtil.closeQuietly(os);
        }
    }

    public static void genPDF(String url, OutputStream os) throws IOException, DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        String content = URLUtil.httpGet(url);
        content = cleanHtml(content);
        String fontPath = GlobalConfig.getValue("pdf.fontPath");
        if (logger.isDebugEnabled()) {
            logger.debug("genPDF url:{} fontPath:{} content:{}", url, fontPath, content);
        }
        renderer.setDocumentFromString(content);
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.layout();
        renderer.createPDF(os);
    }

    public static String cleanHtml(String content) {
        if (StringUtil.isEmptyWithTrim(content)) {
            return content;
        }
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode tagNode = cleaner.clean(content);
        content = cleaner.getInnerHtml(tagNode);
        return "<!DOCTYPE html><html lang=\"zh\">".concat(content).concat("</html>");
    }
}