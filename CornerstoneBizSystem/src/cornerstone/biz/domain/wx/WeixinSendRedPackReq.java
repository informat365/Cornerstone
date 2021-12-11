package cornerstone.biz.domain.wx;

import java.util.UUID;

/**
 * https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_4&index=3
 * @author cs
 *
 */
public class WeixinSendRedPackReq {
	/**随机字符串，不长于32位*/
	public String nonceStr;
	/**必选 String(28)商户订单号（每个订单号必须唯一。取值范围：0~9，a~z，A~Z）接口根据商户订单号支持重入，如出现超时可再调用。*/
	public String mchBillno;
	/**必选 微信支付分配的商户号*/
	public String mchId;
	/**必选 微信分配的公众账号ID（企业号corpid即为此appId）。在微信开放平台（open.weixin.qq.com）申请的移动应用appid无法使用该接口。*/
	public String wxAppId;
	/**必选 红包发送者名称(天虹百货)*/
	public String sendName;
	/**必选 接受红包的用户openidopenid为用户在wxappid下的唯一标识（获取openid参见微信公众平台开发者文档：*/
	public String reOpenid;
	/**必选 付款金额，单位分*/
	public int totalAmount;
	/**必选 红包发放总人数*/
	public int totalNum;
	/**必选 红包祝福语 String(128)*/
	public String wishing;
	/**必选 调用接口的机器Ip地址 String(15)*/
	public String clientIp;
	/**必选 活动名称 String(32)*/
	public String actName;
	/**必选 备注 String(256) 备注信息*/
	public String remark;
	/**
	 * 发放红包使用场景，红包金额大于200或者小于1元时必传
	PRODUCT_1:商品促销
	PRODUCT_2:抽奖
	PRODUCT_3:虚拟物品兑奖 
	PRODUCT_4:企业内部福利
	PRODUCT_5:渠道分润
	PRODUCT_6:保险回馈
	PRODUCT_7:彩票派奖
	PRODUCT_8:税务刮奖
	 */
	public String sceneId;
	/**
	 * posttime:用户操作的时间戳
	mobile:业务系统账号的手机号，国家代码-手机号。不需要+号
	deviceid :mac 地址或者设备唯一标识 
	clientversion :用户操作的客户端版本
	把值为非空的信息用key=value进行拼接，再进行urlencode
	urlencode(posttime=xx& mobile =xx&deviceid=xx)
	 */
	public String riskInfo;
	/**资金授权商户号服务商替特约商户发放时使用 String(32)*/
	public String consumeMchId;
	/***/
	public String msgappid;
	//
	public WeixinSendRedPackReq() {
		nonceStr=UUID.randomUUID().toString().trim().replaceAll("-", "");
//		clientIp="";
	}
}
