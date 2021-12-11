package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;

/**
 * 任务工时记录
 *
 * @author 杜展扬 2018-08-09
 */
@DomainDefine(domainClass = TaskWorkTimeLog.class)
@DomainDefineValid(comment = "任务工时记录")
public class TaskWorkTimeLog extends BaseDomain {
    //
    @ForeignKey(domainClass = Company.class)
    public int companyId;

    @ForeignKey(domainClass = Project.class)
    public int projectId;
    //
    @ForeignKey(domainClass = Task.class)
    @DomainFieldValid(comment = "taskId", canUpdate = true)
    public int taskId;

    @DomainFieldValid(comment = "content", canUpdate = true, maxValue = 512)
    public String content;

    @DomainFieldValid(comment = "开始时间", canUpdate = true)
    public Date startTime;

    @DomainFieldValid(comment = "小时", canUpdate = true)
    public double hour;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", canUpdate = true)
    public int createAccountId;

    //
    //   
    public static class TaskWorkTimeLogInfo extends TaskWorkTimeLog {
        //
        @NonPersistent
        @DomainField(foreignKeyFields = "projectId", field = "name")
        @DomainFieldValid(comment = "项目名称")
        public String projectName;
        //
        @NonPersistent
        @DomainField(foreignKeyFields = "taskId", field = "name")
        @DomainFieldValid(comment = "任务名称")
        public String taskName;

        @NonPersistent
        @DomainField(foreignKeyFields = "taskId", field = "uuid")
        @DomainFieldValid(comment = "任务uuid")
        public String taskUuid;

        @NonPersistent
        @DomainField(foreignKeyFields = "taskId", field = "serialNo")
        @DomainFieldValid(comment = "任务序列号")
        public String taskSerialNo;
        //
        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "name")
        @DomainFieldValid(comment = "创建人")
        public String createAccountName;

        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;

        @NonPersistent
        @DomainField(foreignKeyFields = "taskId", field = "expectWorkTime")
        @DomainFieldValid(comment = "计划工时")
        public int expectWorkTime;

        @NonPersistent
        @DomainField(foreignKeyFields = "taskId", field = "objectType")
        @DomainFieldValid(comment = "对象类型")
        public int objectType;

        @NonPersistent
        @DomainField(ignoreWhenSelect = true)
        @DomainFieldValid(comment = "对象类型名称")
        public String objectTypeName;
    }

    //
    //   
    @QueryDefine(domainClass = TaskWorkTimeLogInfo.class)
    public static class TaskWorkTimeLogQuery extends BizQuery {
        //
        public Integer id;

        public Integer companyId;

        public Integer taskId;

        @QueryField(foreignKeyFields = "taskId", field = "name")
        public String taskName;

        @QueryField(foreignKeyFields = "taskId", field = "objectType")
        public Integer objectType;

        @QueryField(foreignKeyFields = "taskId", field = "isDelete")
        public Boolean isDelete;

        public Integer projectId;

        @QueryField(operator = "in" ,field = "projectId")
        public int[] projectIdInList;

        @QueryField(foreignKeyFields = "projectId", field = "name")
        public String projectName;

        public Integer createAccountId;

        @QueryField(operator = "in",field = "createAccountId")
        public int[] createAccountIdInList;

        @QueryField(foreignKeyFields = "createAccountId", field = "name")
        public String createAccountName;

        public String content;

        @QueryField(operator = ">=", field = "startTime")
        public Date startTimeStart;

        @QueryField(operator = "<=", field = "startTime")
        public Date startTimeEnd;

        public Integer hour;

        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int taskIdSort;
        public int accountIdSort;
        public int contentSort;
        public int startTimeSort;
        public int hourSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}