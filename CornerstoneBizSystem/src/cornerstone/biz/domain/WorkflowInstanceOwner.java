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
 * 流程实例责任人
 * 
 * @author 杜展扬 2019-06-22 15:35
 *
 */
@DomainDefine(domainClass = WorkflowInstanceOwner.class)
@DomainDefineValid(comment = "流程实例责任人")
public class WorkflowInstanceOwner extends BaseDomain {

	public static final int STATUS_待处理 = 1;
	public static final int STATUS_已处理 = 2;
	public static final int STATUS_他人已处理 = 3;
	//
	public static final int TYPE_责任人 = 1;
	public static final int TYPE_抄送人 = 2;
	//
	@ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
	
	@ForeignKey(domainClass = WorkflowInstance.class)
	@DomainFieldValid(comment = "流程实例", required = true, canUpdate = true)
	public int workflowInstanceId;

	@DomainFieldValid(comment = "节点", required = true, canUpdate = true, maxValue = 80)
	public String nodeId;

	@DomainFieldValid(comment = "节点名称", canUpdate = true, maxValue = 128)
	public String nodeName;

	@ForeignKey(domainClass = Account.class)
	@DomainFieldValid(comment = "责任人", required = true, canUpdate = true)
	public int ownerAccountId;

	@DomainFieldValid(comment = "状态", required = false, canUpdate = true, dataDict = "WorkflowInstanceOwner.status")
	public int status;
	
	@DomainFieldValid(comment = "类型", required = true)
	public int type;
	
	@DomainFieldValid(comment = "进入时间",canUpdate=true)
	public Date enterTime;

	@DomainFieldValid(comment = "离开时间",canUpdate=true)
	public Date leaveTime;
	
	@DomainFieldValid(comment = "表单数据",canUpdate=true)
	public String formData;
	
	@DomainFieldValid(comment = "备注",maxValue=256)
	public String remark;
	
	@DomainFieldValid(comment="提交按钮文字",canUpdate=true,maxValue=64)
    public String submitButtonText;

	@ForeignKey(domainClass = Account.class)
	@DomainFieldValid(comment = "创建人", required = true, canUpdate = true)
	public int createAccountId;

	@DomainFieldValid(comment = "更新人", required = true, canUpdate = true)
	public int updateAccountId;

	//
	public WorkflowInstanceOwner() {

	}

	//
	public static class WorkflowInstanceOwnerInfo extends WorkflowInstanceOwner {
		//
		@DomainField(foreignKeyFields = "createAccountId", field = "name", persistent = false)
		@DomainFieldValid(comment = "创建人名称")
		public String createAccountName;

		@DomainField(foreignKeyFields = "ownerAccountId", field = "name", persistent = false)
		@DomainFieldValid(comment = "责任人名称")
		public String ownerAccountName;

		@DomainField(foreignKeyFields = "ownerAccountId", field = "imageId", persistent = false)
		@DomainFieldValid(comment = "责任人头像")
		public String ownerAccountImageId;
	}

	//
	/**
	 * 
	 * @author cs
	 *
	 */
	public static class WorkflowInstanceOwnerSimpleInfo {
		
		public int ownerAccountId;

		public int status;
		
		public int type;

		public String ownerAccountName;

		public String ownerAccountImageId;
	}

	//
	@QueryDefine(domainClass = WorkflowInstanceOwnerInfo.class)
	public static class WorkflowInstanceOwnerQuery extends BizQuery {
		//
		public Integer id;
		
		public Integer companyId;

		public Integer workflowInstanceId;

		@QueryField(operator="=",field="nodeId")
		public String eqNodeId;
		
		public Integer type;

		public String nodeName;

		public Integer ownerType;

		public Integer ownerAccountId;

		public Integer status;

		public Integer createAccountId;

		public Integer updateAccountId;

		@QueryField(operator = ">=", field = "createTime")
		public Date createTimeStart;

		@QueryField(operator = "<=", field = "createTime")
		public Date createTimeEnd;

		@QueryField(operator = ">=", field = "updateTime")
		public Date updateTimeStart;

		@QueryField(operator = "<=", field = "updateTime")
		public Date updateTimeEnd;

		// in or not in fields
		@QueryField(operator = "in", field = "ownerType")
		public int[] ownerTypeInList;

		@QueryField(operator = "not in", field = "ownerType")
		public int[] ownerTypeNotInList;

		@QueryField(operator = "in", field = "ownerAccountId")
		public int[] ownerAccountIdInList;

		@QueryField(operator = "not in", field = "ownerAccountId")
		public int[] ownerAccountIdNotInList;

		@QueryField(operator = "in", field = "status")
		public int[] statusInList;

		@QueryField(operator = "not in", field = "status")
		public int[] statusNotInList;

		// ForeignQueryFields
		@QueryField(foreignKeyFields = "createAccountId", field = "name")
		public String createAccountName;

		// inner joins
		// sort
		public int idSort;
		public int workflowInstanceIdSort;
		public int nodeIdSort;
		public int nodeNameSort;
		public int ownerTypeSort;
		public int ownerAccountIdSort;
		public int statusSort;
		public int createAccountIdSort;
		public int createAccountNameSort;
		public int updateAccountIdSort;
		public int createTimeSort;
		public int updateTimeSort;
	}

}