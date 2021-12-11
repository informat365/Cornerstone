package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 阶段
 *
 * @author 杜展扬 2018-08-17
 */
@DomainDefine(domainClass = Stage.class)
@DomainDefineValid(comment = "阶段")
public class Stage extends BaseDomain {

    public static final int STATUS_未开始 = 1;
    public static final int STATUS_进行中 = 2;
    public static final int STATUS_已完成 = 3;

    //
    //
    @ForeignKey(domainClass = Company.class)
    @DomainFieldValid(comment = "企业", required = true, canUpdate = true)
    public int companyId;

    @ForeignKey(domainClass = Project.class)
    @DomainFieldValid(comment = "项目", required = true, canUpdate = true)
    public int projectId;

    @ForeignKey(domainClass = Stage.class)
    @DomainFieldValid(comment = "前置阶段", canUpdate = true)
    public int preId;

    @DomainFieldValid(comment = "状态", required = true, canUpdate = true)
    public int status;

    @DomainFieldValid(comment = "工期", canUpdate = true)
    public int workDay;

    @DomainFieldValid(comment = "名称", canUpdate = true, maxValue = 128)
    public String name;

    @DomainFieldValid(comment = "备注", canUpdate = true, maxValue = 256)
    public String remark;

    @DomainFieldValid(comment = "修改备注", canUpdate = true, maxValue = 128)
    public String changeRemark;

    @DomainFieldValid(comment = "开始时间", canUpdate = true)
    public Date startDate;

    @DomainFieldValid(comment = "截止时间", canUpdate = true)
    public Date endDate;

    @DomainFieldValid(comment = "是否删除", canUpdate = true)
    public boolean isDelete;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", canUpdate = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人", canUpdate = true)
    public int updateAccountId;

    public static class StageInfo extends Stage {


        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;


        @NonPersistent
        @DomainField(foreignKeyFields = "projectId", field = "name")
        @DomainFieldValid(comment = "项目名")
        public String projectName;

        @NonPersistent
        @DomainField(foreignKeyFields = "preId", field = "name")
        @DomainFieldValid(comment = "前置阶段名")
        public String preStageName;

        @NonPersistent
        @DomainField(ignoreWhenSelect = true)
        @DomainFieldValid(comment = "阶段日志")
        public List<StageLog.StageLogInfo> logList;

        @NonPersistent
        @DomainField(ignoreWhenSelect = true)
        @DomainFieldValid(comment = "阶段关联对象")
        public List<StageAssociate.StageAssociateInfo> associateList;

    }

    //
    //   
    @QueryDefine(domainClass = StageInfo.class)
    public static class StageQuery extends BizQuery {


        @QueryField(foreignKeyFields = "projectId", field = "name")
        public String projectName;

        @QueryField(foreignKeyFields = "createAccountId", field = "name")
        public String createAccountName;

        @QueryField(operator = "not in ",field = "id")
        public int[] idNotInList;

        @QueryField(operator = "in ",field = "status")
        public int[] statusInList;
        //
        public Integer id;

        public Integer preId;

        public Integer status;

        public Integer companyId;

        public Integer projectId;

        public String name;

        public String remark;

        public Boolean isDelete;

        @QueryField(operator = ">=", field = "startDate")
        public Date startDateStart;

        @QueryField(operator = "<=", field = "startDate")
        public Date startDateEnd;

        @QueryField(operator = ">=", field = "endDate")
        public Date endDateStart;

        @QueryField(operator = "<=", field = "endDate")
        public Date endDateEnd;

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