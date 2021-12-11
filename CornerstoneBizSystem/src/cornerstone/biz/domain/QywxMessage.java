package cornerstone.biz.domain;

import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class QywxMessage {
	//
	public static final String MESSAGE_TYPE_文本消息="text";
	public static final String MESSAGE_TYPE_图片消息="image";
	public static final String MESSAGE_TYPE_语音消息="voice";
	public static final String MESSAGE_TYPE_视频消息="video";
	public static final String MESSAGE_TYPE_文件消息="file";
	public static final String MESSAGE_TYPE_文本卡片消息="textcard";
	public static final String MESSAGE_TYPE_图文消息="news";
	public static final String MESSAGE_TYPE_图文消息2="mpnews";
	public static final String MESSAGE_TYPE_小程序通知消息="miniprogram_notice";
	//
	/***成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送*/
	public String touser;
	
	/**企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值*/
	public int agentid;
	
	/**部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数*/
	public String toparty;
	
	/**标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数*/
	public String totag;
	
	/**消息类型，此时固定为：news*/
	public String msgtype;
	
	/***/
	public Text text;
	
	/**小程序消息内容*/
	public MiniprogramNotice miniprogram_notice;
	//
	public static class Text{
		public String content;
	}
	//
	public static class MiniprogramNotice{
		/**小程序appid，必须是与当前小程序应用关联的小程序*/
		public String appid;
		/**点击消息卡片后的小程序页面，仅限本小程序内的页面。该字段不填则消息点击后不跳转*/
		public String page;
		/**消息标题，长度限制4-12个汉字*/
		public String title;
		/**消息描述，长度限制4-12个汉字*/
		public String description;
		/**是否放大第一个content_item*/
		public boolean emphasis_first_item;
		/**消息内容键值对，最多允许10个item*/
		public List<ContentItem> content_item;
	}
	//
	
	public static class ContentItem{
		/**长度10个汉字以内*/
		public String key;
		/**长度30个汉字以内*/
		public String value;
	}
	
}
