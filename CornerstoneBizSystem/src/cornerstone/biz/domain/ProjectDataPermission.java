package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

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
 * 项目数据权限定义
 * 
 * @author 杜展扬 2018-07-30
 *
 */
@DomainDefine(domainClass = ProjectDataPermission.class)
@DomainDefineValid(comment ="项目数据权限定义" ,uniqueKeys={@UniqueKey(fields={"projectId","objectType","permissionId"})})
public class ProjectDataPermission extends BaseDomain{
    //
	public static final int OWNER_ID_创建人=-1;
	public static final int OWNER_ID_责任人=0;
    //
	public int companyId;
	
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="对象类型",canUpdate=true)
    public int objectType;
    
    @ForeignKey(domainClass=Permission.class)
    @DomainFieldValid(comment="权限",required=true,canUpdate=true)
    public String permissionId;
    
    @DomainFieldValid(comment="项目角色列表",canUpdate=true,maxValue=512)
    public List<Integer> ownerList;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ProjectDataPermissionInfo extends ProjectDataPermission{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="permissionId",field="name")
        @DomainFieldValid(comment = "权限名称")
        public String permissionName;
    

    }
    //
    //   
    @QueryDefine(domainClass=ProjectDataPermissionInfo.class)
    public static class ProjectDataPermissionQuery extends BizQuery{
        //
        public Integer id;

        public Integer projectId;

        public String permissionId;

        public String ownerList;

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
        @QueryField(foreignKeyFields="permissionId",field="name")
        public String permissionName;
        
        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int permissionIdSort;
        public int permissionNameSort;
        public int ownerListSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}