package cornerstone.biz;

/**
 * @author rao
 *
 */
public class BizExceptionCode{
	//
	public static final int CODE_用户名或密码错误 = 100001;
	public static final int CODE_登录失败次数超过今日上限 = 100002;
	public static final int CODE_账号尚未激活 = 100003;
	public static final int CODE_TOKEN过期 = 100004;
	public static final int CODE_账号已被禁用 = 100005;
	public static final int CODE_系统繁忙 = 100006;
	public static final int CODE_手机号未注册 = 100007;
	public static final int CODE_账号已被锁定 = 100008;
	public static final int CODE_授权已到期 = 100009;
	public static final int CODE_OPENAPI认证失败 = 200001;
	public static final int CODE_OPENAPI调用失败 = 200002;
	public static final int CODE_OPENAPI业务错误 = 200003;
	public static final int CODE_未知SSO回掉结果类型 = 200004;
	public static final int CODE_SSO回掉结果为空 = 200005;
	public static final int CODE_SSO账号获取失败 = 200006;
	public static final int CODE_SSO回掉异常 = 200007;
	
}
