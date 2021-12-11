package cornerstone.biz.domain.wx;

/**
 * https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_4&index=3
 * @author cs
 *
 */
public class WeixinSendRedPackResp {
	
	/**返回状态码	SUCCESS/FAIL 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断*/
	public String returnCode;
	/**返回信息	如非空，为错误原因签名失败参数格式校验错误*/
	public String returnMsg;
	//
	//以下字段在return_code为SUCCESS的时候有返回
	
	public String sign;
	/**业务结果	SUCCESS/FAIL*/
	public String resultCode;
	/**错误代码	详细参见第6节错误列表*/
	public String errCode;
	/**错误代码描述*/
	public String errCodeDes;
	//
	/**商户订单号（每个订单号必须唯一）组成：mch_id+yyyymmdd+10位一天内不能重复的数字*/
	public String mchBillno;
	/**商户id*/
	public String mchId;
	/**微信支付分配的商户号*/
	public String wxappid;
	/**接受收红包的用户用户在wxappid下的openid*/
	public String reOpenid;
	/**付款金额，单位分*/
	public String totalAmount;
	/**红包订单的微信单号*/
	public String sendListid;
	
}
