package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.WorkflowChartDefine.GraphCompanyRole;
import cornerstone.biz.domain.WorkflowChartDefine.GraphDepartment;
import cornerstone.biz.domain.WorkflowChartDefine.GraphProjectRole;
import cornerstone.biz.domain.WorkflowChartDefine.GraphUser;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 流程定义权限
 * 
 * @author 杜展扬 2019-06-24 16:24
 *
 */
@DomainDefine(domainClass = WorkflowDefinePermission.class)
@DomainDefineValid(comment ="流程定义权限" ,uniqueKeys={@UniqueKey(fields={"workflowDefineId"})})
public class WorkflowDefinePermission extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
	
    @ForeignKey(domainClass=WorkflowDefine.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int workflowDefineId;
    
    //发起权限
    @DomainFieldValid(comment="成员列表",canUpdate=true)
    public List<GraphUser> accountList;
    
    @DomainFieldValid(comment="企业角色列表",canUpdate=true)
    public List<GraphCompanyRole> companyRoleList;
    
    @DomainFieldValid(comment="项目角色列表",canUpdate=true)
    public List<GraphProjectRole> projectRoleList;
    
    @DomainFieldValid(comment="部门列表",canUpdate=true)
    public List<GraphDepartment> departmentList;
    
    //数据权限
    @DomainFieldValid(comment="数据成员列表",canUpdate=true)
    public List<GraphUser> dataAccountList;
    
    @DomainFieldValid(comment="数据企业角色列表",canUpdate=true)
    public List<GraphCompanyRole> dataCompanyRoleList;
    
    @DomainFieldValid(comment="数据项目角色列表",canUpdate=true)
    public List<GraphProjectRole> dataProjectRoleList;
    
    @DomainFieldValid(comment="数据部门列表",canUpdate=true)
    public List<GraphDepartment> dataDepartmentList;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class WorkflowDefinePermissionInfo extends WorkflowDefinePermission{
    //

    }
    //   
    @QueryDefine(domainClass=WorkflowDefinePermissionInfo.class)
    public static class WorkflowDefinePermissionQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer workflowDefineId;

        public String accountList;

        public String companyRoleList;

        public String projectRoleList;

        public String departmentList;

        public String dataAccountList;

        public String dataCompanyRoleList;

        public String dataProjectRoleList;

        public String dataDepartmentList;

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
        public int workflowDefineIdSort;
        public int accountListSort;
        public int companyRoleListSort;
        public int projectRoleListSort;
        public int departmentListSort;
        public int dataAccountListSort;
        public int dataCompanyRoleListSort;
        public int dataProjectRoleListSort;
        public int dataDepartmentListSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}