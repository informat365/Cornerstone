package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 任务提醒
 * 
 * @author 杜展扬 2019-01-12 20:17
 *
 */
@DomainDefine(domainClass = TaskRemind.class)
@DomainDefineValid(comment ="任务提醒" )
public class TaskRemind extends BaseDomain{
    //
    //
	@ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
	@ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
	@ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="任务",required=true,canUpdate=true)
    public int taskId;
    
    @DomainFieldValid(comment="事项",canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="规则",canUpdate=true)
    public List<TaskRemindRule> remindRules;
    
    @DomainFieldValid(comment="提醒人",canUpdate=true)
    public List<Integer> remindAccountIdList;
    
    @DomainFieldValid(comment="提醒人",canUpdate=true)
    public List<AccountSimpleInfo> remindAccountList;
    
    @DomainFieldValid(comment="备注",canUpdate=true)
    public String remark;
    
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class TaskRemindInfo extends TaskRemind{
    //
    	
    }
    //
    //   
    @QueryDefine(domainClass=TaskRemindInfo.class)
    public static class TaskRemindQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public Integer projectId;

        public Integer taskId;

        public String name;

        public String remindRules;

        public String remindAccountList;

        public String remark;

        public Integer createAccountId;

        public Integer updateAccountId;

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
        public int companyIdSort;
        public int projectIdSort;
        public int taskIdSort;
        public int nameSort;
        public int remindRulesSort;
        public int remindAccountListSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}