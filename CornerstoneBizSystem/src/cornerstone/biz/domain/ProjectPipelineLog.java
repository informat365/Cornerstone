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
 * PipelineLog
 * 
 * @author 杜展扬 2018-09-05 15:51
 *
 */
@DomainDefine(domainClass = ProjectPipelineLog.class)
@DomainDefineValid(comment ="PipelineLog" )
public class ProjectPipelineLog extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @ForeignKey(domainClass=ProjectPipeline.class)
    @DomainFieldValid(comment="pipelineId",required=true,canUpdate=true)
    public int projectPipelineId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="执行计划",canUpdate=true,maxValue=128)
    public String cron;
    
    @DomainFieldValid(comment="脚本",canUpdate=true)
    public String script;
    
    @DomainFieldValid(comment="执行ID",canUpdate=true)
    public int runId;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ProjectPipelineLogInfo extends ProjectPipelineLog{
    //

    }
    //
    //   
    @QueryDefine(domainClass=ProjectPipelineLogInfo.class)
    public static class ProjectPipelineLogQuery extends BizQuery{
        //
        public Integer id;

        public Integer projectId;

        public Integer projectPipelineId;

        public String name;

        public String cron;

        public String script;

        public Integer runId;

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
        public int projectIdSort;
        public int projectPipelineIdSort;
        public int nameSort;
        public int cronSort;
        public int scriptSort;
        public int runIdSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }
    
    /**
     * 
     * @param old
     * @return
     */
	public static ProjectPipelineLog create(ProjectPipeline old) {
		ProjectPipelineLog bean=new ProjectPipelineLog();
		bean.projectPipelineId=old.id;
		bean.projectId=old.projectId;
		bean.name=old.name;
		bean.script=old.script;
		bean.remark=old.remark;
		bean.runId=old.runId;
		return bean;
	}

}