package cornerstone.biz.domain.wx;

/**
 * 
 * @author icecooly
 *
 */
public class WeixinRefundResult {
	//
	public static final String REFUND_ACCOUNT_未结算资金退款="REFUND_SOURCE_UNSETTLED_FUNDS";
	public static final String REFUND_ACCOUNT_可用余额退款="REFUND_SOURCE_RECHARGE_FUNDS";
	//
	/**返回状态码	SUCCESS/FAIL*/
	public String returnCode;
	/**返回信息*/
	public String returnMsg;
	//
	//以下字段在return_code为SUCCESS的时候有返回
	/**业务结果	SUCCESS/FAIL*/
	public String resultCode;
	/**错误代码*/
	public String errCode;
	/**错误代码描述*/
	public String errCodeDes;
	/**应用ID*/
	public String appId;
	/**商户号*/
	public String mchId;
	/**设备号*/
	public String deviceInfo;
	/**随机字符串*/
	public String nonceStr;
	/**签名*/
	public String sign;
	/**微信订单号*/
	public String transactionId;
	/**商户订单号*/
	public String outTradeNo;
	/**商户退款单号*/
	public String outRefundNo;
	/**微信退款单号*/
	public String refundId;
	/**退款渠道*/
	public String refundChannel;
	/**退款金额	退款总金额,单位为分,可以做部分退款*/
	public String refundFee;
	/**订单总金额	订单总金额，单位为分，只能为整数*/
	public String totalFee;
	/**订单金额货币种类	订单金额货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY*/
	public String feeType;
	/**现金支付金额	现金支付金额，单位为分，只能为整数*/
	public String cashFee;
	/**现金退款金额	现金退款金额，单位为分，只能为整数*/
	public String cashRefundFee;
	/**代金券或立减优惠退款金额	代金券或立减优惠退款金额=订单金额-现金退款金额，注意：立减优惠金额不会退回*/
	public String couponRefundFee;
	/**代金券或立减优惠使用数量*/
	public String couponRefundCount;
	/**代金券或立减优惠ID*/
	public String couponRefundId;
}
