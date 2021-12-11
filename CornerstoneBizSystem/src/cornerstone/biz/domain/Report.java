package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 汇报
 *
 * @author 杜展扬 2019-01-14 17:53
 */
@DomainDefine(domainClass = Report.class)
@DomainDefineValid(comment = "汇报")
public class Report extends BaseDomain {

    public static final int TYPE_汇报=1;
    public static final int TYPE_签审=2;
    //
    public static final int STATUS_待汇报 = 1;
    public static final int STATUS_待审核 = 2;
    public static final int STATUS_已审核 = 3;


    //
    public String uuid;

    public int status;

//    public int type;

    /**
     * 汇报时间 2019年1月或2019年1月5号或2019年1月5号-2019年1月12号
     */
    public String reportTime;
    //
    @ForeignKey(domainClass = ReportTemplate.class)
    @DomainFieldValid(comment = "汇报模板", required = true, canUpdate = true)
    public int reportTemplateId;

    @ForeignKey(domainClass = Company.class)
    @DomainFieldValid(comment = "企业", required = true, canUpdate = true)
    public int companyId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "汇报人", required = true, canUpdate = true)
    public int submitterId;

    @DomainFieldValid(comment = "名称", required = true, canUpdate = true, maxValue = 128)
    public String name;

    @DomainFieldValid(comment = "周期", required = true, canUpdate = true)
    public int period;

    @ForeignKey(domainClass = Project.class)
    @DomainFieldValid(comment = "项目", required = true, canUpdate = true)
    public int projectId;

    @DomainFieldValid(comment = "审核人", canUpdate = true)
    public List<Integer> auditorIds;

    @DomainFieldValid(comment = "审核人", canUpdate = true)
    public List<AccountSimpleInfo> auditorList;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", canUpdate = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人", canUpdate = true)
    public int updateAccountId;


    @DomainFieldValid(comment = "是否删除", canUpdate = true)
    public boolean isDelete;


    //
    //   
    public static class ReportInfo extends Report {
        //
        @NonPersistent
        @DomainField(foreignKeyFields = "companyId", field = "name")
        @DomainFieldValid(comment = "企业名称")
        public String companyName;

        @NonPersistent
        @DomainField(foreignKeyFields = "submitterId", field = "name")
        @DomainFieldValid(comment = "汇报人名称")
        public String submitterName;

        @NonPersistent
        @DomainField(foreignKeyFields = "submitterId", field = "imageId")
        @DomainFieldValid(comment = "汇报人头像")
        public String submitterImageId;

        @NonPersistent
        @DomainField(foreignKeyFields = "projectId", field = "name")
        @DomainFieldValid(comment = "项目名称")
        public String projectName;

        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;

        @NonPersistent
        @DomainField(ignoreWhenSelect = true)
        public List<ReportContent> reportContentList;
    }

    //
    //   
    @QueryDefine(domainClass = ReportInfo.class)
    public static class ReportQuery extends BizQuery {
        public Integer type;

        //
        public String uuid;

        public Integer id;

        @QueryField(operator = "in", field = "projectId")
        public int[] projectIdInList;

        public Boolean isDelete;

        public Integer status;

        @QueryField(operator = "in", field = "status")
        public int[] statusInList;

        public Integer reportTemplateId;

        public Integer companyId;

        public Integer submitterId;

        public String name;

        public Integer period;

        @QueryField(operator = "in", field = "period")
        public int[] periodInList;

        public Integer projectId;

        public String auditorIds;

        public int[] auditorIdInList;

        public String auditorList;

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
        @QueryField(foreignKeyFields = "companyId", field = "name")
        public String companyName;

        @QueryField(foreignKeyFields = "submitterId", field = "name")
        public String submitterName;

        @QueryField(foreignKeyFields = "projectId", field = "name")
        public String projectName;

        @QueryField(foreignKeyFields = "createAccountId", field = "name")
        public String createAccountName;

        @QueryField(whereSql = "json_contains(a.auditor_ids,'${auditorId}')")
        public Integer auditorId;

        @QueryField(whereSql = " ("
                + "submitter_id=#{submitterIdAuditorId} or "
                + "("
                + "	a.status!=" + Report.STATUS_待汇报 + " and json_contains(a.auditor_ids,'${submitterIdAuditorId}') "
                + ")"
                + ")")
        public Integer submitterIdAuditorId;

        @QueryField(operator = "in", field = "submitterId")
        public int[] submitterIdInList;

        //inner joins
        //sort
        public int idSort;
        public int reportTemplateIdSort;
        public int companyIdSort;
        public int companyNameSort;
        public int submitterIdSort;
        public int submitter_nameSort;
        public int nameSort;
        public int periodSort;
        public int projectIdSort;
        public int projectNameSort;
        public int auditorIdsSort;
        public int auditorListSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}