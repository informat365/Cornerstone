package cornerstone.web.view;

import cn.hutool.core.util.StrUtil;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.tool.markdown.Base64ImgReplacedElementFactory;
import cornerstone.biz.tool.markdown.MarkDown2HtmlWrapper;
import cornerstone.biz.tool.markdown.MarkdownMarked;
import cornerstone.biz.util.StringUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.View;
import jazmin.util.IOUtil;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author cs
 *
 */
public class PdfFileView implements View {
	//
	private static Logger logger=LoggerFactory.get(PdfFileView.class);
	//
	private String content;
	private String fileName;
	private String css;

	//
	public PdfFileView(String fileName, String content) {
		this.content = content;
		this.fileName = fileName;
	}

	public PdfFileView(String fileName, String content, String css) {
		this.content = content;
		this.fileName = fileName;
		this.css = css;
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
			genPDF(content, os, this.css);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			IOUtil.closeQuietly(os);
		}
	}

	//
	private static void appendWebUrl(TagNode result) {
		List<? extends TagNode> tagNodes = result.getElementListByName("img", true);
		for (TagNode tagNode : tagNodes) {
			String url = tagNode.getAttributeByName("src");
			if (StringUtil.isEmpty(url)) {
				continue;
			}
			if (url.startsWith("/")) {
				tagNode.addAttribute("src", GlobalConfig.webUrl.substring(0, GlobalConfig.webUrl.length() - 1) + url);
			}
		}
	}
	//

	public static void genPDF(String content, OutputStream os, String css) throws IOException, DocumentException {
		ITextRenderer renderer = new ITextRenderer();
		HtmlCleaner hc = new HtmlCleaner();
		content=cleanHtml(content);
		TagNode node = hc.clean(content);
		appendWebUrl(node);
		TagNode[] bodyNode = node.getElementsByName("body", true);
		content = hc.getInnerHtml(bodyNode[0]);
		if(logger.isDebugEnabled()) {
			logger.debug("genPDF content:{}",content);
		}
		if (StrUtil.isBlank(css)) {
			css = IOUtil.getContent(PdfFileView.class.getResourceAsStream("pdfstyle.css"));
		}
		String style = "<head><style>" + css + "</style></head>";
		renderer.setDocumentFromString(
				"<html>" + style + "<body style='font-family:Microsoft YaHei;'>" + content + "</body></html>");
		ITextFontResolver fontResolver = renderer.getFontResolver();
		fontResolver.addFont(GlobalConfig.getValue("pdf.fontPath"), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		// 如果携带图片则加上以下两行代码,将图片标签转换为Itext自己的图片对象，Base64ImgReplacedElementFactory为图片处理类
		renderer.getSharedContext().setReplacedElementFactory(new Base64ImgReplacedElementFactory());
		renderer.layout();
		renderer.createPDF(os);
	}


	public static String cleanHtml(String content) {
		Document doc = Jsoup.parse(content,"UTF8");
		Elements esd = doc.getAllElements();
		if(esd==null) {
			return content;
		}
		for (Element etemp : esd) {
			if (etemp.tagName().contains(":")) {
				etemp.remove();
				continue;
			}
//			etemp.clearAttributes();//20200208添加 因为<img src被清楚
			String styleStr = etemp.attr("style");
			if (styleStr != null) {
				styleStr = styleStr.replaceAll("font-family:.*;", "");
				etemp.attr("style", styleStr);
			}
		}
		return doc.html();
	}
	//
}
