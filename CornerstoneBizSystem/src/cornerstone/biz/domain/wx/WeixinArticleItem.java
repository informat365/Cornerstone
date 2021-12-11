package cornerstone.biz.domain.wx;

/**
 * 
 * @author icecooly
 *
 */
public class WeixinArticleItem {
	/**图文消息标题 非必选*/
	public String title;
	/**图文消息描述 非必选*/
	public String description;
	/**图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200	非必选*/
	public String picUrl;
	/**点击图文消息跳转链接 非必选*/
	public String url;
}
