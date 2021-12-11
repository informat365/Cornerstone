package cornerstone.biz.domain.wx;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author icecooly
 *
 */
public class WeixinReplyMsg {
	/**类型*/
	public String msgType;
	/**通过素材管理中的接口上传多媒体文件，得到的id*/
	public String mediaId;
	/**素材名称*/
	public String mediaName;
	/**回复的消息内容（换行：在content中能够换行，微信客户端就支持换行显示）*/
	public String content;
	/**标题*/
	public String title;
	/**多条图文消息信息，默认第一个item为大图,注意，如果图文数超过10，则将会无响应	必选*/
	public List<WeixinArticleItem> items;
	//
	public WeixinReplyMsg(){
		items=new ArrayList<>();
	}
}
