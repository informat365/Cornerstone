package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 企业邀请码
 * 
 * @author 杜展扬 2020-02-10 18:28
 *
 */
@DomainDefine(domainClass = CompanyMemberInviteCode.class)
@DomainDefineValid(comment ="企业邀请码" ,uniqueKeys={@UniqueKey(fields={"code"})})
public class CompanyMemberInviteCode extends BaseDomain{
    //
    public static final int STATUS_未使用 = 1;
    public static final int STATUS_已使用 = 2;
    //
    @DomainFieldValid(comment="uuid",required=true,canUpdate=true,maxValue=64)
    public String code;
    
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="被邀请人",required=true,canUpdate=true)
    public int accountId;
    
    @DomainFieldValid(comment="状态",required=true,canUpdate=true,dataDict="CompanyMemberInviteCode.status")
    public int status;
    
    public Date useTime;
    
    @ForeignKey(domainClass=Account.class)
    public int createAccountId;
    
    //
    //   
    public static class CompanyMemberInviteCodeInfo extends CompanyMemberInviteCode{
    //
    	 @DomainField(foreignKeyFields="accountId",field="name",persistent=false)
         @DomainFieldValid(comment = "被邀请人")
         public String accountName;
    	 
    	 @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
         @DomainFieldValid(comment = "创建人")
         public String createAccountName;
    	 
    	 
    }
    //
    //   
    @QueryDefine(domainClass=CompanyMemberInviteCodeInfo.class)
    public static class CompanyMemberInviteCodeQuery extends BizQuery{
        //
        public Integer id;

        public String code;

        public Integer companyId;

        public Integer accountId;

        public Integer status;
        
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
        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int codeSort;
        public int companyIdSort;
        public int accountIdSort;
        public int statusSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}