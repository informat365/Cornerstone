package cornerstone.biz.domain;

import java.util.Date;

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
@DomainDefine(domainClass = TaskRemindTime.class)
@DomainDefineValid(comment ="任务提醒时间" )
public class TaskRemindTime extends BaseDomain{
    //
	public int companyId;
    //
	@ForeignKey(domainClass=TaskRemind.class)
    @DomainFieldValid(comment="任务提醒",required=true,canUpdate=true)
    public int taskRemindId;
    
    @DomainFieldValid(comment="提醒时间",required=true,canUpdate=true)
    public Date remindTime;
    
    //
    //   
    public static class TaskRemindTimeInfo extends TaskRemindTime{
    //

    }
    //
    //   
    @QueryDefine(domainClass=TaskRemindTimeInfo.class)
    public static class TaskRemindTimeQuery extends BizQuery{
        //
        public Integer id;

        public Integer taskRemindId;

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
        public int createTimeSort;
        public int updateTimeSort;
    }

}