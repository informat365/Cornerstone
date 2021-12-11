package cornerstone.biz.domain.wx;

import java.util.Date;

/**
 * @author yama
 *
 */
public class WeixinAccount {
	public static final int SEX_UNKNOW=0;
	public static final int SEX_MALE=1;
	public static final int SEX_FEMALE=2;
	//
	public int subscribe;
	public String openid;
	public String nickname;
	public int sex;
	public String language;
	public String city;
	public String province;
	public String country;
	public String headimgurl;
	public Date subscribeTime;
	public String remark;
	public int groupid;
	public String unionid;
}
