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
 * 迭代阶段
 * 
 * @author 杜展扬 2018-07-31
 *
 */
@DomainDefine(domainClass = ProjectIterationStep.class)
@DomainDefineValid(comment ="迭代阶段" ,uniqueKeys={@UniqueKey(fields={"name","iterationId"})})
public class ProjectIterationStep extends BaseDomain{
    //
	public int companyId;
	
	@ForeignKey(domainClass=Project.class)
	@DomainFieldValid(comment="项目",required=true,canUpdate=true)
	public int projectId;
    //
    @ForeignKey(domainClass=ProjectIteration.class)
    @DomainFieldValid(comment="迭代",required=true,canUpdate=true)
    public int iterationId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="开始时间",required=true,canUpdate=true)
    public Date startDate;
    
    @DomainFieldValid(comment="结束时间",required=true,canUpdate=true)
    public Date endDate;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ProjectIterationStepInfo extends ProjectIterationStep{
    //

    }
    //
    //   
    @QueryDefine(domainClass=ProjectIterationStepInfo.class)
    public static class ProjectIterationStepQuery extends BizQuery{
        //
        public Integer id;

        public Integer iterationId;
        
        public Integer projectId;

        public String name;

        @QueryField(operator=">=",field="startDate")
        public Date startDateStart;
        
        @QueryField(operator="<=",field="startDate")
        public Date startDateEnd;

        @QueryField(operator=">=",field="endDate")
        public Date endDateStart;
        
        @QueryField(operator="<=",field="endDate")
        public Date endDateEnd;

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
        public int iterationIdSort;
        public int projectIdSort;
        public int nameSort;
        public int startDateSort;
        public int endDateSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}