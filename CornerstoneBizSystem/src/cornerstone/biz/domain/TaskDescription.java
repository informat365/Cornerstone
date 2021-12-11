package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 任务详情
 * 
 * @author 杜展扬 2018-08-01
 *
 */
@DomainDefine(domainClass = TaskDescription.class)
@DomainDefineValid(comment ="任务详情" ,uniqueKeys={@UniqueKey(fields={"taskId"})})
public class TaskDescription extends BaseDomain{
    //
    //
	public int companyId;
	
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="任务",required=true,canUpdate=true)
    public int taskId;
    
    @DomainFieldValid(comment="内容",canUpdate=true)
    public String content;
    
    //
    //   
    public static class TaskDescriptionInfo extends TaskDescription{
    //

    }
    //
    //   
    @QueryDefine(domainClass=TaskDescriptionInfo.class)
    public static class TaskDescriptionQuery extends BizQuery{
        //
        public Integer id;

        public Integer taskId;

        public String content;

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
        public int taskIdSort;
        public int contentSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}