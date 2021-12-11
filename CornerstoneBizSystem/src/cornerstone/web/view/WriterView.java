package cornerstone.web.view;

import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.Response;
import jazmin.server.web.mvc.View;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author cs
 *
 */
public class WriterView implements View {
	String text;
	public WriterView(String text) {
		this.text=text;
	}
	//
	@Override
	public void render(Context ctx) throws Exception {
		HttpServletResponse response=ctx.response().raw();
		response.setCharacterEncoding(Response.DEFAULT_CHARSET_ENCODING);
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().print(text);
	}
}