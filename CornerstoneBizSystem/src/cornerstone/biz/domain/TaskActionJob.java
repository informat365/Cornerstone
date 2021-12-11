package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * TaskAction定时
 * 
 * @author 杜展扬 2019-08-29 19:40
 *
 */
@DomainDefine(domainClass = TaskActionJob.class)
@DomainDefineValid(comment ="TaskAction定时" )
public class TaskActionJob extends BaseDomain{
    //
    //
    @DomainFieldValid(comment="action名称",canUpdate=true,maxValue=64)
    public String actionName;
    
    @DomainFieldValid(comment="是否已经运行",required=true,canUpdate=true)
    public boolean isRun;
    
    @DomainFieldValid(comment="运行时间",canUpdate=true)
    public Date runTime;
    
    @DomainFieldValid(comment="信息",canUpdate=true)
    public String message;
    
    //
    //   
    public static class TaskActionJobInfo extends TaskActionJob{
    //

    }
    //
    //   
    @QueryDefine(domainClass=TaskActionJobInfo.class)
    public static class TaskActionJobQuery extends BizQuery{
        //
        public Integer id;

        public String actionName;

        public Boolean isRun;

        @QueryField(operator=">=",field="runTime")
        public Date runTimeStart;
        
        @QueryField(operator="<=",field="runTime")
        public Date runTimeEnd;

        public String message;

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
        public int actionNameSort;
        public int isRunSort;
        public int runTimeSort;
        public int messageSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}