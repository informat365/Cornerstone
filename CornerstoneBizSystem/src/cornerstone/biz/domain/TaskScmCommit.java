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
 * 任务关联scm提交记录
 * 
 * @author 杜展扬 2018-08-18
 *
 */
@DomainDefine(domainClass = TaskScmCommit.class)
@DomainDefineValid(comment ="任务关联scm提交记录" ,uniqueKeys={@UniqueKey(fields={"taskId","scmCommitLogId"})})
public class TaskScmCommit extends BaseDomain{
    //
	public int companyId;
    //
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="任务",required=true,canUpdate=true)
    public int taskId;
    
    @ForeignKey(domainClass=ScmCommitLog.class)
    @DomainFieldValid(comment="提交记录录",required=true,canUpdate=true)
    public int scmCommitLogId;
    
    //
    //   
    public static class TaskScmCommitInfo extends TaskScmCommit{
    //

    }
    //
    //   
    @QueryDefine(domainClass=TaskScmCommitInfo.class)
    public static class TaskScmCommitQuery extends BizQuery{
        //
        public Integer id;

        public Integer taskId;

        public Integer scmCommitLogId;

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
        public int scmCommitLogIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}