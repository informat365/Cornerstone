package cornerstone.biz;

/**
 * 
 * @author cs
 *
 */
public class NotificationEvent {
	//
	public static final String Notification_新增用户="ITFBossAction.addUser";
	public static final String Notification_修改密码="ITFBossAction.changePassword";
	public static final String Notification_重置密码="ITFBossAction.resetPassword";
	//
	public static final String Notification_微信APP支付成功="AppWeixinAction.paySuccessCallback";
	public static final String Notification_微信APP支付失败="AppWeixinAction.payFailCallback";
	public static final String Notification_微信APP创建用户="cAppWeixinAction.reateWxAccount";
	public static final String Notification_微信APP更新用户="AppWeixinAction.updateWxAccount";
	public static final String Notification_微信APP收到消息="AppWeixinAction.receiveWxMsg";
	//
	public static final String Notification_微信公众号支付成功="WeixinAction.paySuccessCallback";
	public static final String Notification_微信公众号支付失败="WeixinAction.payFailCallback";
	public static final String Notification_微信公众号用户取消订阅公众号="WeixinAction.processUnsubscribe";
	public static final String Notification_微信公众号创建用户="WeixinAction.createWxAccount";
	public static final String Notification_微信公众号更新用户="WeixinAction.updateWxAccount";
	public static final String Notification_微信公众号收到微信消息="WeixinAction.receiveWxMsg";
}
