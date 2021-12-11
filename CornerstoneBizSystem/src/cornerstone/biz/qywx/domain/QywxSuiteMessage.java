package cornerstone.biz.qywx.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author cs
 *
 */
public class QywxSuiteMessage {
	//
	public String suiteId;
	public String timeStamp;
	public String infoType;
	public Map<String,String>args;
	public QywxSuiteMessage() {
		args=new HashMap<String, String>();
	}
	//
	public String toXML(){
		StringBuilder result=new StringBuilder();
		result.append("<xml>");
//		result.append("<SuiteId><![CDATA["+suiteId+"]]></SuiteId>");
//		result.append("<FromUserName><![CDATA["+fromUserName+"]]></FromUserName>");
//		result.append("<CreateTime>"+createTime.getTime()+"</CreateTime>");
//		result.append("<MsgType><![CDATA["+msgType+"]]></MsgType>");
//		if(msgType.equals(MSGTYPE_TEXT)){
//			result.append("<Content><![CDATA["+args.get("Content")+"]]></Content>");		
//		}
//		if(msgType.equals(MSGTYPE_IMAGE)){
//			result.append("<Image>");
//			result.append("<MediaId><![CDATA["+args.get("MediaId")+"]]></MediaId>");
//			result.append("</Image>");		
//		}
//		if(msgType.equals(MSGTYPE_VOICE)){
//			result.append("<Voice>");
//			result.append("<MediaId><![CDATA["+args.get("MediaId")+"]]></MediaId>");
//			result.append("</Voice>");		
//		}
//		if(msgType.equals(MSGTYPE_VIDEO)){
//			result.append("<Video>");
//			result.append("<MediaId><![CDATA["+args.get("MediaId")+"]]></MediaId>");
//			result.append("<Title><![CDATA["+args.get("Title")+"]]></Title>");
//			result.append("<Description><![CDATA["+args.get("Description")+"]]></Description>");
//			result.append("</Video>");		
//		}
//		if(msgType.equals(MSGTYPE_NEWS)){
//			result.append("<ArticleCount>"+items.size()+"</ArticleCount>");
//			result.append("<Articles>");
//			for (WeixinArticleItem item : items) {
//				result.append("<item>");
//				result.append("<Title><![CDATA["+item.title+"]]></Title>");
//				result.append("<Description><![CDATA["+item.description+"]]></Description>");
//				result.append("<PicUrl><![CDATA["+item.picUrl+"]]></PicUrl>");
//				result.append("<Url><![CDATA["+item.url+"]]></Url>");
//				result.append("</item>");
//			}
//			result.append("</Articles>");
//		}
		result.append("</xml>");
		return result.toString();
	}
}
