package cornerstone.biz.srv;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.wx.WeixinAccessToken;
import cornerstone.biz.domain.wx.WeixinAccount;
import cornerstone.biz.domain.wx.WeixinConfig;
import cornerstone.biz.domain.wx.WeixinExceptionCode;
import cornerstone.biz.domain.wx.WeixinJsApiParameter;
import cornerstone.biz.domain.wx.WeixinMessage;
import cornerstone.biz.domain.wx.WeixinOAuthToken;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.http.HttpClientDriver;
import jazmin.driver.http.HttpRequest;
import jazmin.driver.http.HttpResponse;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;

/**
 * @author yama
 *
 */
public class WeixinService {
	private static Logger logger = LoggerFactory.get(WeixinService.class);
	//
	@AutoWired
	public HttpClientDriver httpClientDriver;
	//
	protected WeixinConfig config;
	//
	public void refreshAccessToken() throws AppException {
		refreshAccessToken0();
	}
	
	/**
	 * @return the config
	 */
	public WeixinConfig getConfig() {
		if (config == null) {
			config = new WeixinConfig();
			config.appId = GlobalConfig.appId;
			config.appSecret = GlobalConfig.appSecret;
			logger.debug("setup WeixinConfig appId:{} appSecret:{}", config.appId, config.appSecret);
		}
		return config;
	}

	//
	protected boolean refreshAccessToken0() throws AppException {
		WeixinConfig config = getConfig();
		if (System.currentTimeMillis() - config.accessTokenGetTime > (500 * config.accessTokenExpireSeconds)) {
			WeixinAccessToken token = getAccessToken();
			if (logger.isDebugEnabled()) {
				logger.debug("refreshAccessToken0 oldAccessToken:{} newAccessToken:{}", config.accessToken,
						token.accessToken);
			}
			config.accessToken = token.accessToken;
			config.accessTokenExpireSeconds = token.expires;
			config.accessTokenGetTime = System.currentTimeMillis();
			//
			config.jsapiTicket = getJsapiTicket();
			//
			return true;
		}
		return false;
	}

	// --------------------------------------------------------------------------
	//
	/**
	 * 获取access token
	 */
	public WeixinAccessToken getAccessToken() throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("grant_type", "client_credential");
		p.put("appid", config.appId);
		p.put("secret", config.appSecret);
		Map<String, Object> rsp = get("https://api.weixin.qq.com/cgi-bin/token", p);
		String accessToken = (String) (rsp.get("access_token"));
		int expires = (int) (rsp.get("expires_in"));
		WeixinAccessToken token = new WeixinAccessToken();
		token.accessToken = accessToken;
		token.expires = expires;
		return token;
	}

	/**
	 * 获取jsapi ticket
	 */
	public String getJsapiTicket() throws AppException {
		Map<String, String> p = new HashMap<String, String>();
		p.put("type", "jsapi");
		p.put("access_token", getConfig().accessToken);
		Map<String, Object> rsp = get("https://api.weixin.qq.com/cgi-bin/ticket/getticket", p);
		String jsapiTicket = (String) (rsp.get("ticket"));
		return jsapiTicket;
	}

	//
	/**
	 * 下载素材
	 * 
	 * @param mediaId
	 * @return
	 * @throws AppException
	 */
	public byte[] getMaterialFile(String mediaId) throws AppException {
		Map<String, String> p = new HashMap<String, String>();
		p.put("access_token", getConfig().accessToken);
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("media_id", mediaId);
		String json = JSONUtil.toJson(jsonMap);
		HttpResponse rsp = postWx0("https://api.weixin.qq.com/cgi-bin/material/get_material", p, json);
		byte mediaFile[];
		try {
			mediaFile = rsp.getResponseBodyAsBytes();
			if (logger.isDebugEnabled()) {
				logger.debug("mediaFile {}", mediaFile.length);
			}
		} catch (IOException e) {
			throw new AppException(WeixinExceptionCode.SYSTEM_ERROR, e);
		}
		return mediaFile;
	}

	//
	/**
	 * 获取jsapi ticket
	 */
	public WeixinOAuthToken getOAuthToken(String code) throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("grant_type", "authorization_code");
		p.put("appid", config.appId);
		p.put("secret", config.appSecret);
		p.put("code", code);
		Map<String, Object> rsp = get("https://api.weixin.qq.com/sns/oauth2/access_token", p);
		WeixinOAuthToken token = new WeixinOAuthToken();
		token.accessToken = (String) (rsp.get("access_token"));
		token.openId = (String) (rsp.get("openid"));
		token.unionid = (String) (rsp.get("unionid"));
		token.refreshToken = (String) (rsp.get("refresh_token"));
		return token;
	}

	/**
	 * 获取jsapi ticket
	 */
	public WeixinOAuthToken getOpenOAuthToken(String code) throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("grant_type", "authorization_code");
		p.put("appid", config.openAppId);
		p.put("secret", config.openAppSecret);
		p.put("code", code);
		Map<String, Object> rsp = get("https://api.weixin.qq.com/sns/oauth2/access_token", p);
		WeixinOAuthToken token = new WeixinOAuthToken();
		token.accessToken = (String) (rsp.get("access_token"));
		token.openId = (String) (rsp.get("openid"));
		token.unionid = (String) (rsp.get("unionid"));
		token.refreshToken = (String) (rsp.get("refresh_token"));
		return token;
	}

	/**
	 * 获取用户基本信息
	 */
	public WeixinAccount getUserInfo(String openId) throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("access_token", config.accessToken);
		p.put("openid", openId);
		p.put("lang", "zh_CN");
		Map<String, Object> rsp = get("https://api.weixin.qq.com/cgi-bin/user/info", p);
		WeixinAccount account = new WeixinAccount();
		account.city = (String) rsp.get("city");
		account.nickname = (String) rsp.get("nickname");
		account.subscribe = (Integer) rsp.get("subscribe");
		account.groupid = (Integer) rsp.getOrDefault("groupid", 0);
		account.headimgurl = (String) rsp.get("headimgurl");
		account.language = (String) rsp.get("language");
		account.openid = (String) rsp.get("openid");
		account.province = (String) rsp.get("province");
		account.country = (String) rsp.get("country");
		account.remark = (String) rsp.get("remark");
		account.sex = (Integer) rsp.getOrDefault("sex", 0);
		account.unionid = (String) rsp.get("unionid");
		// account.subscribeTime=new Date((Long) rsp.get("subscribe_time"));
		//
		return account;
	}

	/**
	 * 获取用户基本信息
	 */
	public WeixinAccount getSnsUserInfo(String openId, String accessToken) throws AppException {
		Map<String, String> p = new HashMap<String, String>();
		p.put("access_token", accessToken);
		p.put("openid", openId);
		p.put("lang", "zh_CN");
		Map<String, Object> rsp = get("https://api.weixin.qq.com/sns/userinfo", p);
		WeixinAccount account = new WeixinAccount();
		account.city = (String) rsp.get("city");
		account.nickname = (String) rsp.get("nickname");
		account.subscribe = (Integer) rsp.getOrDefault("subscribe", 0);
		account.headimgurl = (String) rsp.get("headimgurl");
		account.openid = (String) rsp.get("openid");
		account.unionid = (String) rsp.get("unionid");

		account.province = (String) rsp.get("province");
		account.country = (String) rsp.get("country");
		account.sex = (Integer) rsp.get("sex");
		//
		return account;
	}

	/**
	 * 获取access token
	 */
	public WeixinAccessToken refreshToken(String refreshToken) throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("grant_type", "refresh_token");
		p.put("appid", config.appId);
		p.put("refresh_token", refreshToken);
		Map<String, Object> rsp = get("https://api.weixin.qq.com/sns/oauth2/refresh_token", p);
		String accessToken = (String) (rsp.get("access_token"));
		int expires = (int) (rsp.get("expires_in"));
		WeixinAccessToken token = new WeixinAccessToken();
		token.accessToken = accessToken;
		token.expires = expires;
		return token;
	}

	public WeixinAccessToken refreshOpenToken(String refreshToken) throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("grant_type", "refresh_token");
		p.put("appid", config.openAppId);
		p.put("refresh_token", refreshToken);
		Map<String, Object> rsp = get("https://api.weixin.qq.com/sns/oauth2/refresh_token", p);
		String accessToken = (String) (rsp.get("access_token"));
		int expires = (int) (rsp.get("expires_in"));
		WeixinAccessToken token = new WeixinAccessToken();
		token.accessToken = accessToken;
		token.expires = expires;
		return token;
	}

	//
	/**
	 * 生成带参数的推广二维码
	 * 
	 * @throws AppException
	 */
	public String generateQRCode(int sceneId, int expireSeconds,String sceneStr) throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("access_token", config.accessToken);
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		if (expireSeconds > 2592000) {
			expireSeconds = 2592000;
		}
		if (expireSeconds > 0) {
			jsonMap.put("expire_seconds", expireSeconds);
			jsonMap.put("action_name", "QR_SCENE");
		} else {
			jsonMap.put("action_name", "QR_LIMIT_SCENE");
		}
		//
		Map<String, Object> actionInfo = new HashMap<String, Object>();
		Map<String, Object> sceneInfo = new HashMap<String, Object>();
		sceneInfo.put("scene_id", sceneId);
		sceneInfo.put("scene_str", sceneStr);
		actionInfo.put("scene", sceneInfo);
		jsonMap.put("action_info", actionInfo);
		//
		String json = JSONUtil.toJson(jsonMap);
		Map<String, Object> result = post("https://api.weixin.qq.com/cgi-bin/qrcode/create", p, json);
		String ticket = (String) result.get("ticket");
		return ticket;
	}

	/**
	 * 获取关注着的openId
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getUserList() throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("access_token", config.accessToken);
		Map<String, Object> rsp = get("https://api.weixin.qq.com/cgi-bin/user/get", p);
		Map<String, Object> data = (Map) rsp.get("data");
		List<String> openIds = (List<String>) data.get("openid");
		return openIds;
	}

	/**
	 * 发送小程序模板消息
	 * 文档地址：https://mp.weixin.qq.com/debug/wxadoc/dev/api/notice.html#%E5%8F%91%E9%80%81%E6%A8%A1%E6%9D%BF%E6%B6%88%E6%81%AF
	 * 调用接口注意事项
	 * <p>
	 * 1、如果formId获得后或支付完成后立即发送消息，遇到[41028]错误提示，那么请用延时函数延时发送消息，来等待小程序方的formId数据同步
	 * </p>
	 * <p>
	 * 2、如果是已存在的formId，则忽略第一条
	 * </p>
	 * <p>
	 * 3、下发条件说明
	 * 1)支付,当用户在小程序内完成过支付行为，可允许开发者向用户在7天内推送有限条数的模板消息（1次支付可下发3条，多次支付下发条数独立，互相不影响）
	 * 2)当用户在小程序内发生过提交表单行为且该表单声明为要发模板消息的，开发者需要向用户提供服务时，可允许开发者向用户在7天内推送有限条数的模板消息（1次提交表单可下发1条，多次提交下发条数独立，相互不影响）
	 * 4、page路径设置问题
	 * 假定页面访问路径是/pages/user/order/info/page?id=68,则需要设置为pages/user/order/info/page?id=68,不可在开头添加/。
	 * 否则会遇到41030错误 PS:开发模式下可以正确发送，但是线上就会提示41030
	 * </p>
	 * 
	 * @param openId
	 * @param templateId
	 * @param vars
	 * @param page
	 * @param formId
	 * @throws AppException
	 */
	public boolean sendXcxTemplateMessage(String openId, String templateId, Map<String, String> vars, String page,
			String formId) {
		try {
			sendXcxTemplateMessage0(openId, templateId, vars, page, formId);
		} catch (Exception e) {
			logger.catching(e);
			return false;
		}
		return true;
	}

	/**
	 * 发送小程序模板消息
	 * 文档地址：https://mp.weixin.qq.com/debug/wxadoc/dev/api/notice.html#%E5%8F%91%E9%80%81%E6%A8%A1%E6%9D%BF%E6%B6%88%E6%81%AF
	 * 调用接口注意事项
	 * <p>
	 * 1、如果formId获得后或支付完成后立即发送消息，遇到[41028]错误提示，那么请用延时函数延时发送消息，来等待小程序方的formId数据同步
	 * </p>
	 * <p>
	 * 2、如果是已存在的formId，则忽略第一条
	 * </p>
	 * <p>
	 * 3、下发条件说明
	 * 1)支付,当用户在小程序内完成过支付行为，可允许开发者向用户在7天内推送有限条数的模板消息（1次支付可下发3条，多次支付下发条数独立，互相不影响）
	 * 2)当用户在小程序内发生过提交表单行为且该表单声明为要发模板消息的，开发者需要向用户提供服务时，可允许开发者向用户在7天内推送有限条数的模板消息（1次提交表单可下发1条，多次提交下发条数独立，相互不影响）
	 * 4、page路径设置问题
	 * 假定页面访问路径是/pages/user/order/info/page?id=68,则需要设置为pages/user/order/info/page?id=68,不可在开头添加/，否则会遇到41030错误
	 * PS:开发模式下可以正确发送，但是线上就会提示41030
	 * </p>
	 * 
	 * @param openId
	 * @param templateId
	 * @param vars
	 * @param page
	 * @param formId
	 * @throws AppException
	 */
	public void sendXcxTemplateMessage0(String openId, String templateId, Map<String, String> vars, String page,
			String formId) throws AppException {
		Map<String, String> p = new HashMap<String, String>();
		p.put("access_token", config.accessToken);
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("touser", openId);
		jsonMap.put("template_id", templateId);
		jsonMap.put("page", page);
		jsonMap.put("color", "#FF0000");
		jsonMap.put("form_id", formId);
		Map<String, Object> varsMap = new HashMap<String, Object>();
		vars.forEach((k, v) -> {
			Map<String, String> varValueMap = new HashMap<>();
			varValueMap.put("value", v);
			varValueMap.put("color", "#173177");
			varsMap.put(k, varValueMap);
		});
		jsonMap.put("data", varsMap);
		String json = JSONUtil.toJson(jsonMap);
		post("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send", p, json);
	}

	//
	public boolean sendTemplateMessage(String openId, String templateId, Map<String, String> vars, String url)
			throws AppException {
		try {
			sendTemplateMessage0(openId, templateId, vars, url);
		} catch (Exception e) {
			logger.catching(e);
			return false;
		}
		return true;
	}

	//
	/**
	 * 发送模板消息
	 */
	public void sendTemplateMessage0(String openId, String templateId, Map<String, String> vars, String url)
			throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("access_token", config.accessToken);
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("touser", openId);
		jsonMap.put("template_id", templateId);
		jsonMap.put("url", url);
		jsonMap.put("topcolor", "#FF0000");
		//
		Map<String, Object> varsMap = new HashMap<String, Object>();
		vars.forEach((k, v) -> {
			Map<String, String> varValueMap = new HashMap<>();
			varValueMap.put("value", v);
			varValueMap.put("color", "#173177");
			varsMap.put(k, varValueMap);
		});
		//
		jsonMap.put("data", varsMap);
		String json = JSONUtil.toJson(jsonMap);
		post("https://api.weixin.qq.com/cgi-bin/message/template/send", p, json);
	}

	//
	public WeixinJsApiParameter signJsApi(String url) {
		WeixinConfig config = getConfig();
		String jsapiTicket = config.jsapiTicket;
		String nonceStr = UUID.randomUUID().toString();
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		String string1;
		String signature = "";
		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);;
		}
		WeixinJsApiParameter p = new WeixinJsApiParameter();
		p.appId = config.appId;
		p.timestamp = Long.valueOf(timestamp);
		p.nonceStr = nonceStr;
		p.signature = signature;
		return p;
	}

	//
	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	//
	// --------------------------------------------------------------------------
	protected Map<String, Object> get(String url, Map<String, String> parameters) throws AppException {
		Map<String, String> form = new HashMap<String, String>();
		return sendMessage(url, parameters, form, false, null);
	}

	//
	protected Map<String, Object> post(String url, Map<String, String> parameters, String body) throws AppException {
		Map<String, String> form = new HashMap<String, String>();
		return sendMessage(url, parameters, form, true, body);
	}

	//
	protected String postWx(String url, Map<String, String> parameters, String body) throws AppException {
		Map<String, String> form = new HashMap<String, String>();
		return sendWxMessage(url, parameters, form, true, body);
	}

	//
	protected HttpResponse postWx0(String url, Map<String, String> parameters, String body) throws AppException {
		Map<String, String> form = new HashMap<String, String>();
		return sendWxMessage0(url, parameters, form, true, body);
	}

	//
	private Map<String, Object> sendMessage(String url, Map<String, String> parameters, Map<String, String> form,
			boolean post, String body) throws AppException {
		WeixinConfig config = getConfig();
		HttpRequest req;
		if (post) {
			req = httpClientDriver.post(url);
		} else {
			req = httpClientDriver.get(url);
		}
		parameters.forEach((k, v) -> {
			req.addQueryParam(k, v);
		});
		form.forEach((k, v) -> {
			req.addFormParam(k, v);
		});
		HttpResponse rsp = null;
		try {
			if (body != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("request {} \n parameters:{} body {}", url, DumpUtil.dump(parameters), body);
				}
				req.setBody(body.getBytes("UTF-8"));
			}
			rsp = req.execute().get();
		} catch (Exception e) {
			throw new AppException(WeixinExceptionCode.SYSTEM_ERROR, e);
		}
		int code = rsp.getStatusCode();
		if (code != 200) {
			throw new AppException(WeixinExceptionCode.SYSTEM_ERROR, "bad request,code=" + code);
		}
		String rspBody;
		try {
			rspBody = rsp.getResponseBody("UTF-8");
			if (logger.isDebugEnabled()) {
				logger.debug("request {} \nresponse {}", url, rspBody);
			}
		} catch (IOException e) {
			throw new AppException(WeixinExceptionCode.SYSTEM_ERROR, e);
		}
		HashMap<String, Object> obj = JSONUtil.fromJson(rspBody, HashMap.class);
		Integer errorCode = (Integer) obj.get("errcode");
		String errorMsg = (String) obj.get("errmsg");
		//
		if (errorCode != null && (40001 == errorCode || 42001 == errorCode)) {
			logger.warn("refresh access token");
			config.accessTokenGetTime = 0;
		}
		if (errorCode != null && errorCode != 0) {
			int exceptionCode = WeixinExceptionCode.SYSTEM_ERROR;
			if (errorCode == 46003) {// 不存在的菜单数据
				exceptionCode = errorCode;
			}
			throw new AppException(exceptionCode, "weixin errorcode:" + errorCode + "/" + errorMsg);
		}
		//
		return obj;
	}

	//
	private HttpResponse sendWxMessage0(String url, Map<String, String> parameters, Map<String, String> form,
			boolean post, String body) throws AppException {
		HttpRequest req;
		if (post) {
			req = httpClientDriver.post(url);
		} else {
			req = httpClientDriver.get(url);
		}
		parameters.forEach((k, v) -> {
			req.addQueryParam(k, v);
		});
		form.forEach((k, v) -> {
			req.addFormParam(k, v);
		});
		HttpResponse rsp = null;
		try {
			if (body != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("request {} \n body {}", url, body);
				}
				req.setBody(body.getBytes("UTF-8"));
			}
			rsp = req.execute().get();
		} catch (Exception e) {
			throw new AppException(WeixinExceptionCode.SYSTEM_ERROR, e);
		}
		int code = rsp.getStatusCode();
		if (code != 200) {
			throw new AppException(WeixinExceptionCode.SYSTEM_ERROR, "bad request,code=" + code);
		}
		return rsp;
	}

	// 新增永久图文消息
	public String addNews(String json) throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("access_token", config.accessToken);
		String rsp = postWx("https://api.weixin.qq.com/cgi-bin/material/add_news", p, json);
		Map<String, String> result = JSONUtil.fromJson(rsp, Map.class);
		return result.get("media_id");
	}

	// 根据用户OpenId进行群发
	public String sendMassMessage(String json) throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("access_token", config.accessToken);
		String rsp = postWx("https://api.weixin.qq.com/cgi-bin/message/mass/send", p, json);
		return rsp;
	}

	// 客服消息
	public String sendCustomMessage(String json) throws AppException {
		WeixinConfig config = getConfig();
		Map<String, String> p = new HashMap<String, String>();
		p.put("access_token", config.accessToken);
		String rsp = postWx("https://api.weixin.qq.com/cgi-bin/message/custom/send", p, json);
		return rsp;
	}

	//
	private String sendWxMessage(String url, Map<String, String> parameters, Map<String, String> form, boolean post,
			String body) throws AppException {
		WeixinConfig config = getConfig();
		HttpResponse rsp = sendWxMessage0(url, parameters, form, post, body);
		String rspBody;
		try {
			rspBody = rsp.getResponseBody("UTF-8");
			if (logger.isDebugEnabled()) {
				logger.debug("request {} \nresponse {}", url, rspBody);
			}
		} catch (IOException e) {
			throw new AppException(WeixinExceptionCode.SYSTEM_ERROR, e);
		}
		HashMap<String, Object> obj = JSONUtil.fromJson(rspBody, HashMap.class);
		Integer errorCode = (Integer) obj.get("errcode");
		String errorMsg = (String) obj.get("errmsg");
		//
		if (errorCode != null && (40001 == errorCode || 42001 == errorCode)) {
			logger.warn("refresh access token");
			config.accessTokenGetTime = 0;
		}
		if (errorCode != null && errorCode != 0) {
			throw new AppException(WeixinExceptionCode.SYSTEM_ERROR, "weixin errorcode:" + errorCode + "/" + errorMsg);
		}
		//
		return rspBody;
	}

	// 来自微信的消息
	public static WeixinMessage parseWeixinMessage(String xml) throws AppException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		WeixinMessage msg = new WeixinMessage();
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			Document d = db.parse(new InputSource(new StringReader(xml)));
			NodeList nl = d.getDocumentElement().getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				String k = n.getNodeName();
				String v = n.getTextContent();
				if ("ToUserName".equals(k)) {
					msg.toUserName = v;
				} else if ("FromUserName".equals(k)) {
					msg.fromUserName = v;
				} else if ("CreateTime".equals(k)) {
					msg.createTime = new Date(Long.valueOf(v));
				} else if ("MsgType".equals(k)) {
					msg.msgType = v;
				} else {
					msg.args.put(k, v);
				}
			}
		} catch (Exception e) {
			throw new AppException(WeixinExceptionCode.SYSTEM_ERROR, e);
		}
		return msg;
	}
}
