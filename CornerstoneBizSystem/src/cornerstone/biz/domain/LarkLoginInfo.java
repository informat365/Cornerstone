package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class LarkLoginInfo {
	//
	public static final int CODE_授权失败=1;
	public static final int CODE_已有账号绑定=2;
	public static final int CODE_绑定已登录账户=3;
	public static final int CODE_没有账号绑定=4;
	//
	public int code;
	public String message;
	public String name;
	public String openId;
	public String tenantKey;
	public String token;
}
