package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 验证码日志
 * 
 * @author 杜展扬 2020-02-14 18:05
 *
 */
@DomainDefine(domainClass = KaptchaLog.class)
@DomainDefineValid(comment ="验证码日志" )
public class KaptchaLog extends BaseDomain{
    //
    //
    @DomainFieldValid(comment="签名",required=true,canUpdate=true,maxValue=64)
    public String sign;
    
    @DomainFieldValid(comment="验证码",required=true,canUpdate=true,maxValue=64)
    public String code;
    
    @DomainFieldValid(comment="类型",canUpdate=true)
    public int type;
    
    @DomainFieldValid(comment="有效时间",canUpdate=true)
    public Date validTime;
    
    @DomainFieldValid(comment="账号",canUpdate=true)
    public int createAccountId;
    
    //
    public static KaptchaLog create(Kaptcha k) {
    	KaptchaLog bean=new KaptchaLog();
    	bean.sign=k.sign;
    	bean.code=k.code;
    	bean.createAccountId=k.createAccountId;
    	bean.type=k.type;
    	bean.validTime=k.validTime;
    	return bean;
    }
    //   
    public static class KaptchaLogInfo extends KaptchaLog{
    //

    }
    //
    //   
    @QueryDefine(domainClass=KaptchaLogInfo.class)
    public static class KaptchaLogQuery extends BizQuery{
        //
        public Integer id;

        public String sign;

        public String code;

        public Integer type;

        @QueryField(operator=">=",field="validTime")
        public Date validTimeStart;
        
        @QueryField(operator="<=",field="validTime")
        public Date validTimeEnd;

        public Integer createAccountId;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int signSort;
        public int codeSort;
        public int typeSort;
        public int validTimeSort;
        public int createAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}