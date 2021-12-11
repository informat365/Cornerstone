package cornerstone.biz.domain.wx;
/**
 * 
 * @author yama
 *
 */
public class WeixinPayResult {
	public static final String TRADE_TYPE_JSAPI="JSAPI";
	public static final String TRADE_TYPE_NATIVE="NATIVE";
	
	public String prepayId;
	public String codeUrl;
}
