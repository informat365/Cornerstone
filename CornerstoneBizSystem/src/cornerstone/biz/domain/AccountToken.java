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
 * 用户token
 * 
 * @author 杜展扬 2018-07-28
 *
 */
@DomainDefine(domainClass = AccountToken.class)
@DomainDefineValid(comment ="用户token" ,uniqueKeys={@UniqueKey(fields={"accountId"}),@UniqueKey(fields={"token"})})
public class AccountToken extends BaseDomain{
    //
    //
    @DomainFieldValid(comment="TOKEN",required=true,canUpdate=true,maxValue=32)
    public String token;
    
    @DomainFieldValid(comment="用户",required=true,canUpdate=true)
    public int accountId;
    
    //
    //   
    public static class AccountTokenInfo extends AccountToken{
    //

    }
    //
    //   
    @QueryDefine(domainClass=AccountTokenInfo.class)
    public static class AccountTokenQuery extends BizQuery{
        //
        public Integer id;

        public String token;

        public Integer accountId;

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
        public int tokenSort;
        public int accountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}