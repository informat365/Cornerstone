/**
 * 
 */
package cornerstone.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;

import cornerstone.biz.BizData;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.srv.LarkService.Callback;
import jazmin.log.Logger;
import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.Controller;
import jazmin.server.web.mvc.JsonView;
import jazmin.server.web.mvc.RedirectView;
import jazmin.server.web.mvc.Service;

/**
 * 
 * @author cs
 *
 */
@Controller(id="lark")
public class LarkController extends BaseController{
	//
	private static Logger logger=jazmin.log.LoggerFactory.get(LarkController.class);
	
	
	@Service(id="home")
	public void home(Context ctx){
		String openIdEncode=ctx.getString("openId");
		String token=null;
		try {
			token=BizData.bizAction.loginWithLarkOpenIdEncode(openIdEncode);
		} catch (Exception e) {
			RedirectView rdView=new RedirectView(BizData.larkAction.getLarkQRCodeUrl(""));
			ctx.view(rdView);
			return;
		}
		//
		setTokenCookie(ctx, token);
		String rdUrl=GlobalConfig.webUrl;
		RedirectView rdView=new RedirectView(rdUrl);
		ctx.view(rdView);
	}
	
	//
	private void setTokenCookie(Context ctx,String token) {
		Cookie cookie=new Cookie("token", token);
		cookie.setMaxAge(-1);
		cookie.setPath("/");
		ctx.response().raw().addCookie(cookie);
		
		cookie=new Cookie("token", token);
		cookie.setMaxAge(-1);
		cookie.setPath("/mobile");
		ctx.response().raw().addCookie(cookie);
		logger.info("setTokenCookie cookie:{} token:{}",cookie,token);
	}
	/**
	 * 
	 * @param ctx
	 *
	 */
	@Service(id="enter")
	public void enter(Context ctx){
		String code=ctx.getString("code");
		String state=ctx.getString("state");
		if(code==null){
			throw new IllegalArgumentException("missing code or state");
		}
		String token=BizData.bizAction.larkRegister(code, state);
		if(token==null) {
			throw new IllegalArgumentException("token is null");
		}
		setTokenCookie(ctx, token);
		String rdUrl=GlobalConfig.webUrl;
		RedirectView rdView=new RedirectView(rdUrl);
		ctx.view(rdView);
	}
	/**
	 * 
	 * @param ctx
	 *
	 */
	@Service(id="login")
	public void login(Context ctx){
		String code=ctx.getString("code");
		String state=ctx.getString("state");
		if(code==null){
			throw new IllegalArgumentException("missing code or state");
		}
		BizData.larkAction.login(code, state);
		String rdUrl=GlobalConfig.webUrl+"bind_lark.html";
		RedirectView rdView=new RedirectView(rdUrl);
		ctx.view(rdView);
	}
	
	/**
	 * 
	 * @param ctx
	 *
	 */
	@Service(id="callback")
	public void callback(Context ctx){
		String body=ctx.request().body();
		Callback callback=BizData.larkAction.larkCallback(body);
		ctx.put("challenge", callback.challenge);
		ctx.view(new JsonView());
	}
	
	@Service(id="enter_cs")
	public void enterCs(Context ctx) throws UnsupportedEncodingException {
		String state=ctx.getString("state");
		String rdUrl=GlobalConfig.webUrl;
		rdUrl=URLEncoder.encode(rdUrl,"utf-8");
		String targetURL="https://open.feishu.cn/connect/qrconnect/page/sso?"+
				"app_id="+GlobalConfig.larkAppId+
				"&redirect_uri="+rdUrl+
				"&state="+state;
		logger.info("LarkController enterTaskInfo targetURL:{}",targetURL);
		RedirectView rdView=new RedirectView(targetURL);
		rdView.setTitle("正在加载");
		ctx.view(rdView);
	}
	
	@Service(id="task_info")
	public void taskInfo(Context ctx){
		String openIdEncode=ctx.getString("openId");
		Boolean mobile=ctx.getBoolean("isMobile");
		String projectUuid=ctx.getString("projectUuid");
		String objectType=ctx.getString("objectType");
		String taskUuid=ctx.getString("taskUuid");
		String token=null;
		try {
			token=BizData.bizAction.loginWithLarkOpenIdEncode(openIdEncode);
		} catch (Exception e) {
			RedirectView rdView=new RedirectView(BizData.larkAction.getLarkQRCodeUrl(""));
			ctx.view(rdView);
			return;
		}
		//
		setTokenCookie(ctx, token);
		//
		String rdUrl=GlobalConfig.webUrl+"#/pm/project/"+projectUuid+
				"/task"+objectType+"?id="+taskUuid;
		if(mobile!=null&&mobile) {
			rdUrl=GlobalConfig.webUrl+"mobile/#/m/task?id="+taskUuid;
		}
		logger.info("rdUrl:{}",rdUrl);
		RedirectView rdView=new RedirectView(rdUrl);
		ctx.view(rdView);
	}
	
}
