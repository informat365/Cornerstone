package cornerstone.biz.domain.wx;

/**
 * 查询订单resp
 * @author icecooly
 *
 */
public class WeixinOrderQueryResp{
	/**返回状态码	SUCCESS/FAIL 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断*/
	public String returnCode;
	/**返回信息	如非空，为错误原因签名失败参数格式校验错误*/
	public String returnMsg;
	//
	//以下字段在return_code为SUCCESS的时候有返回
	/**应用APPID*/
	public String appid;
	/**商户号*/
	public String mchId;
	/**设备号	调用接口提交的终端设备号*/
	public String deviceInfo;
	/**随机字符串	微信返回的随机字符串*/
	public String nonceStr;
	/**微信返回的签名*/
	public String sign;
	/**业务结果	SUCCESS/FAIL*/
	public String resultCode;
	/**错误代码	详细参见第6节错误列表*/
	public String errCode;
	/**错误代码描述*/
	public String errCodeDes;
	//
	//以下字段在return_code 和result_code都为SUCCESS的时候有返回
	/**用户在商户appid 下的唯一标识*/
	public String openId;
	/**用户是否关注公众账号，仅在公众账号类型支付有效，取值范围：Y或N;Y-关注;N-未关注*/
	public String isSubscribe;
	/**支付类型为MICROPAY(即扫码支付)*/
	public String tradeType;
	/**交易状态
	 *	SUCCESS—支付成功
		REFUND—转入退款
		NOTPAY—未支付
		CLOSED—已关闭
		REVOKED—已撤销（刷卡支付）
		USERPAYING--用户支付中
		PAYERROR--支付失败(其他原因，如银行返回失败)
	 */
	public String tradeState;
	/**银行类型，采用字符串类型的银行标识，值列表详见银行类型*/
	public String bankType;
	/**符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型  非必填*/
	public String feeType;
	/**订单总金额，单位为分，只能为整数*/
	public String totalFee;
	/**应结订单金额。应结订单金额=订单金额-非充值代金券金额，应结订单金额<=订单金额。*/
	public int settlementTotalFee;
	/**现金支付货币类型*/
	public String cashFeeType;
	/**现金支付金额*/
	public String cashFee;
	/**微信支付订单号*/
	public String transactionId;
	/**商户订单号*/
	public String outTradeNo;
	/**商家数据包*/
	public String attach;
	/**订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。详见时间规则*/
	public String timeEnd;
}
