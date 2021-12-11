package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

import java.util.Date;
/**
 * 任务每日统计
 * 
 * @author 杜展扬 2018-08-18
 *
 */
@DomainDefine(domainClass = TaskDayData.class)
@DomainDefineValid(comment ="任务每日统计" ,uniqueKeys={@UniqueKey(fields={"companyId","projectId","iterationId","objectType","statDate"})})
public class TaskDayData{
    //
	
	@ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
	
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="对象类型",required=true,canUpdate=true)
    public int objectType;//0是汇总
    
    @ForeignKey(domainClass=ProjectIteration.class)
    @DomainFieldValid(comment="迭代",required=true,canUpdate=true)
    public int iterationId;
    
    @DomainFieldValid(comment="统计时间",required=true,canUpdate=true)
    public Date statDate;
    
    @DomainFieldValid(comment="累积数量",required=true,canUpdate=true)
    public long totalNum;//注意：这个totalNum包含那些非statusBase的objectType,所以燃尽图得自己sun
    
    @DomainFieldValid(comment="累积完成数量",required=true,canUpdate=true)//不是今日完成数量
    public long totalFinishNum;
    
    @DomainFieldValid(comment="今日完成数量",required=true,canUpdate=true)
    public long todayFinishNum;
    
    @DomainFieldValid(comment="累积每日提交代码数量",required=true,canUpdate=true)
    public long totalCommitNum;
    
    @DomainFieldValid(comment="今日提交代码数量",required=true,canUpdate=true)
    public long todayCommitNum;

    @DomainFieldValid(comment="累积工作量",required=true,canUpdate=true)
    public double totalLoad;//注意：这个totalNum包含那些非statusBase的objectType,所以燃尽图得自己sun

    @DomainFieldValid(comment="累积完成工作量",required=true,canUpdate=true)//不是今日完成数量
    public double totalFinishLoad;

    @DomainFieldValid(comment="今日完成工作量",required=true,canUpdate=true)
    public double todayFinishLoad;
    
    
    @DomainFieldValid(comment="创建时间")
	public Date createTime;
	
	@DomainFieldValid(comment="更新时间")
	public Date updateTime;
    //
    //   
    public static class TaskDayDataInfo extends TaskDayData{
    
    }
    //
    //   
    @QueryDefine(domainClass=TaskDayDataInfo.class)
    public static class TaskDayDataQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer projectId;

        public Integer objectType;

        public Integer iterationId;

        @QueryField(operator=">=",field="statDate")
        public Date statDateStart;
        
        @QueryField(operator="<=",field="statDate")
        public Date statDateEnd;

        public Integer totalFinishNum;

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
        public int objectTypeSort;
        public int iterationIdSort;
        public int statDateSort;
        public int totalFinishNumSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}