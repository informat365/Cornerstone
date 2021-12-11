package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.WorkflowInstanceOwner.WorkflowInstanceOwnerSimpleInfo;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 流程实例节点变更
 * 
 * @author 杜展扬 2019-06-24 17:50
 *
 */
@DomainDefine(domainClass = WorkflowInstanceNodeChange.class)
@DomainDefineValid(comment ="流程实例节点变更" )
public class WorkflowInstanceNodeChange extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
	
    @ForeignKey(domainClass=WorkflowInstance.class)
    @DomainFieldValid(comment="流程",required=true,canUpdate=true)
    public int workflowInstanceId;
    
    @DomainFieldValid(comment="节点",required=true,canUpdate=true,maxValue=80)
    public String nodeId;
    
    @DomainFieldValid(comment="节点名称",required=true,canUpdate=true,maxValue=128)
    public String nodeName;
    
    @DomainFieldValid(comment="进入时间",canUpdate=true)
    public Date enterTime;
    
    @DomainFieldValid(comment="离开时间",canUpdate=true)
    public Date leaveTime;
   
    @DomainFieldValid(comment="责任人列表",canUpdate=true,maxValue=60*1024)
    public List<WorkflowInstanceOwnerSimpleInfo> ownerAccountList;
    
    @DomainFieldValid(comment="抄送人列表",canUpdate=true,maxValue=60*1024)
    public List<WorkflowInstanceOwnerSimpleInfo> ccAccountList;
    
    @DomainFieldValid(comment="表单数据",canUpdate=true)
    public String formData;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class WorkflowInstanceNodeChangeInfo extends WorkflowInstanceNodeChange{
    //

    }
    //
    //   
    @QueryDefine(domainClass=WorkflowInstanceNodeChangeInfo.class)
    public static class WorkflowInstanceNodeChangeQuery extends BizQuery{
        //
        public Integer id;

        public Integer workflowInstanceId;
        
        public Integer companyId;

        public String nodeId;

        public String nodeName;

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

        public String ownerAccountList;

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
        public int workflowInstanceIdSort;
        public int nodeIdSort;
        public int nodeNameSort;
        public int enterTimeSort;
        public int leaveTimeSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int ownerAccountListSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}