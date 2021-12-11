package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 测试用例
 *
 * @author 杜展扬 2018-08-11
 */
@DomainDefine(domainClass = TestPlanTestCase.class)
@DomainDefineValid(comment = "测试用例", uniqueKeys = {@UniqueKey(fields = {"testCaseId", "testPlanId"})})
public class TestPlanTestCase extends BaseDomain {
    //
    public static final int STATUS_未执行 = 1;
    public static final int STATUS_通过 = 2;
    public static final int STATUS_不通过 = 3;
    public static final int STATUS_阻塞 = 4;

    public int companyId;
    //
    @ForeignKey(domainClass = Project.class)
    @DomainFieldValid(comment = "项目", required = true, canUpdate = true)
    public int projectId;

    @ForeignKey(domainClass = Task.class)
    @DomainFieldValid(comment = "测试计划", required = true, canUpdate = true)
    public int testPlanId;

    @ForeignKey(domainClass = Task.class)
    @DomainFieldValid(comment = "测试用例", required = true, canUpdate = true)
    public int testCaseId;

    @DomainFieldValid(comment = "状态", canUpdate = true, dataDict = "TestPlanTestCase.status")
    public int status;

    @DomainFieldValid(comment = "执行时间", canUpdate = true)
    public Date excuteTime;

    @DomainFieldValid(comment = "执行通过时间", canUpdate = true)
    public Date passTime;

    @DomainFieldValid(comment = "执行失败时间", canUpdate = true)
    public Date failTime;

    @DomainFieldValid(comment = "运行次数", canUpdate = true)
    public int runCount;

    @DomainFieldValid(comment = "最后一次跑的时间", canUpdate = true)
    public Date lastRunTime;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "最后一次跑的人", canUpdate = true)
    public int lastRunAccountId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", required = true, canUpdate = true)
    public int createAccountId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "更新人", required = true, canUpdate = true)
    public int updateAccountId;

    //
    //   
    public static class TestPlanTestCaseInfo extends TestPlanTestCase {
        //
        @NonPersistent
        @DomainField(foreignKeyFields = "testCaseId", field = "name")
        @DomainFieldValid(comment = "测试用例名称")
        public String testCaseName;

        @NonPersistent
        @DomainField(foreignKeyFields = "testCaseId", field = "uuid")
        @DomainFieldValid(comment = "测试用例uuid")
        public String uuid;

        @NonPersistent
        @DomainField(foreignKeyFields = "testCaseId")
        @DomainFieldValid(comment = "测试用例serialNo")
        public int serialNo;

        @NonPersistent
        @DomainField(foreignKeyFields = "lastRunAccountId", field = "imageId")
        @DomainFieldValid(comment = "最后一次跑的人头像")
        public String lastRunAccountImageId;

        @NonPersistent
        @DomainField(foreignKeyFields = "lastRunAccountId", field = "name")
        @DomainFieldValid(comment = "最后一次跑的人名称")
        public String lastRunAccountName;

        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;

        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;

        @NonPersistent
        @DomainField(foreignKeyFields = "testCaseId", field = "categoryIdList")
        @DomainFieldValid(comment = "创建人名称")
        public List<Integer> testCaseCategoryIdList;


    }

    //
    //   
    @QueryDefine(domainClass = TestPlanTestCaseInfo.class)
    public static class TestPlanTestCaseQuery extends BizQuery {
        //
        public Integer id;

        public Integer projectId;

        public Integer testPlanId;

        public Integer testCaseId;

        public Integer status;

        @QueryField(operator = ">=", field = "excuteTime")
        public Date excuteTimeStart;

        @QueryField(operator = "<=", field = "excuteTime")
        public Date excuteTimeEnd;

        @QueryField(operator = ">=", field = "passTime")
        public Date passTimeStart;

        @QueryField(operator = "<=", field = "passTime")
        public Date passTimeEnd;

        @QueryField(operator = ">=", field = "failTime")
        public Date failTimeStart;

        @QueryField(operator = "<=", field = "failTime")
        public Date failTimeEnd;

        public Integer runCount;

        @QueryField(operator = ">=", field = "lastRunTime")
        public Date lastRunTimeStart;

        @QueryField(operator = "<=", field = "lastRunTime")
        public Date lastRunTimeEnd;

        public Integer lastRunAccountId;

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
        @QueryField(operator = "in", field = "status")
        public int[] statusInList;

        @QueryField(operator = "not in", field = "status")
        public int[] statusNotInList;


        //ForeignQueryFields
        @QueryField(foreignKeyFields = "lastRunAccountId", field = "imageId")
        public String lastRunAccountImageId;

        @QueryField(foreignKeyFields = "lastRunAccountId", field = "name")
        public String lastRunAccountName;

        @QueryField(foreignKeyFields = "createAccountId", field = "imageId")
        public String createAccountImageId;

        @QueryField(foreignKeyFields = "createAccountId", field = "name")
        public String createAccountName;

        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int testPlanIdSort;
        public int testCaseIdSort;
        public int statusSort;
        public int excuteTimeSort;
        public int passTimeSort;
        public int failTimeSort;
        public int runCountSort;
        public int lastRunTimeSort;
        public int lastRunAccountIdSort;
        public int lastRunAccountImageIdSort;
        public int lastRunAccountNameSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}