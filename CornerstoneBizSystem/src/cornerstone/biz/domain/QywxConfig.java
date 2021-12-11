package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class QywxConfig {
	public String corpid;
	public String corpsecret;//应用的凭证密钥，获取方式参考：术语说明-secret https://work.weixin.qq.com/api/doc/90000/90135/91039
	public String providerSecret;
	/**通过corpid providerSecret获取得到 有效期通过返回的expires_in来传达，正常情况下为7200秒（2小时），有效期内重复获取返回相同结果，过期后获取会返回新的provider_access_token。
	 * 企业微信可能会出于运营需要，提前使provider_access_token失效，开发者应实现provider_access_token失效时重新获取的逻辑。
	 * */
	public String providerAccessToken;
	
}