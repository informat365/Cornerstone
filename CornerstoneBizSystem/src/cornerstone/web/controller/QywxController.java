/**
 * 
 */
package cornerstone.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;

import cornerstone.biz.BizData;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.qywx.aes.AesException;
import cornerstone.biz.qywx.aes.WXBizMsgCrypt;
import cornerstone.biz.util.StringUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.Controller;
import jazmin.server.web.mvc.HttpMethod;
import jazmin.server.web.mvc.PlainTextView;
import jazmin.server.web.mvc.RedirectView;
import jazmin.server.web.mvc.ResourceView;
import jazmin.server.web.mvc.Service;

/**
 * 
 * @author cs
 *
 */
@Controller(id="qywx")
public class QywxController extends BaseController{
	//
	private static Logger logger=LoggerFactory.get(QywxController.class);
	
	/**
	 * 企业接收消息
	 * 自定义丰富的服务行为。比如，用户向应用发消息时，识别消息关键词，回复不同的消息内容；用户点击应用菜单时，转化为指令，执行自动化任务。
		可以及时获取到状态变化。比如，通讯录发生变化时，不需要定时去拉取通讯录对比，而是实时地获取到变化的通讯录结点，进行同步。
	 * @param ctx
	 *
	 */
	@Service(id="callback",method=HttpMethod.ALL)
	public void callback(Context ctx) throws AesException {
		logger.debug("callback method:{}",ctx.request().raw().getMethod());
		ctx.view(new PlainTextView("success"));
		if("get".equalsIgnoreCase(ctx.request().raw().getMethod())){
			handleGetCallback(ctx,GlobalConfig.qywxToken,GlobalConfig.qywxEncodingAESKey);
		}
		if("post".equalsIgnoreCase(ctx.request().raw().getMethod())){
			handlePostCallback(ctx,GlobalConfig.qywxToken,GlobalConfig.qywxEncodingAESKey,GlobalConfig.qywxCorpId);
		}
	}
	//
	/**
	 * 第三方回调(指令回调URL)
	 * 在发生授权、通讯录变更、ticket变化等事件时，企业微信服务器会向应用的“指令回调URL”推送相应的事件消息。消息结构体将使用创建应用时的EncodingAESKey进行加密（特别注意, 在第三方回调事件中使用加解密算法，receiveid的内容为suiteid）
	 * 本章节的回调事件，服务商在收到推送后都必须直接返回字符串 “success”，若返回值不是 “success”，企业微信会把返回内容当作错误信息
	 * @param ctx
	 *
	 */
	@Service(id="zhiling_callback",method=HttpMethod.ALL)
	public void zhilingCallback(Context ctx) throws AesException {
		logger.debug("zhiling_callback method:{}",ctx.request().raw().getMethod());
		if("get".equalsIgnoreCase(ctx.request().raw().getMethod())){
			handleGetCallback(ctx,GlobalConfig.qywxAppToken,GlobalConfig.qywxAppEncodingAESKey);
		}
		if("post".equalsIgnoreCase(ctx.request().raw().getMethod())){
			handlePostCallback(ctx,GlobalConfig.qywxAppToken,
					GlobalConfig.qywxAppEncodingAESKey,GlobalConfig.qywxAppId);
		}
	} 
	//
	@Service(id="data_callback",method=HttpMethod.ALL)
	public void dataCallback(Context ctx) throws AesException {
		logger.debug("data_callback method:{}",ctx.request().raw().getMethod());
		if("get".equalsIgnoreCase(ctx.request().raw().getMethod())){
			handleGetCallback(ctx,GlobalConfig.qywxAppToken,GlobalConfig.qywxAppEncodingAESKey);
		}
		if("post".equalsIgnoreCase(ctx.request().raw().getMethod())){
			handlePostCallback(ctx,GlobalConfig.qywxAppToken,
					GlobalConfig.qywxAppEncodingAESKey,GlobalConfig.qywxAppId);
		}
	} 
	//
	private static WXBizMsgCrypt initWXBizMsgCrypt(String token,String encodingAESKey,String receiveid) {
		if(StringUtil.isEmpty(token)||StringUtil.isEmpty(encodingAESKey)
				||StringUtil.isEmpty(receiveid)) {
			logger.warn("token is empty or encodingAESKey is empty or receiveid is empty");
			return null;
		}
		try {
			WXBizMsgCrypt wxBizMsgCrypt=new WXBizMsgCrypt(token,encodingAESKey,receiveid);
			return wxBizMsgCrypt;
		} catch (Exception e) {
			logger.warn(e.getMessage(),e);
			return null;
		}
	}
	//
	public static void handleGetCallback(Context ctx,String token,String encodingAESKey){
		String msgSignature = ctx.getString("msg_signature");
		String timeStamp = ctx.getString("timestamp");
		String nonce =ctx.getString("nonce");
		String echoStr =ctx.getString("echostr");
		WXBizMsgCrypt wxBizMsgCrypt=initWXBizMsgCrypt(token,encodingAESKey,GlobalConfig.qywxCorpId);
		try {
			String decodeStr=wxBizMsgCrypt.VerifyURL(msgSignature, timeStamp, nonce, echoStr);
			if(logger.isDebugEnabled()) {
				logger.debug("decodeStr:{}",decodeStr);
			}
			ctx.view(new PlainTextView(decodeStr));
		} catch (Exception e) {
			logger.warn(e.getMessage(),e);
		}
	}
	//
	//要保证微信调用接口一定不能报错，有返回 
	public static void handlePostCallback(Context ctx,String token,String encodingAESKey,String receiverId) throws AesException {
		String msgSignature = ctx.getString("msg_signature");
		String timeStamp = ctx.getString("timestamp");
		String nonce =ctx.getString("nonce");
		String postData=ctx.request().body();
		logger.debug("post data from wechat:\n{}",postData);
		WXBizMsgCrypt wxBizMsgCrypt=initWXBizMsgCrypt(token,encodingAESKey,receiverId);
		String decodeStr=wxBizMsgCrypt.DecryptMsg(msgSignature, timeStamp, nonce, postData);
		//
		ctx.view(new PlainTextView("success"));
//		try{
//			WeixinMessage rspMsg = BizData.qywxAction.receiveSuiteMessage(decodeStr);
//			if(rspMsg!=null){
//				String rspXml=rspMsg.toXML();
//				logger.debug("response rspXml:\n {} ",rspXml);
//				ctx.view(new PlainTextView(rspXml));
//			}
//		}catch(Exception e){
//			logger.error(e.getMessage(),e);
//		}
	}
	//
	@Service(id="enter")
	public void enter(Context ctx) throws UnsupportedEncodingException {
		String state=ctx.getString("state");
		if(state==null){
			state="1";
		}
		String rdUrl=GlobalConfig.webUrl+"p/qywx/login";
		rdUrl=URLEncoder.encode(rdUrl,"utf-8");
		String targetURL="https://open.weixin.qq.com/connect/oauth2/authorize?"+
				"appid="+GlobalConfig.qywxCorpId+
				"&redirect_uri="+rdUrl+
				"&response_type=code"+ //response_type	是	返回类型，此时固定为：code
				"&scope=snsapi_base"+//scope	是	应用授权作用域。企业自建应用固定填写：snsapi_base
				"&state="+state+
				"#wechat_redirect";//#wechat_redirect	是	终端使用此参数判断是否需要带上身份信息
		RedirectView rdView=new RedirectView(targetURL);
		logger.info("enter rd {}",targetURL);
		rdView.setTitle("正在加载");
		ctx.view(rdView);
	} 
	//
	@Service(id="login")
	public void login(Context ctx){
		String code=ctx.getString("code");
		if(code==null){
			throw new IllegalArgumentException("missing code or state");
		}
		String token=null;
		try{
			token=BizData.qywxAction.qywxLogin(code);
		}catch(Exception e){
			logger.catching(e);
			RedirectView rdView=new RedirectView(getRedirectUrl(ctx));
			rdView.setTitle("正在加载");
			ctx.view(rdView);
			return;
		}
		//设置cookie
		Cookie cookie=new Cookie("token",token);
		cookie.setMaxAge(-1);
		cookie.setPath("/mobile");
		ctx.response().raw().addCookie(cookie);
		String rdUrl=getRedirectUrl(ctx);
		//
		RedirectView rdView=new RedirectView(rdUrl);
		rdView.setTitle("正在加载");
		ctx.view(rdView);
	}
	
	/**
	 * 获取绑定企业微信的链接
	 * @param ctx
	 *
	 */
	@Service(id="get_bind_url")
	public void getBindUrl(Context ctx) throws UnsupportedEncodingException {
		String token=ctx.getString("token",true);
		String rdUrl=GlobalConfig.webUrl+"p/qywx/bind";
		rdUrl=URLEncoder.encode(rdUrl,"utf-8");
		String targetURL="https://open.work.weixin.qq.com/wwopen/sso/qrConnect?"+
				"appid="+GlobalConfig.qywxCorpId+
				"&agentid="+GlobalConfig.qywxAppId+
				"&redirect_uri="+rdUrl+
				"&state="+token;
		RedirectView rdView=new RedirectView(targetURL);
		logger.info("get_bind_url rd {}",targetURL);
		rdView.setTitle("正在加载");
		ctx.view(rdView);
	}
	
	@Service(id="bind")
	public void bind(Context ctx){
		String token=ctx.getString("state",true);
		String code=ctx.getString("code");
		if(code==null){
			throw new IllegalArgumentException("missing code or state");
		}
		try{
			BizData.qywxAction.bind(token,code);
		}catch(Exception e){
			logger.catching(e);
			ctx.view(new ResourceView("/page/common/error.jsp"));
			return;
		}
		String rdUrl=GlobalConfig.webUrl+"bind_success.html";
		RedirectView rdView=new RedirectView(rdUrl);
		ctx.view(rdView);
		return;
	}
	
	protected String getRedirectUrl(Context ctx) {
		String rdUrl=GlobalConfig.webUrl+"mobile/#/t/todo";
        return rdUrl;
    }
}
