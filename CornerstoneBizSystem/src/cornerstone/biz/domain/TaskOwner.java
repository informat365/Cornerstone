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
 * 任务责任人
 * 
 * @author 杜展扬 2019-06-10 22:55
 *
 */
@DomainDefine(domainClass = TaskOwner.class)
@DomainDefineValid(comment ="任务责任人" ,uniqueKeys={@UniqueKey(fields={"taskId","accountId"})})
public class TaskOwner extends BaseDomain{
    //
	public int companyId;
    //
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="任务",required=true,canUpdate=true)
    public int taskId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="责任人",required=true,canUpdate=true)
    public int accountId;
    
    //
    //   
    public static class TaskOwnerInfo extends TaskOwner{
    //

    }
    //
    //   
    @QueryDefine(domainClass=TaskOwnerInfo.class)
    public static class TaskOwnerQuery extends BizQuery{
        //
        public Integer id;

        public Integer taskId;

        public Integer accountId;

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
        public int accountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}