/**
 * 
 */
package cornerstone.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;

import cornerstone.biz.BizData;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.wx.WeixinMessage;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.Controller;
import jazmin.server.web.mvc.HttpMethod;
import jazmin.server.web.mvc.PlainTextView;
import jazmin.server.web.mvc.RedirectView;
import jazmin.server.web.mvc.Service;

/**
 * @author yama
 *
 */
@Controller(id="wx")
public class WeixinController extends BaseController{
	//
	private static Logger logger=LoggerFactory.get(WeixinController.class);
	
	WeixinControllerCallback callback;
	
	/**
	 * 
	 * @param ctx
	 *
	 */
	@Service(id="enter")
	public void enter(Context ctx) throws UnsupportedEncodingException {
		String state=ctx.getString("state");
		String rdUrl=GlobalConfig.webUrl+"p/wx/login";
		rdUrl=URLEncoder.encode(rdUrl,"utf-8");
		String targetURL="https://open.weixin.qq.com/connect/oauth2/authorize?"+
				"appid="+GlobalConfig.appId+
				"&redirect_uri="+rdUrl+
				"&response_type=code"+ 
				"&scope=snsapi_userinfo"+
				"&state="+state+
				"#wechat_redirect";
		RedirectView rdView=new RedirectView(targetURL);
		rdView.setTitle("正在加载");
		ctx.view(rdView);
	}
	
	@Service(id="login")
	public void login(Context ctx){
		String code=ctx.getString("code");
		String state=ctx.getString("state");
		if(code==null){
			throw new IllegalArgumentException("missing code or state");
		}
		String token=BizData.bizAction.wxLogin(code, state);
		String rdUrl=GlobalConfig.webUrl+"mobile/#/wechat_login";
		if(token!=null) {//
			rdUrl=GlobalConfig.webUrl+"mobile/#/wechat_login?state="+state;
		}
		RedirectView rdView=new RedirectView(rdUrl);
		ctx.view(rdView);
	}
	
	@Service(id="enter_task_info")
	public void enterTaskInfo(Context ctx) throws UnsupportedEncodingException {
		String state=ctx.getString("state");
		String rdUrl=GlobalConfig.webUrl+"p/wx/task_info";
		rdUrl=URLEncoder.encode(rdUrl,"utf-8");
		String targetURL="https://open.weixin.qq.com/connect/oauth2/authorize?"+
				"appid="+GlobalConfig.appId+
				"&redirect_uri="+rdUrl+
				"&response_type=code"+ 
				"&scope=snsapi_userinfo"+
				"&state="+state+
				"#wechat_redirect";
		RedirectView rdView=new RedirectView(targetURL);
		rdView.setTitle("正在加载");
		ctx.view(rdView);
	}
	
	@Service(id="enter_workflow_info")
	public void enterWorkflowInfo(Context ctx) throws UnsupportedEncodingException {
		String state=ctx.getString("state");
		String rdUrl=GlobalConfig.webUrl+"p/wx/workflow_info";
		rdUrl=URLEncoder.encode(rdUrl,"utf-8");
		String targetURL="https://open.weixin.qq.com/connect/oauth2/authorize?"+
				"appid="+GlobalConfig.appId+
				"&redirect_uri="+rdUrl+
				"&response_type=code"+ 
				"&scope=snsapi_userinfo"+
				"&state="+state+
				"#wechat_redirect";
		RedirectView rdView=new RedirectView(targetURL);
		rdView.setTitle("正在加载");
		ctx.view(rdView);
	}
	
	@Service(id="task_info")
	public void taskInfo(Context ctx){
		String code=ctx.getString("code");
		String state=ctx.getString("state");
		if(code==null){
			throw new IllegalArgumentException("missing code or state");
		}
		String token=BizData.bizAction.wxPageLogin(code);
		String rdUrl=GlobalConfig.webUrl+"mobile/#/m/task";
		if(token!=null) {//
			rdUrl=GlobalConfig.webUrl+"mobile/#/m/task?id="+state+"&_token="+token;
		}
		RedirectView rdView=new RedirectView(rdUrl);
		ctx.view(rdView);
	}
	
	@Service(id="workflow_info")
	public void workflowInfo(Context ctx){
		String code=ctx.getString("code");
		String state=ctx.getString("state");
		if(code==null){
			throw new IllegalArgumentException("missing code or state");
		}
		String token=BizData.bizAction.wxPageLogin(code);
		String rdUrl=GlobalConfig.webUrl+"mobile/#/m/workflow";
		if(token!=null) {//
			rdUrl=GlobalConfig.webUrl+"mobile/#/m/workflow?id="+state+"&_token="+token;
		}
		RedirectView rdView=new RedirectView(rdUrl);
		ctx.view(rdView);
	}
	
	//微信回调接口 
	@Service(id="callback",method=HttpMethod.ALL)
	public void callback(Context ctx){
		ctx.view(new PlainTextView("success"));
		if("get".equalsIgnoreCase(ctx.request().raw().getMethod())){
			handleGetCallback(ctx);
		}
		if("post".equalsIgnoreCase(ctx.request().raw().getMethod())){
			handlePostCallback(ctx);
		}
	}
	//
	private static boolean checkSignature(String signature,String timestamp,String nonce){
		if(signature==null||timestamp==null||nonce==null){
			return false;
		}
	    String token = "jazmin";
		String tmpArr[] = new String[]{token,timestamp,nonce};
		Arrays.sort(tmpArr);
		String tmpStr="";
		for(int i=0;i<tmpArr.length;i++){
			tmpStr+=tmpArr[i];
		}
		tmpStr= sha1( tmpStr );
		if(tmpStr.equals(signature)){
			return true;
		}else{
			return false;
		}
	}
	//
	private static String sha1(String str) {
		try {
			byte[] strTemp = str.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("SHA-1"); //SHA-256
			mdTemp.update(strTemp);
			return toHexString(mdTemp.digest());
		} catch (Exception e) {
			return null;
		}
	}
	private static String toHexString(byte[] md) {
		char[] hexDigits = {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		int j = md.length;
		char str[] = new char[j * 2];
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[2 * i] = hexDigits[byte0 >>> 4 & 0xf];
			str[i * 2 + 1] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}
	//
	public static void handleGetCallback(Context ctx){
		checkParameter(ctx);
	}
	//
	private static void checkParameter(Context ctx){
		String signature = ctx.getString("signature");
		String timestamp = ctx.getString("timestamp");
		String nonce =ctx.getString("nonce");
		if(checkSignature(signature,timestamp,nonce)){
			ctx.view(new PlainTextView(ctx.getString("echostr")));
		}
	}
	//要保证微信调用接口一定不能报错，有返回 
	public static void handlePostCallback(Context ctx){
		checkParameter(ctx);
		String reqBody=ctx.request().body();
		logger.debug("post data from wechat:\n{}",reqBody);
		ctx.view(new PlainTextView("success"));
		try{
			WeixinMessage rspMsg = BizData.weixinAction.receiveWeixinMessage(reqBody);
			if(rspMsg!=null){
				String rspXml=rspMsg.toXML();
				logger.debug("handlePostCallback response \n {} ",rspXml);
				ctx.view(new PlainTextView(rspXml));
			}else {
				logger.debug("handlePostCallback response is null");
			}
		}catch(Exception e){
			logger.catching(e);
		}
	}
	//
	//jsapi ticket
//	//--------------------------------------------------------------------------
//	@Service(id="jsapi_ticket")
//	public void jsapiTicket(Context ctx)throws AppException{
//		String url=ctx.getString("url");
//		ctx.put("result",BizData.weixinAction.getJsApiParameter(url));
//		ctx.view(new JsonView());
//	}
	//
}
