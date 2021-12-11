package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * Pipeline运行
 * 
 * @author 杜展扬 2018-08-09
 *
 */
@DomainDefine(domainClass = ProjectPipelineRunLog.class)
@DomainDefineValid(comment ="Pipeline运行" )
public class ProjectPipelineRunLog extends BaseDomain{
    //
    public static final int STATUS_Pending = 1;
    public static final int STATUS_正在执行 = 2;
    public static final int STATUS_执行成功 = 3;
    public static final int STATUS_执行失败 = 4;
    //
    @ForeignKey(domainClass=Company.class)
    public int companyId;
    
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment = "项目")
    public int projectId;
    
    @ForeignKey(domainClass=ProjectPipeline.class)
    @DomainFieldValid(comment="pipeline",required=true,canUpdate=true)
    public int pipelineId;
    
    @DomainFieldValid(comment="脚本",required=true,canUpdate=true)
    public String script;
    
    @DomainFieldValid(comment="状态",canUpdate=true,dataDict="ProjectPipelineRunLog.status")
    public int status;
    
    @DomainFieldValid(comment="节点",canUpdate=true,maxValue=128)
    public String node;
    
    @DomainFieldValid(comment="stage",canUpdate=true,maxValue=128)
    public String stage;
    
    @DomainFieldValid(comment="阶段",canUpdate=true,maxValue=128)
    public String step;
    
    @DomainFieldValid(comment="参数",canUpdate=true,maxValue=128)
    public String parameter;
    
    @DomainFieldValid(comment="参数KeyValue",canUpdate=true,maxValue=128)
    public Map<String,ParameterValue> parameterMap;
   
    @DomainFieldValid(comment="开始时间",canUpdate=true)
    public Date startTime;
    
    @DomainFieldValid(comment="结束时间",canUpdate=true)
    public Date endTime;
    
    @DomainFieldValid(comment="执行时间",canUpdate=true)
    public String useTime;
    
    @DomainFieldValid(comment="错误信息",canUpdate=true,maxValue=512)
    public String errorMessage;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="步骤信息",canUpdate=true)
    public List<StepRunInfo> stepInfo;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ProjectPipelineRunLogInfo extends ProjectPipelineRunLog{
    //
    		
    	@NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
        
        @NonPersistent
        @DomainField(foreignKeyFields="updateAccountId",field="name")
        @DomainFieldValid(comment = "更新人名称")
        public String updateAccountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="updateAccountId",field="imageId")
        @DomainFieldValid(comment = "更新人头像")
        public String updateAccountImageId;
    }
    //
    public static class StepRunInfo{
    		public String step;
    		public long time;
    		public boolean success;
    }
    //   
    @QueryDefine(domainClass=ProjectPipelineRunLogInfo.class)
    public static class ProjectPipelineRunLogQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer pipelineId;

        public Integer status;

        public String node;

        public String stage;

        public String step;

        @QueryField(operator=">=",field="startTime")
        public Date startTimeStart;
        
        @QueryField(operator="<=",field="startTime")
        public Date startTimeEnd;

        @QueryField(operator=">=",field="endTime")
        public Date endTimeStart;
        
        @QueryField(operator="<=",field="endTime")
        public Date endTimeEnd;

        public String errorMessage;
        
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
        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int pipelineIdSort;
        public int statusSort;
        public int nodeSort;
        public int stageSort;
        public int stepSort;
        public int startTimeSort;
        public int endTimeSort;
        public int errorMessageSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}