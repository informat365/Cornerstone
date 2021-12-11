package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 企业飞书
 * 
 * @author 杜展扬 2020-04-18 21:16
 *
 */
@DomainDefine(domainClass = CompanyLark.class)
@DomainDefineValid(comment ="企业飞书" ,uniqueKeys={@UniqueKey(fields={"companyUuid","larkTenantKey"})})
public class CompanyLark extends BaseDomain{
    //
    //
    @DomainFieldValid(comment="企业",required=true,maxValue=64)
    public String companyUuid;
    
    @DomainFieldValid(comment="lark_tenan",required=true,maxValue=64)
    public String larkTenantKey;
    
    @DomainFieldValid(comment="创建人",required=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true)
    public int updateAccountId;
    
    //
    //   
    public static class CompanyLarkInfo extends CompanyLark{
    //

    }
    //
    //   
    @QueryDefine(domainClass=CompanyLarkInfo.class)
    public static class CompanyLarkQuery extends BizQuery{
        //
        public Integer id;

        public String companyUuid;

        public String larkTenantKey;

        public Integer createAccountId;

        public Integer updateAccountId;

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
        public int companyUuidSort;
        public int larkTenantKeySort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}