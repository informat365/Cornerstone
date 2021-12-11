package cornerstone.web.view;

import javax.servlet.http.HttpServletResponse;

import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.Response;
import jazmin.server.web.mvc.View;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CalView implements View {
	String text;

	public CalView(String text) {
		this.text = text;
	}

	//
	@Override
	public void render(Context ctx) throws IOException {
		HttpServletResponse response = ctx.response().raw();
		response.setCharacterEncoding(Response.DEFAULT_CHARSET_ENCODING);
		response.setContentType("text/calendar;charset=UTF-8");
		byte[] outBytes = text.getBytes(StandardCharsets.UTF_8);
		response.getOutputStream().write(outBytes);
		response.setContentLength(outBytes.length);
	}
}