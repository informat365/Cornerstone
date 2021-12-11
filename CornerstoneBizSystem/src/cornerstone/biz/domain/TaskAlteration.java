package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 项目变更
 */
@DomainDefine(domainClass = TaskAlteration.class)
@DomainDefineValid(comment = "项目变更")
public class TaskAlteration extends BaseDomain {

    public static final int STATUS_待定 = 0;
    public static final int STATUS_通过 = 1;
    public static final int STATUS_驳回 = 2;
    public static final int STATUS_取消 = 3;

    //
    //
    @ForeignKey(domainClass = Company.class)
    @DomainFieldValid(comment = "企业", required = true, canUpdate = true)
    public int companyId;

    @ForeignKey(domainClass = Project.class)
    @DomainFieldValid(comment = "项目", required = true, canUpdate = true)
    public int projectId;

    @ForeignKey(domainClass = Task.class)
    @DomainFieldValid(comment = "变更对象", canUpdate = true)
    public int taskId;

    @ForeignKey(domainClass = ObjectType.class)
    @DomainFieldValid(comment = "对象类型", canUpdate = true)
    public int objectType;

    @DomainFieldValid(comment = "结果(通过/驳回/取消)", required = true, canUpdate = true)
    public int status;

    @ForeignKey(domainClass = TaskAlterationDefine.class)
    @DomainFieldValid(comment = "审批流程状态", required = true, canUpdate = true)
    public int flowStatus;

    @DomainFieldValid(comment = "优先级", canUpdate = true)
    public int priority;

    @DomainFieldValid(comment = "变更原因", canUpdate = true)
    public String reason;

    @DomainFieldValid(comment = "变更字段", canUpdate = true)
    public List<TaskAlterationField> fields;

    @DomainFieldValid(comment = "是否删除", canUpdate = true)
    public boolean isDelete;

    @DomainFieldValid(comment = "是否完成", canUpdate = true)
    public boolean isFinish;

    /**
     * 责任人列表(用于新增或编辑任务)
     */
    public List<Integer> ownerIdList;

    /**
     * 责任人列表(用于新增或编辑任务)
     */
    public List<AccountSimpleInfo> ownerAccountList;

    /**
     * 初始责任人
     */
    public List<Integer> firstOwner;

    /**
     * 上一个责任人
     */
    public List<Integer> lastOwner;

    /**
     * 变更时保存任务状态
     */
    public Task.TaskInfo task;


    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", canUpdate = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人", canUpdate = true)
    public int updateAccountId;

    public static class TaskAlterationInfo extends TaskAlteration {


        @NonPersistent
        @DomainField(foreignKeyFields = "projectId", field = "name")
        @DomainFieldValid(comment = "项目名")
        public String projectName;


        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;


        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;


        @NonPersistent
        @DomainField(foreignKeyFields = "taskId", field = "name")
        @DomainFieldValid(comment = "任务名")
        public String taskName;

        @NonPersistent
        @DomainField(foreignKeyFields = "taskId", field = "serialNo")
        @DomainFieldValid(comment = "任务序列号")
        public String serialNo;


        @NonPersistent
        @DomainField(foreignKeyFields = "flowStatus", field = "name")
        @DomainFieldValid(comment = "审批流程状态名")
        public String flowStatusName;


        @NonPersistent
        @DomainField(foreignKeyFields = "flowStatus", field = "color")
        @DomainFieldValid(comment = "审批流程状态颜色")
        public String flowStatusColor;


        @NonPersistent
        @DomainField(foreignKeyFields = "objectType", field = "name")
        @DomainFieldValid(comment = "审批流程状态名")
        public String objectTypeName;


        @NonPersistent
        @DomainField(ignoreWhenSelect = true)
        public List<TaskAlterationLog.TaskAlterationLogInfo> logList;


    }

    //
    //   
    @QueryDefine(domainClass = TaskAlterationInfo.class)
    public static class TaskAlterationQuery extends BizQuery {

        @QueryField(whereSql="json_contains(a.owner_id_list,'${ownerAccountId}')")
        public Integer ownerAccountId;

        @QueryField(whereSql="(a.create_account_id=${ownerAccountOrCreateAccountIdId} or json_contains(a.owner_id_list,'${ownerAccountOrCreateAccountIdId}')) ")
        public Integer ownerAccountOrCreateAccountIdId;//创建人或责任人


        @QueryField(foreignKeyFields = "projectId", field = "name")
        public String projectName;

        @QueryField(foreignKeyFields = "flowStatus", field = "name")
        public String flowStatusName;

        @QueryField(foreignKeyFields = "createAccountId", field = "name")
        public String createAccountName;

        @QueryField(operator = "not in ", field = "id")
        public int[] idNotInList;

        @QueryField(operator = "in ", field = "status")
        public int[] statusInList;

        @QueryField(operator = "in ", field = "projectId")
        public int[] projectIdInList;

        @QueryField(operator = "in ", field = "createAccountId")
        public int[] createAccountIdInList;
        //
        public Integer id;

        public Integer taskId;

        public Integer status;

        public Integer flowStatus;

        public Integer companyId;

        public Integer projectId;

        public String name;

        public String remark;

        public Boolean isDelete;

        public Boolean isFinish;

        public int isFinishSort;

        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //inner joins
        //sort
        public int idSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}