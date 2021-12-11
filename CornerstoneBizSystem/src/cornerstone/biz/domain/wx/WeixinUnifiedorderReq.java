package cornerstone.biz.domain.wx;

/**
 * 微信统一下单req
 * https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1
 * https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1
 * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
 * @author icecooly
 *
 */
public class WeixinUnifiedorderReq{
	//
	public static final String TRADE_TYPE_JSAPI="JSAPI";//公众号支付
	public static final String TRADE_TYPE_APP="APP";//app支付
	public static final String TRADE_TYPE_NATIVE="NATIVE";//原生扫码支付
	//
	/**应用ID*/
	public String appid;//必填
	/**商户号*/
	public String mchId;//必填
	/**设备号	终端设备号(门店号或收银设备ID)，默认请传"WEB"*/
	public String deviceInfo;//非必填
	/**随机字符串，不长于32位*/
	public String nonceStr;//必填
	/**签名*/
	public String sign;//必填
	/**商品描述	商品描述交易字段格式根据不同的应用场景按照以下格式：APP——需传入应用市场上的APP名字-实际商品名称，天天爱消除-游戏充值*/
	public String body;//必填
	/**商品详情	商品名称明细列表*/
	public String detail;//非必填	eg:Ipad mini  16G  白色
	/**附加数据	附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据*/
	public String attach;//非必填
	/**商户订单号	商户系统内部的订单号,32个字符内、可包含字母*/
	public String outTradeNo;//必填
	/**货币类型	符合ISO 4217标准的三位字母代码，默认人民币：CNY*/
	public String feeType;//非必填
	/**总金额 订单总金额，单位为分*/
	public int totalFee;//必填
	/**终端IP 用户端实际ip*/
	public String spbillCreateIp;//必填
	/**交易起始时间	订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010*/
	public String timeStart;//非必填
	/**交易结束时间	订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010*/
	public String timeExpire;//非必填
	/**商品标记 代金券或立减优惠功能的参数*/
	public String goodsTag;//非必填
	/**通知地址	接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。*/
	public String notifyUrl;//必填
	/**交易类型取值如下：JSAPI，NATIVE，APP*/
	public String tradeType;//必填
	/**商品ID	trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义*/
	public String productId;//非必填
	/**指定支付方式	no_credit--指定不能使用信用卡支付	eg:no_credit*/
	public String limitPay;//必填
	/**trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识*/
	public String openid;//非必填
	//
	public WeixinUnifiedorderReq(String tradeType){
		this.deviceInfo="WEB";
		this.tradeType=tradeType;
	}
	
}
