package cornerstone.biz.domain.wx;

/**
 * 调起APP支付的请求参数
 * @author icecooly
 *
 */
public class WeixinAppPayReq{
	public String appid;
	public String partnerId;
	public String prepayId;
	public String nonceStr;
	public String timeStamp;
	public String packageValue;
	public String sign;
}
