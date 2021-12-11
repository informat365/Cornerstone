package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.Attachment.AttachmentInfo;
import cornerstone.biz.domain.TaskAssociated.TaskAssociatedInfo;
import cornerstone.biz.domain.TaskStatusChangeLog.TaskStatusChangeLogInfo;
import cornerstone.biz.domain.TaskWorkTimeLog.TaskWorkTimeLogInfo;
import cornerstone.biz.domain.TestPlanTestCase.TestPlanTestCaseInfo;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * 任务
 * 
 * @author 杜展扬 2018-07-30
 *
 */
@DomainDefine(domainClass = Task.class)
@DomainDefineValid(comment ="任务",uniqueKeys={@UniqueKey(fields={"uuid"}),@UniqueKey(fields={"companyId","serialNo"})})
public class Task extends BaseDomain{
	//
	public static final int OBJECTTYPE_任务 = 1;
    public static final int OBJECTTYPE_缺陷 = 2;
    public static final int OBJECTTYPE_需求 = 3;
    public static final int OBJECTTYPE_测试计划 = 4;
    public static final int OBJECTTYPE_测试用例 = 5;
    public static final int OBJECTTYPE_项目清单 = 1001;
    public static final int OBJECTTYPE_项目督办 = 1002;

    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment = "企业")
    public int companyId;
    
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;

    @ForeignKey(domainClass=ProjectIteration.class)
    @DomainFieldValid(comment="迭代",required=true,canUpdate=true)
    public int iterationId;

    @ForeignKey(domainClass=Stage.class)
    @DomainFieldValid(comment="阶段",required=true,canUpdate=true)
    public int stageId;

    @ForeignKey(domainClass=ObjectType.class)
    @DomainFieldValid(comment="类型",canUpdate=true,dataDict="Task.objectType")
    public int objectType;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="UUID",required=true,canUpdate=true,maxValue=64)
    public String uuid;
    
    @DomainFieldValid(comment="序列号",canUpdate=true)
    public String serialNo;
      
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",canUpdate=true)
    public int updateAccountId;
    
    @ForeignKey(domainClass=ProjectStatusDefine.class)
    @DomainFieldValid(comment="状态",canUpdate=true)
    public int status;
    
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="父任务",canUpdate=true)
    public int parentId;
    
    @ForeignKey(domainClass=ProjectPriorityDefine.class)
    @DomainFieldValid(comment="优先级",canUpdate=true)
    public int priority;
    
    @DomainFieldValid(comment="开始时间",canUpdate=true)
    public Date startDate;
    
    @DomainFieldValid(comment="截止时间",canUpdate=true)
    public Date endDate;
    
    @DomainFieldValid(comment="计划截止时间",canUpdate=true)
    public Date expectEndDate;
    
    @DomainFieldValid(comment="预计工时",canUpdate=true,minValue=0)
    public int expectWorkTime;
    
    @DomainFieldValid(comment="实际工时",canUpdate=true,minValue=0)
    public double workTime;
    
    @DomainFieldValid(comment="是否删除",canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="是否冻结",canUpdate=true)
    public boolean isFreeze;

    @DomainFieldValid(comment="变更ID",canUpdate=true)
    public int alterationId;

    @ForeignKey(domainClass=ProjectRelease.class)
    @DomainFieldValid(comment="关联Release",canUpdate=true)
    public int releaseId;
    
    @ForeignKey(domainClass=ProjectSubSystem.class)
    @DomainFieldValid(comment="子系统",canUpdate=true)
    public int subSystemId;
    
    @ForeignKey(domainClass=TaskDescription.class)
    @DomainFieldValid(comment="详情",canUpdate=true)
    public int taskDescriptionId;
    
    @DomainFieldValid(comment="子任务数量",canUpdate=true)
    public int subTaskCount;
    
    @DomainFieldValid(comment="完成子任务数量",canUpdate=true)
    public int finishSubTaskCount;
    
    @DomainFieldValid(comment="自定义字段",canUpdate=true)
    public Map<String,Object> customFields;
    
    @DomainFieldValid(comment="分类列表",canUpdate=true)
    public List<Integer> categoryIdList;
    
    @DomainFieldValid(comment="已用天数",canUpdate=true)//需要TASK跑now()-startDate
    public int startDays;
    
    @DomainFieldValid(comment="剩余天数",canUpdate=true)//需要TASK跑  endDate-now()
    public int endDays;
    
    @DomainFieldValid(comment="重新打开次数",canUpdate=true)
    public int reopenCount;
    
    @DomainFieldValid(comment="是否完成",canUpdate=true)
    public boolean isFinish;
    
    @DomainFieldValid(comment="完成时间",canUpdate=true)
    public Date finishTime;
    
    /**责任人列表(用于新增或编辑任务)*/
    public List<Integer> ownerAccountIdList;
    
    /**责任人列表(用于新增或编辑任务)*/
    public List<AccountSimpleInfo> ownerAccountList;
    
    /**初始责任人*/
    public List<Integer> firstOwner;
    
    /**上一个状态责任人*/
    public List<Integer> lastOwner;
    
    /**是否建立索引*/
    public boolean isCreateIndex;
    
    /**进度*/
    public int progress;
    
    /**关联项目ID*/
    @ForeignKey(domainClass=Project.class)
    public int associateProjectId;

    @DomainFieldValid(comment="分管领导",canUpdate=true)
    public List<Integer> leaderAccountIdList;

    @DomainFieldValid(comment="分管领导(冗余)",canUpdate=true)
    public List<AccountSimpleInfo> leaderAccountList;

    @DomainFieldValid(comment="工作量",canUpdate=true)
    public double workLoad;

    @DomainFieldValid(comment="关联对象数",canUpdate=true)
    public int associateCount;

    //region
    @ForeignKey(domainClass=CompanyVersion.class)
    @DomainFieldValid(comment="系统版本",required=true,canUpdate=true)
    public int repositoryVersionId;

    @ForeignKey(domainClass=CompanyVersionRepository.class)
    @DomainFieldValid(comment="系统",required=true,canUpdate=true)
    public int repositoryId;
    // end region


    public static class TaskInfo extends Task{

        @DomainField(foreignKeyFields="repositoryId",field="name",persistent=false)
        @DomainFieldValid(comment = "系统名称")
        public String repositoryName;

        @DomainField(foreignKeyFields="projectId",field="name",persistent=false)
        @DomainFieldValid(comment = "项目名称")
        public String projectName;

        @DomainField(foreignKeyFields="stageId",field="name",persistent=false)
        @DomainFieldValid(comment = "阶段名称")
        public String stageName;

        @DomainField(foreignKeyFields="repositoryVersionId",field="name",persistent=false)
        @DomainFieldValid(comment = "系统版本名称")
        public String repositoryVersionName;

        @DomainField(foreignKeyFields="associateProjectId",field="uuid",persistent=false)
        public String associateProjectUuid;
        
        @DomainField(foreignKeyFields="projectId",field="uuid",persistent=false)
        public String projectUuid;
         
        @DomainField(foreignKeyFields="objectType",field="name",persistent=false)
        @DomainFieldValid(comment = "对象类型名称")
        public String objectTypeName;

        @DomainFieldValid(comment = "对象类型系统名称")
        @DomainField(foreignKeyFields = "objectType", field = "systemName", persistent = false)
        public String objectTypeSystemName;

        @DomainField(foreignKeyFields="iterationId",field="name",persistent=false)
        @DomainFieldValid(comment = "迭代名称")
        public String iterationName;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人")
        public String createAccountName;
        
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="status",field="name",persistent=false)
        @DomainFieldValid(comment = "状态")
        public String statusName;
        
        @DomainField(foreignKeyFields="status",field="color",persistent=false)
        @DomainFieldValid(comment = "状态颜色")
        public String statusColor;
        
        @DomainField(foreignKeyFields="status",field="type",persistent=false)
        @DomainFieldValid(comment = "状态类型")
        public int statusType;
    
        @DomainField(foreignKeyFields="priority",field="name",persistent=false)
        @DomainFieldValid(comment = "优先级")
        public String priorityName;
        
        @DomainField(foreignKeyFields="priority",field="color",persistent=false)
        @DomainFieldValid(comment = "优先级颜色")
        public String priorityColor;
        
        @DomainField(foreignKeyFields="priority",field="sortWeight",persistent=false)
        @DomainFieldValid(comment = "优先级排序")
        public String prioritySortWeight;
    
        @DomainField(foreignKeyFields="releaseId",field="name",persistent=false)
        @DomainFieldValid(comment = "关联release")
        public String releaseName;
    
        @DomainField(foreignKeyFields="subSystemId",field="name",persistent=false)
        @DomainFieldValid(comment = "关联子系统")
        public String subSystemName;
     
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<AttachmentInfo> attachmentList;//附件列表

        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<WikiPage.WikiPageInfo> wikiPageList;//关联wiki列表

        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<String> attachmentUuidList;//附件Id列表
        
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<TaskAssociatedInfo> associatedList;//关联对象列表
        
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<Integer> associatedIdList;//关联对象ID列表
        
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<TaskInfo> subTaskList;//子任务列表
        
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<TaskInfo> parentTaskList;//父任务列表
        
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<TaskWorkTimeLogInfo> workTimeLogList;//工时列表
        
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<TestPlanTestCaseInfo> testCaseList;//测试用例列表
        
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<TaskStatusChangeLogInfo> statusChangeLogList;//状态变更记录
        
        @DomainField(ignoreWhenSelect=true,persistent=false)//分类列表
        public List<String> categoryList;

        
    }
    //
    public static class TaskDetailInfo extends TaskInfo{
        @DomainField(foreignKeyFields="taskDescriptionId",field="content",persistent=false)
        @DomainFieldValid(comment = "描述")
        public String content;
    }
    //   
    @QueryDefine(domainClass=TaskInfo.class)
    public static class TaskQuery extends BizQuery{
        //
        public Integer id;

        public Integer alterationId;

        public Integer companyId;

        public Integer projectId;
        
        @QueryField(field ="associateProjectId")
        public int[] associateProjectIdInList;

        @QueryField(field ="projectId")
        public int[] projectIdInList;

        @QueryField(whereSql = "a.object_type not in ("+OBJECTTYPE_项目清单+")")
        public Boolean excludeProjectSetTask;

        public Integer departmentLevel;

        public Integer iterationId;
        
        public Integer releaseId;
        
        public Integer subSystemId;

        public Integer objectType;
        
        @QueryField(foreignKeyFields = "objectType",field = "systemName")
        public String[] excludeObjectTypeSystemNames;

        public String name;

        public String uuid;

        @QueryField(operator="=")
        public String serialNo;

        public Integer createAccountId;
        
        @QueryField(field = "createAccountId")
        public int[] createAccountIdInList;

        public Integer status;

        public Integer parentId;

        public Integer priority;
        
        public Integer reopenCount;
        
        public Boolean isFinish;

        public Boolean isFreeze;

        @QueryField(whereSql=" ( a.end_date is not null and (a.is_finish=false and a.end_date<curdate()) or a.is_finish=true and a.end_date<DATE_FORMAT(a.finish_time,'%Y-%m-%d') )")
        public Boolean isDelay;
        
        public String category;
        
        public Boolean isCreateIndex;

        @QueryField(operator=">=",field="startDate")
        public Date startDateStart;
        
        @QueryField(operator="<=",field="startDate")
        public Date startDateEnd;

        @QueryField(operator=">=",field="endDate")
        public Date endDateStart;

//        @QueryField(whereSql ="( (a.end_date is not null and a.end_date<=#{endDateEnd}) or (a.expect_end_date is not null and a.expect_end_date>=#{expectEndDateStart}))" )
        @QueryField(operator="<=",field="endDate")
        public Date endDateEnd;
        
        @QueryField(whereSql ="( (a.expect_end_date is null and a.end_date>=#{expectEndDateStart}) or (a.expect_end_date is not null and a.expect_end_date>=#{expectEndDateStart}))" )
        public Date expectEndDateStart;
        
        @QueryField(whereSql ="( (a.expect_end_date is null and a.end_date<=#{expectEndDateEnd}) or (a.expect_end_date is not null and a.expect_end_date<=#{expectEndDateEnd}))" )
        public Date expectEndDateEnd;
        
        @QueryField(operator=">=",field="finishTime")
        public Date finishTimeStart;
        
        @QueryField(operator="<=",field="finishTime")
        public Date finishTimeEnd;
        
        @QueryField(operator=">=",field="startDays")
        public Integer startStartDays;
        
        @QueryField(operator="<=",field="startDays")
        public Integer endStartDays;
        
        @QueryField(operator=">=",field="endDays")
        public Integer startEndDays;
        
        @QueryField(operator="<=",field="endDays")
        public Integer endEndDays;

        public Integer expectWorkTime;

        public Integer workTime;

        public Boolean isDelete;

        public Integer subTaskCount;

        @QueryField(ingore=true)
        public Map<String,Object> customFields;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="id")
        public int[] idInList;

        @QueryField(operator="not in",field="id")
        public int[] idNotInList;

        @QueryField(operator="in",field="objectType")
        public int[] objectTypeInList;
        
        @QueryField(operator="not in",field="objectType")
        public int[] objectTypeNotInList;

        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        
        @QueryField(operator="in",field="priority")
        public int[] priorityInList;
        
        @QueryField(operator="not in",field="priority")
        public int[] priorityNotInList;
        
        @QueryField(operator="in",field="releaseId")
        public int[] releaseInList;
        
        @QueryField(operator="in",field="subSystemId")
        public int[] subSystemInList;

        //ForeignQueryFields
        @QueryField(foreignKeyFields="projectId",field="name")
        public String projectName;
        
        @QueryField(foreignKeyFields="iterationId",field="name")
        public String iterationName;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        @QueryField(foreignKeyFields="status",field="name")
        public String statusName;
        
        @QueryField(foreignKeyFields="priority",field="name")
        public String priorityName;
        
        //inner joins
        //sort
        public int contentSort;
        public int projectNameSort;
        public int projectUuidSort;
        public int objectTypeNameSort;
        public int iterationNameSort;
        public int createAccountNameSort;
        public int createAccountImageIdSort;
        public int statusNameSort;
        public int statusColorSort;
        public int priorityNameSort;
        public int priorityColorSort;
        public int releaseNameSort;
        public int subSystemNameSort;
        public int attachmentListSort;
        public int associatedListSort;
        public int subTaskListSort;
        public int workTimeLogListSort;
        public int testCaseListSort;
        public int companyIdSort;
        public int projectIdSort;
        public int iterationIdSort;
        public int objectTypeSort;
        public int nameSort;
        public int uuidSort;
        public int serialNoSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int statusSort;
        public int parentIdSort;
        public int prioritySort;
        public int startDateSort;
        public int endDateSort;
        public int expectWorkTimeSort;
        public int workTimeSort;
        public int isDeleteSort;
        public int releaseIdSort;
        public int subSystemIdSort;
        public int taskDescriptionIdSort;
        public int subTaskCountSort;
        public int finishSubTaskCountSort;
        public int categoryIdListSort;
        public int startDaysSort;
        public int endDaysSort;
        public int reopenCountSort;
        public int isFinishSort;
        public int finishTimeSort;
        public int ownerAccountIdListSort;
        public int ownerAccountListSort;
        public int firstOwnerSort;
        public int lastOwnerSort;
        public int isCreateIndexSort;
        public int idSort;
        public int createTimeSort;
        public int updateTimeSort;
        public int ownerAccountNameSort;
        public int prioritySortWeightSort;
        public int progressSort;
        //
        @QueryField(ingore=true)
        public Map<String,Integer> customFieldsSort;
        //
        @QueryField(whereSql="json_contains(a.owner_account_id_list,'${ownerAccountId}')")
        public Integer ownerAccountId;
        
        @QueryField(whereSql="JSON_LENGTH(a.owner_account_id_list)=0")
        public Boolean ownerAccountIdIsNull;
        
        @QueryField(whereSql="JSON_LENGTH(a.owner_account_id_list)>0")
        public Boolean ownerAccountIdIsNotNull;
        
        @QueryField(whereSql="(a.create_account_id=${ownerAccountOrCreateAccountIdId} or json_contains(a.owner_account_id_list,'${ownerAccountOrCreateAccountIdId}')) ")
        public Integer ownerAccountOrCreateAccountIdId;//创建人或责任人
        
        @InnerJoin(table2=TaskAssociated.class,table2Field="taskId")
        public Integer associatedTaskId;
        
        @InnerJoin(table2=TaskAssociated.class,table2Field="taskId")
        public Integer associatedType;
        
        @QueryField(ingore=true)
        public Integer filterId;
        
        @QueryField(ingore=true)
        public int[] categoryIdList;//这个会查出子分类的
        
        @QueryField(ingore=true)
        public Boolean isSetCategory;//是否设置分类
        
        @QueryField(whereSql="json_contains(a.category_id_list,'${categoryId}')")
        public Integer categoryId;
        
        @QueryField(ingore=true)//责任人列表
        public int[] ownerAccountIdList;
        
        @QueryField(foreignKeyFields="projectId",field="status")
        public Integer projectStatus;
    }
    //
    @QueryDefine(domainClass=TaskDetailInfo.class)
    public static class TaskDetailInfoQuery extends TaskQuery{
    	
    }

    //获取责任人列表，逗号分隔
    public static String getOwnerAccountNames(Task task) {
    	if(task==null||task.ownerAccountList==null||task.ownerAccountList.isEmpty()) {
    		return null;
    	}
    	StringBuilder sb=new StringBuilder();
    	for (AccountSimpleInfo e : task.ownerAccountList) {
			sb.append(e.name);
			sb.append(",");
		}
    	if(sb.length()>0) {
    		sb.deleteCharAt(sb.length()-1);
    	}
    	return sb.toString();
    }
}