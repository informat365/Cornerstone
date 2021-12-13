package cornerstone.web;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import cornerstone.biz.util.URLUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.FileUtil;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author skydu
 */
public class PdfUtil {
    //
    public static String pdfFontPath = "D:\\svn\\CornerstoneWebSystem\\src\\cornerstone\\web\\msyh.ttf";
    //

    private static Logger logger = LoggerFactory.get(PdfUtil.class);

    public static void genPDF(String content, OutputStream os) {
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode tagNode = cleaner.clean(content);
        content = cleaner.getInnerHtml(tagNode);
        content = "<!DOCTYPE html><html>".concat(content).concat("</html>");
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(content);
        ITextFontResolver fontResolver = renderer.getFontResolver();
        try {
            fontResolver.addFont(pdfFontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            renderer.layout();
            renderer.createPDF(os);
        } catch (DocumentException | IOException e) {
           logger.error("PdfUtil ERR",e);
        }

    }

}
