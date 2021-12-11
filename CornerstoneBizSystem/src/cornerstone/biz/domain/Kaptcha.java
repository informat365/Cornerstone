package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 图形验证码
 * 
 * @author 杜展扬 2018-04-19
 *
 */
@DomainDefine(domainClass = Kaptcha.class)
@DomainDefineValid(comment ="验证码" ,uniqueKeys={@UniqueKey(fields={"sign"})})
public class Kaptcha extends BaseDomain{
	//
	public static final int TYPE_图形验证码=1;
	public static final int TYPE_手机验证码=2;
	public static final int TYPE_邮箱验证码=3;
	public static final int TYPE_绑定邮箱=4;
	//
    @DomainFieldValid(comment="有效时间",canUpdate=true)
    public Date validTime;
    
    @DomainFieldValid(comment="签名",required=true,maxValue=64)
    public String sign;
    
    @DomainFieldValid(comment="验证码",canUpdate=true,maxValue=32)
    public String code;
    
    public int type;
    
    public int createAccountId;
    //   
    public static class KaptchaInfo extends Kaptcha{
    //
    }
    //
    //   
    @QueryDefine(domainClass=KaptchaInfo.class)
    public static class KaptchaQuery{
    //
        public Integer id;

        @QueryField(operator=">=",field="validTime")
        public Date validTimeStart;
        
         @QueryField(operator="<=",field="validTime")
        public Date validTimeEnd;

        public String sign;

        public String code;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
         @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
         @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;


        //sort
        public int idSort;
    
        public int validTimeSort;
    
        public int signSort;
    
        public int codeSort;
    
        public int createTimeSort;
    
        public int updateTimeSort;
    
    }

}