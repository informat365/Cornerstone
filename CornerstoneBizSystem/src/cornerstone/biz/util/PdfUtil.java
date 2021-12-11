package cornerstone.biz.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lowagie.text.DocumentException;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

import cornerstone.biz.domain.ChangeLog.ChangeLogInfo;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.Task.TaskDetailInfo;
import cornerstone.biz.domain.TaskComment.TaskCommentInfo;
import jazmin.util.FileUtil;

/**
 * 
 * @author cs
 *
 */
public class PdfUtil {

	/**
	 * 
	 * @param content
	 * @param os
	 *
	 */
	public static void genPDF(String content, OutputStream os) throws IOException, DocumentException {
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(content);
		ITextFontResolver fontResolver = renderer.getFontResolver();
		fontResolver.addFont(GlobalConfig.getValue("pdf.fontPath"), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		renderer.layout();
		renderer.createPDF(os);
	}
	//
}
