package cornerstone.biz.domain.wx;

/**
 * 
 * @author icecooly
 *
 */
public class WeixinUnifiedorderResp{
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
	/**交易类型	调用接口提交的交易类型，取值如下：JSAPI，NATIVE，APP*/
	public String tradeType;
	/**预支付交易会话标识	微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时*/
	public String prepayId;
	/**二维码链接	trade_type为NATIVE是有返回，可将该参数值生成二维码展示出来进行扫码支付*/
	public String codeUrl;
}
