package cornerstone.biz.domain.wx;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author yama
 *
 */
public class WeixinMessage {
	public static final String MSGTYPE_TEXT="text";
	public static final String MSGTYPE_NEWS="news";
	public static final String MSGTYPE_EVENT="event";
	public static final String MSGTYPE_IMAGE="image";
	public static final String MSGTYPE_VOICE="voice";
	public static final String MSGTYPE_VIDEO="video";
	public static final String MSGTYPE_SHORTVIDEO="shortvideo";
	public static final String MSGTYPE_LOCATION="location";
	public static final String MSGTYPE_LINK="link";
	//
	public static final String MENU_TEXT_MSG_PRE="ReplayText_";
	//
	public String toUserName;
	public String fromUserName;
	public Date createTime;
	public String msgType;
	public Map<String,String>args;
	public List<WeixinArticleItem> items;
	public WeixinMessage() {
		args=new HashMap<String, String>();
		items=new ArrayList<>();
	}
	//
	public String toXML(){
		StringBuilder result=new StringBuilder();
		result.append("<xml>");
		result.append("<ToUserName><![CDATA["+toUserName+"]]></ToUserName>");
		result.append("<FromUserName><![CDATA["+fromUserName+"]]></FromUserName>");
		result.append("<CreateTime>"+createTime.getTime()+"</CreateTime>");
		result.append("<MsgType><![CDATA["+msgType+"]]></MsgType>");
		if(msgType.equals(MSGTYPE_TEXT)){
			result.append("<Content><![CDATA["+args.get("Content")+"]]></Content>");		
		}
		if(msgType.equals(MSGTYPE_IMAGE)){
			result.append("<Image>");
			result.append("<MediaId><![CDATA["+args.get("MediaId")+"]]></MediaId>");
			result.append("</Image>");		
		}
		if(msgType.equals(MSGTYPE_VOICE)){
			result.append("<Voice>");
			result.append("<MediaId><![CDATA["+args.get("MediaId")+"]]></MediaId>");
			result.append("</Voice>");		
		}
		if(msgType.equals(MSGTYPE_VIDEO)){
			result.append("<Video>");
			result.append("<MediaId><![CDATA["+args.get("MediaId")+"]]></MediaId>");
			result.append("<Title><![CDATA["+args.get("Title")+"]]></Title>");
			result.append("<Description><![CDATA["+args.get("Description")+"]]></Description>");
			result.append("</Video>");		
		}
		if(msgType.equals(MSGTYPE_NEWS)){
			result.append("<ArticleCount>"+items.size()+"</ArticleCount>");
			result.append("<Articles>");
			for (WeixinArticleItem item : items) {
				result.append("<item>");
				result.append("<Title><![CDATA["+item.title+"]]></Title>");
				result.append("<Description><![CDATA["+item.description+"]]></Description>");
				result.append("<PicUrl><![CDATA["+item.picUrl+"]]></PicUrl>");
				result.append("<Url><![CDATA["+item.url+"]]></Url>");
				result.append("</item>");
			}
			result.append("</Articles>");
		}
		result.append("</xml>");
		return result.toString();
	}
}
