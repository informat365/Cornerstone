package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 机器登录token
 * 
 * @author 杜展扬 2018-08-03
 *
 */
@DomainDefine(domainClass = MachineLoginToken.class)
@DomainDefineValid(comment ="机器登录token" ,uniqueKeys={@UniqueKey(fields={"token"})})
public class MachineLoginToken extends BaseDomain{
    //
    public static final int TYPE_交互 = 1;
    public static final int TYPE_只读 = 2;
    //
    @ForeignKey(domainClass=MachineLoginSession.class)
    @DomainFieldValid(comment="sessionId",required=true,canUpdate=true)
    public int sessionId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int accountId;
    
    @DomainFieldValid(comment="TOKEN",required=true,canUpdate=true,maxValue=64)
    public String token;
    
    @DomainFieldValid(comment="类型",canUpdate=true,dataDict="MachineLoginToken.type")
    public int type;
    
    @DomainFieldValid(comment="是否允许分享",required=true,canUpdate=true)
    public boolean enableShare;
    
    //
    //   
    public static class MachineLoginTokenInfo extends MachineLoginToken{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String accountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=MachineLoginTokenInfo.class)
    public static class MachineLoginTokenQuery extends BizQuery{
        //
        public Integer id;

        public Integer sessionId;

        public Integer accountId;

        public String token;

        public Integer type;
        
        public Boolean enableShare;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="type")
        public int[] typeInList;
        
        @QueryField(operator="not in",field="type")
        public int[] typeNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="accountId",field="name")
        public String accountName;
        
        //inner joins
        //sort
        public int idSort;
        public int sessionIdSort;
        public int accountIdSort;
        public int accountNameSort;
        public int tokenSort;
        public int typeSort;
        public int enableShareSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}