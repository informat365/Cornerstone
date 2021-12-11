package cornerstone.web.view;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.Response;
import jazmin.server.web.mvc.View;

/**
 * @author yama 29 Dec, 2014
 */
public class ExPlainTextView implements View {
	//
	private static final int DEFAULT_BUFFER_SIZE = 10240; // ..bytes = 10KB
	//
	String text;
	String fileName;

	public ExPlainTextView(String text, String fileName) {
		this.text = text;
		this.fileName = fileName;
	}

	//
	@Override
	public void render(Context ctx) throws IOException {
		HttpServletResponse response = ctx.response().raw();
		response.setCharacterEncoding(Response.DEFAULT_CHARSET_ENCODING);
		String contentType = "application/octet-stream";
		String disposition = "attachment";
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		fileName = URLEncoder.encode(fileName, "UTF-8");
		fileName = fileName.replace("+", "%20");
		response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
		response.setHeader("Accept-Ranges", "bytes");
		response.setContentType(contentType);
		byte[] outBytes = text.getBytes(StandardCharsets.UTF_8);
		response.getOutputStream().write(outBytes);
		response.setContentLength(outBytes.length);
	}
}