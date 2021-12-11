package cornerstone.web.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import cornerstone.biz.sftp.SftpChannel;
import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.View;
import jazmin.util.IOUtil;

/**
 * 
 * @author cs
 *
 */
public class InputSteamFileView implements View {
	private SftpChannel sftpChannel;
	private InputStream is;
	private String fileName;
	private long fileSize;
	private String mimetype;
	private int cacheSeconds;
	private String contentDisposition;

	public InputSteamFileView(InputStream is, String fileName, long fileSize, SftpChannel sftpChannel) {
		this.is = is;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.sftpChannel = sftpChannel;
		contentDisposition = "attachment";
	}

	/**
	 * @return the contentDisposition
	 */
	public String getContentDisposition() {
		return contentDisposition;
	}

	/**
	 * @param contentDisposition
	 *            the contentDisposition to set
	 */
	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	//
	@Override
	public void render(Context ctx) throws IOException {
		HttpServletResponse response = ctx.response().raw();
		if (is == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		ServletOutputStream outStream = response.getOutputStream();
		// sets response content type
		if (mimetype == null) {
			mimetype = "application/octet-stream";
		}
		response.setContentType(mimetype);
		response.setContentLengthLong(fileSize);
		// sets HTTP header
		String filename = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
		response.setHeader("Content-Disposition",
				contentDisposition + "; filename=\"" + java.net.URLEncoder.encode(filename, "UTF-8") + "\"");
		if (cacheSeconds > 0) {
			long expiry = System.currentTimeMillis() + cacheSeconds * 1000;
			response.setDateHeader("Expires", expiry);
			response.setHeader("Cache-Control", "max-age=" + cacheSeconds);
		}
		response.addHeader("Cache-Control", "public, max-age=90000");
		IOUtil.copy(is, outStream);
		IOUtil.closeQuietly(is);
		IOUtil.closeQuietly(outStream);
		if (sftpChannel != null) {
			sftpChannel.exit();
		}
	}
}
