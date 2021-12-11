package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 流程实例变更记录
 * 
 * @author 杜展扬 2019-06-24 19:58
 *
 */
@DomainDefine(domainClass = WorkflowInstanceChangeLog.class)
@DomainDefineValid(comment ="流程实例变更记录" )
public class WorkflowInstanceChangeLog extends BaseDomain{
    //
    public static final int TYPE_发起 = 1;
    public static final int TYPE_终止 = 2;
    public static final int TYPE_撤回 = 3;
    public static final int TYPE_评论 = 4;
    public static final int TYPE_流转 = 5;
    public static final int TYPE_转交 = 6;
    public static final int TYPE_回退 = 7;
    public static final int TYPE_结束 = 8;
    public static final int TYPE_提交表单 = 9;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="流程实例",required=true,canUpdate=true)
    public int workflowInstanceId;
    
    @DomainFieldValid(comment="类型",required=true,canUpdate=true,dataDict="WorkflowInstanceChangeLog.type")
    public int type;
    
    @DomainFieldValid(comment="之前节点",canUpdate=true,maxValue=80)
    public String beforeNodeId;
    
    @DomainFieldValid(comment="之前节点名称",canUpdate=true,maxValue=128)
    public String beforeNodeName;
    
    @DomainFieldValid(comment="当前节点",canUpdate=true,maxValue=80)
    public String nodeId;
    
    @DomainFieldValid(comment="当前节点名称",canUpdate=true,maxValue=128)
    public String nodeName;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="责任人",canUpdate=true)
    public int ownerAccountId;
    
    @DomainFieldValid(comment="详情",canUpdate=true)
    public String detail;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=128)
    public String remark;
    
    @DomainFieldValid(comment="提交按钮文字",canUpdate=true,maxValue=64)
    public String submitButtonText;
    
    @DomainFieldValid(comment="终止按钮文字",canUpdate=true,maxValue=64)
    public String terminalButtonText;
    //   
    public static class WorkflowInstanceChangeLogInfo extends WorkflowInstanceChangeLog{
    //
        @DomainField(foreignKeyFields="ownerAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "责任人头像")
        public String ownerAccountImageId;
    
        @DomainField(foreignKeyFields="ownerAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "责任人名称")
        public String ownerAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=WorkflowInstanceChangeLogInfo.class)
    public static class WorkflowInstanceChangeLogQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer workflowInstanceId;

        public Integer type;
        
        public String beforeNodeId;

        public String beforeNodeName;

        public String nodeId;

        public String nodeName;

        public Integer ownerAccountId;

        public String detail;

        public String remark;

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
        @QueryField(foreignKeyFields="ownerAccountId",field="imageId")
        public String ownerAccountImageId;
        
        @QueryField(foreignKeyFields="ownerAccountId",field="name")
        public String ownerAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int workflowInstanceIdSort;
        public int typeSort;
        public int beforeNodeIdSort;
        public int beforeNodeNameSort;
        public int nodeIdSort;
        public int nodeNameSort;
        public int ownerAccountIdSort;
        public int ownerAccountImageIdSort;
        public int ownerAccountNameSort;
        public int detailSort;
        public int remarkSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}