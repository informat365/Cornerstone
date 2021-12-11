package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;
import java.util.Set;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.WorkflowInstanceOwner.WorkflowInstanceOwnerSimpleInfo;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.InnerJoin;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 流程实例
 * 
 * @author 杜展扬 2019-06-21 21:35
 *
 */
@DomainDefine(domainClass = WorkflowInstance.class)
@DomainDefineValid(comment ="流程实例" )
public class WorkflowInstance extends BaseDomain{
    //
	public static final int STATUS_草稿=1;
	public static final int STATUS_正式=2;
	//
	public static final int FINISH_TYPE_正常结束 = 1;
	public static final int FINISH_TYPE_终止 = 2;
	public static final int FINISH_TYPE_撤回 = 3;
    //
    @DomainFieldValid(comment="UUID",required=true)
    public String uuid;
	
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true)
    public int companyId;
    
    @DomainFieldValid(comment="序列号",required=true)
    public String serialNo;
    
    @ForeignKey(domainClass=WorkflowDefine.class)
    @DomainFieldValid(comment="流程定义",required=true)
    public int workflowDefineId;
    
    @ForeignKey(domainClass=WorkflowChartDefine.class)
    @DomainFieldValid(comment="流程流转定义",required=true)
    public int workflowChartDefineId;
    
    @ForeignKey(domainClass=WorkflowFormDefine.class)
    @DomainFieldValid(comment="流程表单定义",required=true)
    public int workflowFormDefineId;
    
    @DomainFieldValid(comment="名称",maxValue=128)
    public String title;
    
    @DomainFieldValid(comment="状态",required=true)
    public int status;
    
    @DomainFieldValid(comment="是否结束",required=true)
    public boolean isFinished;
    
    @DomainFieldValid(comment="结束类型")
    public int finishType;
    
    @DomainFieldValid(comment="结束文本")
    public String finishText;
    
    @DomainFieldValid(comment="结束时间")
    public Date finishTime;
    
    @DomainFieldValid(comment="之前节点",maxValue=80)
    public String beforeNodeId;
    
    @DomainFieldValid(comment="之前节点名称",maxValue=128)
    public String beforeNodeName;
    
    @DomainFieldValid(comment="当前节点",maxValue=80)
    public String currNode;
    
    @DomainFieldValid(comment="当前节点名称",maxValue=128)
    public String currNodeName;
    
    @DomainFieldValid(comment="表单数据")
    public String formData;//Map<String,Object> formData=JSONUtil.fromJson(instance.formData, Map.class);
    
    @DomainFieldValid(comment="备注")
    public String remark;
    
    @DomainFieldValid(comment="当前责任人列表")
    public List<WorkflowInstanceOwnerSimpleInfo> currAccountList;
    
    @DomainFieldValid(comment="参与人列表")
    public Set<Integer> joinAccountIdList;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true)
    public int updateAccountId;
    
    //
    //   
    public static class WorkflowInstanceInfo extends WorkflowInstance{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
        
        @DomainField(foreignKeyFields="workflowDefineId",field="color",persistent=false)
        @DomainFieldValid(comment = "流程定义颜色")
        public String workflowDefineColor;
        
        @DomainField(foreignKeyFields="workflowDefineId",field="name",persistent=false)
        @DomainFieldValid(comment = "流程定义名称")
        public String workflowDefineName;
    

    }
    //
    //   
    @QueryDefine(domainClass=WorkflowInstanceInfo.class)
    public static class WorkflowInstanceQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;
        
        public String serialNo;

        public Integer workflowDefineId;

        public String title;
        
        public Integer status;

        public Boolean isFinished;

        public Integer finishType;
        
        public String beforeNodeId;

        public String beforeNodeName;

        public String currNode;

        public String currNodeName;

        public String formData;

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
        
        @QueryField(operator=">=",field="finishTime")
        public Date finishTimeStart;
        
        @QueryField(operator="<=",field="finishTime")
        public Date finishTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        @QueryField(foreignKeyFields="workflowDefineId",field="name")
        public String workflowDefineName;
        
        //inner joins
        @InnerJoin(table2=WorkflowInstanceOwner.class,table2Field="workflowInstanceId",
        		table1Fields={"currNode"},table2Fields= {"nodeId"})
        public Integer ownerAccountId;
        
        @InnerJoin(table2=WorkflowInstanceOwner.class,table2Field="workflowInstanceId")
        @QueryField(field="status")
        public Integer ownerStatus;
        
        @InnerJoin(table2=WorkflowInstanceOwner.class,table2Field="workflowInstanceId",
        		table1Fields={"currNode"},table2Fields= {"nodeId"})
        @QueryField(field="type")
        public Integer ownerType;
        
        @InnerJoin(table2=WorkflowInstanceOwner.class,table2Field="workflowInstanceId",
        		table1Fields={"currNode"},table2Fields= {"nodeId"})
        public Integer ccAccountId;
        
        @QueryField(whereSql="json_contains(a.join_account_id_list,'${joinAccountId}')")
        public Integer joinAccountId;//参与人id
        //sort
        public int idSort;
        public int companyIdSort;
        public int workflowDefineIdSort;
        public int titleSort;
        public int statusSort;
        public int isFinishedSort;
        public int finishTypeSort;
        public int currNodeSort;
        public int currNodeNameSort;
        public int formDataSort;
        public int remarkSort;
        public int currAccountListSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}