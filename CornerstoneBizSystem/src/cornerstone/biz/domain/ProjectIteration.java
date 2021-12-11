package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.ProjectIterationStep.ProjectIterationStepInfo;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 迭代
 *
 * @author 杜展扬 2018-07-29
 */
@DomainDefine(domainClass = ProjectIteration.class)
@DomainDefineValid(comment = "迭代", uniqueKeys = {@UniqueKey(fields = {"projectId", "name"})})
public class ProjectIteration extends BaseDomain {
    //
    public static final int STATUS_计划中 = 1;
    public static final int STATUS_开发中 = 2;//进行中
    public static final int STATUS_已完成 = 3;

    @DomainFieldValid(comment = "企业", canUpdate = true)
    public int companyId;
    //
    @ForeignKey(domainClass = Project.class)
    @DomainFieldValid(comment = "项目", required = true, canUpdate = true)
    public int projectId;

    @DomainFieldValid(comment = "名称", required = true, canUpdate = true, maxValue = 128)
    public String name;

    @DomainFieldValid(comment = "描述", canUpdate = true, maxValue = 512)
    public String description;

    @DomainFieldValid(comment = "状态", canUpdate = true, dataDict = "ProjectIteration.status")
    public int status;

    @DomainFieldValid(comment = "开始时间", required = true, canUpdate = true)
    public Date startDate;

    @DomainFieldValid(comment = "结束时间", required = true, canUpdate = true)
    public Date endDate;

    @DomainFieldValid(comment = "是否被删除", required = true, canUpdate = true)
    public boolean isDelete;

    @DomainFieldValid(comment = "原始名称，用于删除后恢复", required = false, canUpdate = true, maxValue = 128)
    public String originalName;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", required = true, canUpdate = true)
    public int createAccountId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "更新人", required = true, canUpdate = true)
    public int updateAccountId;

    //
    //   
    public static class ProjectIterationInfo extends ProjectIteration {
        //
        @NonPersistent
        @DomainField(foreignKeyFields = "projectId", field = "companyId")
        @DomainFieldValid(comment = "项目企业")
        public int companyId;

        @NonPersistent
        @DomainField(foreignKeyFields = "projectId", field = "name")
        @DomainFieldValid(comment = "项目名称")
        public String projectName;

        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;

        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
        //
        @DomainField(ignoreWhenSelect = true)
        @NonPersistent
        public List<ProjectIterationStepInfo> stepList;

        @NonPersistent
        @DomainField(foreignKeyFields = "projectId", field = "uuid")
        @DomainFieldValid(comment = "项目UUID")
        public String projectUuid;
    }

    //
    //   
    @QueryDefine(domainClass = ProjectIterationInfo.class)
    public static class ProjectIterationQuery extends BizQuery {
        //
        public Integer id;

        public Integer projectId;

        public String name;

        public String description;

        public Integer status;

        public Boolean isDelete;

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
        @QueryField(operator = "in", field = "projectId")
        public int[] projectIdInList;

        @QueryField(operator = "in", field = "status")
        public int[] statusInList;

        @QueryField(operator = "not in", field = "status")
        public int[] statusNotInList;


        //ForeignQueryFields
        @QueryField(foreignKeyFields = "projectId")
        public Integer companyId;

        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int nameSort;
        public int descriptionSort;
        public int statusSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;

        //
        public ProjectIterationQuery() {
            isDelete = false;
        }
    }

}