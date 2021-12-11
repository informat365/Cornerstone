package cornerstone.biz.domain.wx;

/**
 * 撤销订单resp
 * @author icecooly
 *
 */
public class WeixinPayReverseResp{
	/**返回状态码	SUCCESS/FAIL 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断*/
	public String returnCode;
	/**返回信息	如非空，为错误原因签名失败参数格式校验错误*/
	public String returnMsg;
	//
	//以下字段在return_code为SUCCESS的时候有返回
	/**应用APPID*/
	public String appId;
	/**商户号*/
	public String mchId;
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
	/**是否需要继续调用撤销，Y-需要，N-不需要*/
	public String recall;
}
