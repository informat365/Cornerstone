package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;

/**
 * 项目变更
 */
@DomainDefine(domainClass = TaskAlterationLog.class)
@DomainDefineValid(comment = "项目变更")
public class TaskAlterationLog extends BaseDomain {

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

    @DomainFieldValid(comment = "变更", required = true, canUpdate = true)
    public int alterationId;

    @DomainFieldValid(comment = "审批结果", required = true, canUpdate = true)
    public int status;

    @ForeignKey(domainClass = TaskAlterationDefine.class)
    @DomainFieldValid(comment = "审批结果", required = true, canUpdate = true)
    public int flowStatus;

    @DomainFieldValid(comment = "是否完成", canUpdate = true)
    public boolean isFinish;

    @DomainFieldValid(comment = "备注", canUpdate = true)
    public String remark;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", canUpdate = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人", canUpdate = true)
    public int updateAccountId;

    public static class TaskAlterationLogInfo extends TaskAlterationLog {


        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;


        @NonPersistent
        @DomainField(foreignKeyFields = "projectId", field = "name")
        @DomainFieldValid(comment = "项目名")
        public String projectName;

        @NonPersistent
        @DomainField(foreignKeyFields = "taskId", field = "name")
        @DomainFieldValid(comment = "阶段名")
        public String taskName;

        @NonPersistent
        @DomainField(foreignKeyFields = "flowStatus", field = "name")
        @DomainFieldValid(comment = "流转名称")
        public String flowStatusName;


    }

    //
    //
    @QueryDefine(domainClass = TaskAlterationLogInfo.class)
    public static class TaskAlterationLogQuery extends BizQuery {


        @QueryField(foreignKeyFields = "projectId", field = "name")
        public String projectName;

        @QueryField(foreignKeyFields = "createAccountId", field = "name")
        public String createAccountName;

        @QueryField(operator = "not in ", field = "id")
        public int[] idNotInList;

        @QueryField(operator = "in ", field = "status")
        public int[] statusInList;
        //
        public Integer id;

        public Integer taskId;

        public Integer status;

        public Integer flowStatus;

        public Integer companyId;

        public Integer projectId;

        public Integer alterationId;

        public String name;

        public String remark;

        public Boolean isDelete;


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