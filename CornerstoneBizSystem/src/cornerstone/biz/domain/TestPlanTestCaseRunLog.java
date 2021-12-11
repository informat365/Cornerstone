package cornerstone.biz.domain;

import java.util.Date;

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
 * 测试用例执行记录
 * 
 * @author 杜展扬 2018-08-11
 *
 */
@DomainDefine(domainClass = TestPlanTestCaseRunLog.class)
@DomainDefineValid(comment ="测试用例执行记录" )
public class TestPlanTestCaseRunLog extends BaseDomain{
    //
	public int companyId;
    //
    @ForeignKey(domainClass=TestPlanTestCase.class)
    @DomainFieldValid(comment="测试计划测试用例",required=true,canUpdate=true)
    public int testPlanTestCaseId;
    
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="测试计划",required=true,canUpdate=true)
    public int testPlanId;
    
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="测试用例",required=true,canUpdate=true)
    public int testCaseId;
    
    @DomainFieldValid(comment="状态",required=true,canUpdate=true)
    public int status;
    
    @DomainFieldValid(comment="备注",canUpdate=true)
    public String remark;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    //
    //   
    public static class TestPlanTestCaseRunLogInfo extends TestPlanTestCaseRunLog{
    		//
    		@NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
        
        @DomainField(foreignKeyFields="testPlanId",field="name",persistent = false)
        public String testPlanName;
        
        @DomainField(foreignKeyFields="testPlanId",field="uuid",persistent = false)
        public String testPlanUuid;
    }
    //
    //   
    @QueryDefine(domainClass=TestPlanTestCaseRunLogInfo.class)
    public static class TestPlanTestCaseRunLogQuery extends BizQuery{
        //
        public Integer id;

        public Integer testPlanTestCaseId;

        public Integer testPlanId;

        public Integer testCaseId;

        public Integer status;

        public String remark;

        public Integer createAccountId;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int testPlanTestCaseIdSort;
        public int testPlanIdSort;
        public int testCaseIdSort;
        public int statusSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}