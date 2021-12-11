package cornerstone.biz.srv;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.util.URLUtil;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 *
 */
public class QywxService {
	//
	private static Logger logger = LoggerFactory.get(QywxService.class);
	//
	private String accessToken;
	private Date accessTokenExpireTime;
	//
	private Map<String, String> checkResult(String json) {
		Map<String, String> map = JSONUtil.fromJsonMap(json, String.class, String.class);
		Integer errcode = Integer.valueOf(map.get("errcode"));
		String errmsg = map.get("errmsg");
		if (errcode == null || errcode != 0) {
			logger.error("checkResult :{}", json);
			throw new AppException(errmsg);
		}
		return map;
	}
	
	/**
	 * 构造网页授权链接
	 * 参数	必须	说明
appid	是	企业的CorpID
redirect_uri	是	授权后重定向的回调链接地址，请使用urlencode对链接进行处理
response_type	是	返回类型，此时固定为：code
scope	是	应用授权作用域。企业自建应用固定填写：snsapi_base
state	否	重定向后会带上state参数，企业可以填写a-zA-Z0-9的参数值，长度不可超过128个字节
#wechat_redirect	是	终端使用此参数判断是否需要带上身份信息
	 * @param redirectUri
	 * @param state
	 * @return
	 *
	 */
	public String getAuthorizeUrl(String redirectUri,String state) throws UnsupportedEncodingException {
		return String.format("https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect",
				GlobalConfig.qywxAppId,URLEncoder.encode(redirectUri,"UTF-8"),state);
	}
	//
	public void refreshAccessToken() {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("corpid", GlobalConfig.qywxCorpId);
		paraMap.put("corpsecret", GlobalConfig.qywxCorpSecret);
		String json = URLUtil.httpGet(url, paraMap);
		Map<String, String> result = checkResult(json);
		accessToken=(String)result.get("access_token");
		accessTokenExpireTime=DateUtil.getNextSecond(Integer.valueOf(result.get("expires_in")));
		logger.info("accessToken:{} accessTokenExpireTime:{}",accessToken,accessTokenExpireTime);
	}
	//
	public static class UserInfo{
		public String UserId;
		public String DeviceId;
	}
	/**
	 * https://work.weixin.qq.com/api/doc/90000/90135/91023
	 * 获取访问用户身份
	 * @param code
	 * @return
	 */
	public UserInfo getUserInfo(String code) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("access_token",accessToken);
		paraMap.put("code", code);
		String json = URLUtil.httpGet(url, paraMap);
		checkResult(json);
		UserInfo userInfo=JSONUtil.fromJson(json, UserInfo.class);
		return userInfo;
	}
}
