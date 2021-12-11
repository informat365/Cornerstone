package cornerstone.biz.domain;

import java.util.Date;
import java.util.Set;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.InnerJoin;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 角色
 * 
 * @author 杜展扬 2018-07-30
 *
 */
@DomainDefine(domainClass = Role.class)
@DomainDefineValid(comment ="角色" ,uniqueKeys={@UniqueKey(fields={"companyId","type","name"})})
public class Role extends BaseDomain{
    //
    public static final int TYPE_项目 = 1;
    public static final int TYPE_全局 = 2;//企业
    //
    public static final String NAME_管理员="管理员";
    public static final String NAME_成员="成员";
    public static final String NAME_项目管理员="项目管理员";
    public static final String NAME_项目成员="项目成员";
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="类型",canUpdate=true,dataDict="Role.type")
    public int type;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=32)
    public String name;
    
    @DomainFieldValid(comment="描述",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="是否是系统角色",canUpdate=true)
    public boolean isSystemRole;
    
    @DomainFieldValid(comment="权限列表",canUpdate=true)
    public Set<String> permissionIds;//存到数据库是treeset
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class RoleInfo extends Role{
    //

    }
    //
    //   
    @QueryDefine(domainClass=RoleInfo.class)
    public static class RoleQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public Integer type;

        public String name;
        
        @QueryField(field="name",operator="=")
        public String eqName;

        public String remark;

        public Boolean isSystemRole;

        public String permissionIds;

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
        @QueryField(operator="in",field="type")
        public int[] typeInList;
        
        @QueryField(operator="not in",field="type")
        public int[] typeNotInList;
        

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int typeSort;
        public int nameSort;
        public int remarkSort;
        public int isSystemRoleSort;
        public int permissionIdsSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
        
        //全局权限
        @InnerJoin(table2=CompanyMemberRole.class,table2Field="roleId")
        @QueryField(field="accountId")
        public Integer globalAccountId;
        
        @InnerJoin(table2=ProjectMemberRole.class,table2Field="roleId")
        @QueryField(field="accountId")
        public Integer projectAccountId;
        
        @InnerJoin(table2=ProjectMemberRole.class,table2Field="roleId")
        public Integer projectId;
        
        @InnerJoin(table2=ProjectMemberRole.class,table2Field="roleId")
        public Integer projectMemberId;
    }

}