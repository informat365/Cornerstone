package cornerstone.biz.domain.wx;

import java.util.List;

/**
 * 微信关键词自动回复规则
 * @author icecooly
 *
 */
public class WeixinKeyAutoReplyRule {
	/**规则名 最多60个字*/
	public String ruleName;
	/**关键字列表(每个关键字最多30个字) 分号分隔*/
	public String keywords;
	/**回复内容*/
	public List<WeixinReplyMsg> replyMsgs;
	/**是否回复全部*/
	public boolean replayAll;
}
