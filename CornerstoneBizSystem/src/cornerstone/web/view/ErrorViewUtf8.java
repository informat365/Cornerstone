package cornerstone.web.view;

import javax.servlet.http.HttpServletResponse;

import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.View;

import java.io.IOException;

/**
 * @author yama
 * 8 Jan, 2015
 */
public class ErrorViewUtf8 implements View{
	//
	private int code;
	private String message;
	public ErrorViewUtf8(int code) {
		this.code=code;
	}
	public ErrorViewUtf8(int code,String message) {
		this.code=code;
		this.message=message;
	}
	//
	@Override
	public void render(Context ctx) throws IOException {
		HttpServletResponse response=ctx.response().raw();
        response.resetBuffer();
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(code);
        response.getOutputStream().print("errorMessage:"+message);
        response.flushBuffer();
	}
}
