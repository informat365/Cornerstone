package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.ChangeLogTypes;
import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.ProjectIteration.ProjectIterationInfo;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.Wiki.WikiInfo;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 变更记录
 * 
 * @author 杜展扬 2018-08-02
 *
 */
@DomainDefine(domainClass = ChangeLog.class)
@DomainDefineValid(comment = "变更记录")
public class ChangeLog extends BaseDomain {
	//
	public static final int TYPE_创建 = 1;//NONE
	public static final int TYPE_编辑属性 = 2;// List<ChangeLogItem>
	public static final int TYPE_上传附件 = 3;// Attachment
	public static final int TYPE_删除附件 = 4;// Attachment
	public static final int TYPE_新增关联对象 = 5;// TaskSimpleInfo
	public static final int TYPE_关联前置对象 = 52;// TaskSimpleInfo
	public static final int TYPE_关联后置对象 = 53;// TaskSimpleInfo
	public static final int TYPE_被对象关联 = 51;// TaskSimpleInfo
	public static final int TYPE_被关联为前置对象 = 54;// TaskSimpleInfo
	public static final int TYPE_被关联为后置对象 = 55;// TaskSimpleInfo
	public static final int TYPE_取消关联对象 = 6;// TaskSimpleInfo
	public static final int TYPE_新增子任务 = 7;// TaskSimpleInfo
	public static final int TYPE_删除子任务 = 8;// TaskSimpleInfo
	public static final int TYPE_编辑详细描述 = 9;// String
	public static final int TYPE_新增项目成员 = 10;// ProjectMemberInfo
	public static final int TYPE_删除项目成员 = 11;// ProjectMemberInfo
	public static final int TYPE_新增任务 = 12;// TaskSimpleInfo
	public static final int TYPE_删除任务 = 13;// TaskSimpleInfo
	public static final int TYPE_新增迭代 = 14;// ProjectIterationInfo
	public static final int TYPE_删除迭代 = 15;// ProjectIterationInfo
	public static final int TYPE_新增Release = 16;// ProjectRelease
	public static final int TYPE_删除Release = 17;// ProjectRelease
	public static final int TYPE_新增子系统 = 18;// ProjectSubSystem
	public static final int TYPE_删除子系统 = 19;// ProjectSubSystem
	public static final int TYPE_新增SCM提交记录 = 20;// ScmCommitLog
	public static final int TYPE_恢复任务 = 21;// 恢复任务 TaskSimpleInfo
	public static final int TYPE_恢复Release = 22;// ProjectRelease
	public static final int TYPE_恢复子系统 = 23;// ProjectSubSystem
	//
	public static final int TYPE_新增知识库 = 101;// WIKI WikiInfo
	public static final int TYPE_删除知识库 = 102;// WikiInfo
	public static final int TYPE_恢复知识库 = 103;// WikiInfo

	public static final int TYPE_恢复迭代 = 110;// 迭代ProjectIteration
	//
	public static final int TYPE_新增WIKIPAGE = 120;// WIKI WikiPageSimpleInfo
	public static final int TYPE_删除WIKIPAGE = 121;// WikiPageSimpleInfo
	public static final int TYPE_恢复WIKIPAGE = 122;// WikiPageSimpleInfo

	public static final int TYPE_新增主机 = 131;// Machine
	public static final int TYPE_删除主机 = 132;// Machine
	public static final int TYPE_恢复主机 = 133;// Machine

	public static final int TYPE_归档项目 = 141;// ProjectSimpleInfo
	public static final int TYPE_删除项目 = 142;// ProjectSimpleInfo
	public static final int TYPE_重新打开项目 = 143;// ProjectSimpleInfo
	public static final int TYPE_变更项目工作流状态 = 144;// ProjectSimpleInfo

	public static final int TYPE_登录主机 = 150;// MachineSimpleInfo

	public static final int TYPE_执行PIPELINE = 160;// ProjectPipelineSimpleInfo

//    public static final int TYPE_填写迭代进度=170;
	public static final int TYPE_编辑迭代 = 171;// ProjectIterationInfo

	public static final int TYPE_新增文件 = 180;// File
	public static final int TYPE_删除文件 = 181;// File
	
	//版本库
	public static final int TYPE_新增版本库 = 1000;
	public static final int TYPE_编辑版本库 = 1001;
	public static final int TYPE_删除版本库 = 1002;
	//
	public static final int TYPE_新增版本库版本 = 1100;
	public static final int TYPE_编辑版本库版本 = 1101;
	public static final int TYPE_删除版本库版本 = 1102;
	public static final int TYPE_版本库版本关联对象 = 1103;
	public static final int TYPE_版本库版本删除对象 = 1104;

	//交付版本
	public static final int TYPE_新增交付版本=1200;
	public static final int TYPE_编辑交付版本=1201;
	public static final int TYPE_删除交付版本=1202;

	public static final int TYPE_恢复汇报 =1300;

	public static final int TYPE_编辑工时记录 =1400;

	//关联wiki
	public static final int TYPE_关联wiki附件 =1500;
	public static final int TYPE_解除wiki附件 =1501;

	//阶段
	public static final int TYPE_新增阶段=1600;
	public static final int TYPE_编辑阶段=1601;
	public static final int TYPE_删除阶段=1602;

	//里程碑
	public static final int TYPE_新增里程碑=1701;
	public static final int TYPE_编辑里程碑=1702;
	public static final int TYPE_删除里程碑=1703;



	//
	@ForeignKey(domainClass = Company.class)
	public int companyId;

	@ForeignKey(domainClass = Project.class)
	@DomainFieldValid(comment = "项目", required = true, canUpdate = true)
	public int projectId;

	@ForeignKey(domainClass = Task.class)
	@DomainFieldValid(comment = "任务", required = true, canUpdate = true)
	public int taskId;
	
	@DomainFieldValid(comment = "关联ID")
	public int associatedId;

	@DomainFieldValid(comment = "类型", canUpdate = true, dataDict = "ChangeLog.type")
	public int type;

	@DomainFieldValid(comment = "变更内容", canUpdate = true)
	public String items;

	@DomainFieldValid(comment = "备注", canUpdate = true, maxValue = 512)
	public String remark;

	@DomainFieldValid(comment = "itemId", canUpdate = true)
	public String itemId;

	@ForeignKey(domainClass = Account.class)
	@DomainFieldValid(comment = "创建人", required = true, canUpdate = true)
	public int createAccountId;

	//
	//
	public static class ChangeLogInfo extends ChangeLog {
		//
		@NonPersistent
		@DomainField(foreignKeyFields = "projectId", field = "name")
		@DomainFieldValid(comment = "项目名称")
		public String projectName;

		@NonPersistent
		@DomainField(foreignKeyFields = "taskId", field = "name")
		@DomainFieldValid(comment = "任务名称")
		public String taskName;

		@NonPersistent
		@DomainField(foreignKeyFields = "createAccountId", field = "name")
		@DomainFieldValid(comment = "创建人名称")
		public String createAccountName;

		@NonPersistent
		@DomainField(foreignKeyFields = "createAccountId", field = "imageId")
		@DomainFieldValid(comment = "创建人头像")
		public String createAccountImageId;

		@DomainField(ignoreWhenSelect = true, persistent = false)
		public ChangeLogItemDetailInfo itemsInfo;
	}
	//
	public static class ChangeLogItemDetailInfo {

		@ChangeLogTypes({2})
		public List<ChangeLogItem> itemList;

		@ChangeLogTypes({3,4})
		public Attachment attachment;

		@ChangeLogTypes({9})
		public String value;

		@ChangeLogTypes({5,6,7,8,12,13,21})
		public TaskSimpleInfo task;

		@ChangeLogTypes({10,11})
		public ProjectMemberInfo projectMember;

		@ChangeLogTypes({14,15,110,171})
		public ProjectIterationInfo projectIteration;

		@ChangeLogTypes({16,17,22})
		public ProjectRelease projectRelease;

		@ChangeLogTypes({18,19,23})
		public ProjectSubSystem projectSubSystem;

		@ChangeLogTypes({20})
		public ScmCommitLog scmCommitLog;

		@ChangeLogTypes({101,102,103})
		public WikiInfo wiki;
		
		@ChangeLogTypes({120,121,122})
		public WikiPageSimpleInfo wikiPage;

		@ChangeLogTypes({141,142,143})
		public ProjectSimpleInfo project;

		@ChangeLogTypes({131,132,133})
		public Machine machine;

		@ChangeLogTypes({150})
		public MachineSimpleInfo machineSimpleInfo;

		@ChangeLogTypes({160})
		public ProjectPipelineSimpleInfo projectPipeline;

		@ChangeLogTypes({180,181})
		public File file;
		
		@ChangeLogTypes({190,191})
		public Report report;
		//
	}

	//
	@QueryDefine(domainClass = ChangeLogInfo.class)
	public static class ChangeLogQuery extends BizQuery {
		//
		public Integer id;
		
		public Integer associatedId;
		
		@QueryField(field = "taskId")
		public int[] taskIdInList;

		public Integer companyId;

		public Integer projectId;

		@QueryField(field = "projectId")
		public int[] projectIdInList;

		public Integer taskId;

		public Integer type;

		public String items;

		public String remark;

		public Integer createAccountId;

		@QueryField(operator = ">=", field = "createTime")
		public Date createTimeStart;

		@QueryField(operator = "<", field = "createTime")
		public Date createTimeEnd;

		@QueryField(operator = ">=", field = "updateTime")
		public Date updateTimeStart;

		@QueryField(operator = "<=", field = "updateTime")
		public Date updateTimeEnd;

		// in or not in fields
		@QueryField(operator = "in", field = "type")
		public int[] typeInList;

		@QueryField(operator = "not in", field = "type")
		public int[] typeNotInList;

//        @QueryField(operator="in",field="projectId")
//        public int[] projectIdInList;

		// ForeignQueryFields
		@QueryField(foreignKeyFields = "taskId", field = "name")
		public String taskName;

		@QueryField(foreignKeyFields = "projectId", field = "name")
		public String projectName;

		@QueryField(foreignKeyFields = "createAccountId", field = "name")
		public String createAccountName;

		// inner joins
		// sort
		public int idSort;
		public int companyIdSort;
		public int projectIdSort;
		public int taskIdSort;
		public int taskNameSort;
		public int typeSort;
		public int itemsSort;
		public int remarkSort;
		public int createAccountIdSort;
		public int createAccountNameSort;
		public int createAccountImageIdSort;
		public int createTimeSort;
		public int updateTimeSort;
	}

}