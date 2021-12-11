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
 * 用户角色
 * 
 * @author 杜展扬 2018-07-30
 *
 */
@DomainDefine(domainClass = CompanyMemberRole.class)
@DomainDefineValid(comment ="用户角色" ,uniqueKeys={@UniqueKey(fields={"accountId","roleId"})})
public class CompanyMemberRole extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
	public int companyId;
    //
    @ForeignKey(domainClass=Role.class)
    @DomainFieldValid(comment="角色",required=true,canUpdate=true)
    public int roleId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="accountId",canUpdate=true)
    public int accountId;
    
    //
    //   
    public static class CompanyMemberRoleInfo extends CompanyMemberRole{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="roleId",field="name")
        @DomainFieldValid(comment = "角色名称")
        public String roleName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="name")
        @DomainFieldValid(comment = "accountId名称")
        public String accountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=CompanyMemberRoleInfo.class)
    public static class CompanyMemberRoleQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer roleId;

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
        @QueryField(foreignKeyFields="roleId",field="name")
        public String roleName;
        
        @QueryField(foreignKeyFields="accountId",field="name")
        public String accountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int roleIdSort;
        public int roleNameSort;
        public int accountIdSort;
        public int accountNameSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}