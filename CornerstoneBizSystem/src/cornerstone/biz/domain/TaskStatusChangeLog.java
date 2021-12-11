package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 对象状态变更记录
 * 
 * @author 杜展扬 2019-05-30 16:44
 *
 */
@DomainDefine(domainClass = TaskStatusChangeLog.class)
@DomainDefineValid(comment ="对象状态变更记录" )
public class TaskStatusChangeLog extends BaseDomain{
    //
	public int companyId;
    //
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",canUpdate=true)
    public int projectId;
    
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="任务",required=true,canUpdate=true)
    public int taskId;
    
    @ForeignKey(domainClass=ProjectStatusDefine.class)
    @DomainFieldValid(comment="旧状态",required=true,canUpdate=true)
    public int oldStatus;
    
    @ForeignKey(domainClass=ProjectStatusDefine.class)
    @DomainFieldValid(comment="新状态",required=true,canUpdate=true)
    public int status;
    
    @DomainFieldValid(comment="进来时间",canUpdate=true)
    public Date enterTime;
    
    @DomainFieldValid(comment="离开时间",canUpdate=true)
    public Date leaveTime;
    
    /**旧责任人列表*/
    public List<AccountSimpleInfo> beforeOwnerList;
    
    /**新责任人列表*/
    public List<AccountSimpleInfo> afterOwnerList;
    
    /**旧责任人列表*/
    public List<Integer> beforeOwnerIdList;
    
    /**新责任人列表*/
    public List<Integer> afterOwnerIdList;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class TaskStatusChangeLogInfo extends TaskStatusChangeLog{
    //
        @DomainField(foreignKeyFields="projectId",field="name",persistent=false)
        @DomainFieldValid(comment = "项目名称")
        public String projectName;
    
        @DomainField(foreignKeyFields="taskId",field="name",persistent=false)
        @DomainFieldValid(comment = "任务名称")
        public String taskName;
    
        @DomainField(foreignKeyFields="oldStatus",field="name",persistent=false)
        @DomainFieldValid(comment = "旧状态名称")
        public String oldStatusName;
        
        @DomainField(foreignKeyFields="oldStatus",field="color",persistent=false)
        @DomainFieldValid(comment = "旧状态颜色")
        public String oldStatusColor;
    
        @DomainField(foreignKeyFields="status",field="name",persistent=false)
        @DomainFieldValid(comment = "新状态名称")
        public String statusName;
        
        @DomainField(foreignKeyFields="status",field="color",persistent=false)
        @DomainFieldValid(comment = "新状态颜色")
        public String statusColor;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    
        @DomainField(foreignKeyFields="updateAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "更新人名称")
        public String updateAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=TaskStatusChangeLogInfo.class)
    public static class TaskStatusChangeLogQuery extends BizQuery{
        //
        public Integer id;

        public Integer projectId;

        public Integer taskId;

        public Integer oldStatus;

        public Integer status;

        @QueryField(operator=">=",field="enterTime")
        public Date enterTimeStart;
        
        @QueryField(operator="<=",field="enterTime")
        public Date enterTimeEnd;

        @QueryField(operator=">=",field="leaveTime")
        public Date leaveTimeStart;
        
        @QueryField(operator="<=",field="leaveTime")
        public Date leaveTimeEnd;

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
        @QueryField(foreignKeyFields="projectId",field="name")
        public String projectName;
        
        @QueryField(foreignKeyFields="taskId",field="name")
        public String taskName;
        
        @QueryField(foreignKeyFields="oldStatus",field="name")
        public String oldStatusName;
        
        @QueryField(foreignKeyFields="status",field="name")
        public String statusName;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        @QueryField(foreignKeyFields="updateAccountId",field="name")
        public String updateAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int projectNameSort;
        public int taskIdSort;
        public int taskNameSort;
        public int oldStatusSort;
        public int oldStatusNameSort;
        public int statusSort;
        public int statusNameSort;
        public int enterTimeSort;
        public int leaveTimeSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int updateAccountNameSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}