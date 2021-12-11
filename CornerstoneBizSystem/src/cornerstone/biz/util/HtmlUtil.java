package cornerstone.biz.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import cornerstone.biz.BizData;
import cornerstone.biz.FileServiceManager;
import cornerstone.biz.domain.FileInfo;
import cornerstone.biz.domain.GlobalConfig;
import io.itit.itf.okhttp.FastHttpClient;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.IOUtil;

/**
 * 
 * @author cs
 *
 */
public class HtmlUtil {
	//
	private static Logger logger = LoggerFactory.get(HtmlUtil.class);

	/**
	 * 不会删除<p>标签
	 * @param content
	 * @return
	 *
	 */
	public static String cleanHtml(String content) {
		if (content == null || content.trim().isEmpty()) {
			return "";
		}
		HtmlCleaner hc = new HtmlCleaner();
		TagNode contentNode = hc.clean(content);
		// 设置img为绝对路径
		saveToFileServer(contentNode);
		for (TagNode tn : contentNode.getAllElements(true)) {
			// 删除script br
			if ("script".equalsIgnoreCase(tn.getName())) {
				tn.removeFromTree();
			}
		}
		//
		TagNode[] bodyNode = contentNode.getElementsByName("body", true);
		return hc.getInnerHtml(bodyNode[0]);
	}

	//
	//
	private static void saveToFileServer(TagNode result) {
		List<? extends TagNode> tagNodes = result.getElementListByName("img", true);
		for (TagNode tagNode : tagNodes) {
			ByteArrayOutputStream os = null;
			try {
				String url = tagNode.getAttributeByName("src");
				if (StringUtil.isEmpty(url)) {
					continue;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("saveToFileServer process url:{} GlobalConfig.webUrl:{}", url, GlobalConfig.webUrl);
				}
				if (!url.startsWith(GlobalConfig.webUrl) && url.startsWith("http")) {
					FileInfo fi = saveToFileServer("image.png", downloadImage(url));
					tagNode.addAttribute("src", "/p/file/get_file/" + fi.fileId);
				}
			} catch (Exception e) {
				logger.warn(e);
			} finally {
				IOUtil.closeQuietly(os);
			}
		}
	}

	//
	private static InputStream downloadImage(String url) {
		try {
			return FastHttpClient.get().url(url).build().execute().byteStream();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AppException("下载图片失败." + url);
		}
	}

	//
	private static FileInfo saveToFileServer(String fileName, InputStream is) {
		FileInfo info = new FileInfo();
		try {
			info=FileServiceManager.get().upload(is, fileName);
			IOUtil.closeQuietly(is);
			logger.info("saveToFileServer info:{}",DumpUtil.dump(info));
			return info;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * 把富文本里的img标签加上完整路径
	 * @param content
	 */
	public static String imageAppendWebUrl(String content) {
		HtmlCleaner hc = new HtmlCleaner();
		content=cleanHtml(content);
		TagNode node = hc.clean(content);
		appendWebUrl(node);
		TagNode[] bodyNode = node.getElementsByName("body", true);
		content = hc.getInnerHtml(bodyNode[0]);
		return content;
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
}
