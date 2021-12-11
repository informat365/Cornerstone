package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 版本任务
 *
 * @author 杜展扬 2020-04-16 16:32
 */
@DomainDefine(domainClass = CompanyVersionTask.class)
@DomainDefineValid(comment = "版本任务", uniqueKeys = {@UniqueKey(fields = {"versionId", "taskId"})})
public class CompanyVersionTask extends BaseDomain {
    //
    //
    @ForeignKey(domainClass = Company.class)
    @DomainFieldValid(comment = "企业", required = true)
    public int companyId;

    @ForeignKey(domainClass = CompanyVersionRepository.class)
    @DomainFieldValid(comment = "版本库", required = true)
    public int repositoryId;

    @ForeignKey(domainClass = CompanyVersion.class)
    @DomainFieldValid(comment = "名称", required = true, maxValue = 128)
    public int versionId;

    @ForeignKey(domainClass = Task.class)
    @DomainFieldValid(comment = "状态", required = true)
    public int taskId;

    @ForeignKey(domainClass = ObjectType.class)
    @DomainFieldValid(comment = "类型", canUpdate = true, dataDict = "Task.objectType")
    public int objectType;

    @ForeignKey(domainClass = Project.class)
    @DomainFieldValid(comment = "项目", required = true)
    public int projectId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", required = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人", required = true)
    public int updateAccountId;

    //
    //   
    public static class CompanyVersionTaskInfo extends CompanyVersionTask {
        //
        @DomainField(foreignKeyFields = "taskId", field = "status", persistent = false)
        @DomainFieldValid(comment = "状态状态")
        public int taskStatus;
        //
        @DomainField(foreignKeyFields = "taskId", field = "uuid", persistent = false)
        @DomainFieldValid(comment = "任务UUID")
        public String taskUuid;

        @DomainField(foreignKeyFields = "taskId", field = "serialNo", persistent = false)
        @DomainFieldValid(comment = "任务serialNo")
        public String taskSerialNo;
        
        @DomainField(foreignKeyFields = "taskId,status", field = "name", persistent = false)
        @DomainFieldValid(comment = "任务状态")
        public String taskStatusName;

        @DomainField(foreignKeyFields = "taskId", field = "finishTime", persistent = false)
        @DomainFieldValid(comment = "状态状态")
        public Date finishTime;

        @DomainField(foreignKeyFields = "taskId", field = "name", persistent = false)
        @DomainFieldValid(comment = "任务名称")
        public String taskName;

        @DomainField(foreignKeyFields = "objectType", field = "name", persistent = false)
        @DomainFieldValid(comment = "对象类型名称")
        public String objectTypeName;

        @DomainField(foreignKeyFields = "projectId", field = "name", persistent = false)
        @DomainFieldValid(comment = "项目名称")
        public String projectName;

        @DomainField(foreignKeyFields = "createAccountId", field = "imageId", persistent = false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;

        @DomainField(foreignKeyFields = "createAccountId", field = "name", persistent = false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;


    }

    //
    //   
    @QueryDefine(domainClass = CompanyVersionTaskInfo.class)
    public static class CompanyVersionTaskQuery extends BizQuery {
        //
        public Integer id;

        public Integer companyId;

        public Integer repositoryId;

        public Integer versionId;

        public Integer taskId;

        public Integer projectId;

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

        //in or not in fields

        //ForeignQueryFields
        @QueryField(foreignKeyFields = "taskId", field = "status")
        public Integer taskStatus;

        @QueryField(foreignKeyFields = "taskId", field = "name")
        public String taskName;

        @QueryField(foreignKeyFields = "projectId", field = "name")
        public String projectName;

        @QueryField(foreignKeyFields = "createAccountId", field = "imageId")
        public String createAccountImageId;

        @QueryField(foreignKeyFields = "createAccountId", field = "name")
        public String createAccountName;

        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int repositoryIdSort;
        public int versionIdSort;
        public int taskIdSort;
        public int taskStatusSort;
        public int taskNameSort;
        public int projectIdSort;
        public int projectNameSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}