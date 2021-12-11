package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.ProjectPipelineRunLog.ProjectPipelineRunLogInfo;
import cornerstone.biz.domain.query.BizQuery;
import cornerstone.biz.pipeline.PipelineDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * Pipeline
 * 
 * @author 杜展扬 2018-08-09
 *
 */
@DomainDefine(domainClass = ProjectPipeline.class)
@DomainDefineValid(comment ="Pipeline",uniqueKeys={@UniqueKey(fields={"projectId","name"})})
public class ProjectPipeline extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
	public int companyId;
    //
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="执行计划",canUpdate=true,maxValue=128)
    public String cron;
    
    @DomainFieldValid(comment="脚本",canUpdate=true)
    public String script;
    
    @ForeignKey(domainClass=ProjectPipelineRunLog.class)
    @DomainFieldValid(comment="执行id",canUpdate=true)
    public int runId;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="是否删除",canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="定义",canUpdate=true)
    public PipelineDefine pipelineDefine;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    @DomainFieldValid(comment="上次执行时间")
    public Date lastRunTime;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="上次执行人")
    public int lastRunAccountId;
    
    @DomainFieldValid(comment="下次自动跑的时间")
    public Date nextRunTime;
    
    @DomainFieldValid(comment="执行次数")
    public int runCount;
    
    @DomainFieldValid(comment="授权角色",canUpdate=true,maxValue=512)
    public List<Integer> roles;
    
    @DomainFieldValid(comment="是否启用授权角色",canUpdate=true,maxValue=512)
    public boolean enableRole;
    
    @DomainFieldValid(comment="分组",canUpdate=true,maxValue=64)
    public String group;
    //
    //   
    public static class ProjectPipelineInfo extends ProjectPipeline{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="projectId",field="name")
        @DomainFieldValid(comment = "项目名称")
        public String projectName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="runId")
        public ProjectPipelineRunLogInfo runLog;
        
        @NonPersistent
        @DomainField(foreignKeyFields="lastRunAccountId",field="name")
        @DomainFieldValid(comment = "最后执行人")
        public String lastRunAccountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="lastRunAccountId",field="imageId")
        @DomainFieldValid(comment = "最后执行人")
        public String lastRunAccountImageId;
        
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人")
        public String createAccountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;

    }
    //
    //   
    @QueryDefine(domainClass=ProjectPipelineInfo.class)
    public static class ProjectPipelineQuery extends BizQuery{
        //
        public Integer id;

        public Integer projectId;

        public Integer companyId;

        public String name;

        public String cron;

        public String script;
        
        public Integer runId;

        public String remark;
        
        public Boolean isDelete;

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
        @QueryField(foreignKeyFields="projectId",field="name")
        public String projectName;
        
        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int projectNameSort;
        public int companyIdSort;
        public int nameSort;
        public int cronSort;
        public int scriptSort;
        public int runIdSort;
        public int remarkSort;
        public int isDeleteSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}