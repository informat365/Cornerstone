package cornerstone.biz.domain.wx;

/**
 * 企业付款resp(https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_2)
 * @author icecooly
 *
 */
public class WeixinTransfersResp{
	/**返回状态码	SUCCESS/FAIL 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断*/
	public String returnCode;
	/**返回信息	如非空，为错误原因签名失败参数格式校验错误*/
	public String returnMsg;
	//
	//以下字段在return_code为SUCCESS的时候有返回
	/**微信分配的公众账号ID（企业号corpid即为此appId）*/
	public String mchAppid;
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
	/**商户订单号*/
	public String outTradeNo;
	/**企业付款成功，返回的微信订单号*/
	public String paymentNo;
	/**企业付款成功时间*/
	public String paymentTime;
}
