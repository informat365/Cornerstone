package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;
/**
 * 项目
 * 
 * @author 杜展扬 2018-07-29
 *
 */
@DomainDefine(domainClass = Project.class)
@DomainDefineValid(comment ="项目" )
public class Project extends BaseDomain{
    //
    public static final int STATUS_运行中 = 1;
    public static final int STATUS_已归档 = 2;
    //
    public static final int RUNSTATUS_进度正常 = 1;
    public static final int RUNSTATUS_存在风险 = 2;
    public static final int RUNSTATUS_进度失控 = 3;
    //
    public static final int ID_项目集模板ID=10001;//这个是模板项目
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="UUID",canUpdate=true,maxValue=64)
    public String uuid;
    
    @DomainFieldValid(comment="模板类型",canUpdate=true)
    public int type;
    
    @DomainFieldValid(comment="名称",canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="拼音简称",canUpdate=true,maxValue=128)
    public String pinyinShortName;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String description;
    
    @DomainFieldValid(comment="状态",canUpdate=true,dataDict="Project.status")
    public int status;
    
    @ForeignKey(domainClass = ProjectStatusDefine.class)
    @DomainFieldValid(comment="工作流",canUpdate=true)//ProjectStatusDefine
    public int workflowStatus;
    
    public String imageId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",canUpdate=true)
    public int createAccountId;
    
    public int updateAccountId;
    
    @ForeignKey(domainClass=ProjectIteration.class)
    @DomainFieldValid(comment="迭代",canUpdate=true)
    public int iterationId;
    
    @DomainFieldValid(comment="是否是模板",required=true,canUpdate=true)
    public boolean isTemplate;
    
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="模板ID",required=true,canUpdate=true)
    public int templateId;
    
    public boolean isPriavteDeploy;
    
    public boolean isDelete;
    
    @Deprecated
    public boolean isIterationBased;//废弃
    
    public String color;
    
    public String group;
    
    public int runStatus;
    
    public int progress;
    
    public String runLogRemark;
    
    public String remark;
    
    @DomainFieldValid(comment="公告",canUpdate=true,maxValue=60*1024)
    public String announcement;
    
    public String activityCount;
    
    @DomainFieldValid(comment="开始日期",canUpdate=true)
    public Date startDate;
    
    @DomainFieldValid(comment="截止日期",canUpdate=true)
    public Date endDate;

    /**
     * 是否完成（项目工作流转到结束状态，项目仅可查看）
     */
    @DomainFieldValid(comment="是否结束",canUpdate=true)
    public boolean isFinish;

    @DomainFieldValid(comment="实际结束日期",canUpdate=true)
    public Date finishDate;
    //
    @DomainFieldValid(comment="责任人",canUpdate=true)
    public List<Integer> ownerAccountIdList;

    @DomainFieldValid(comment="责任人(冗余)",canUpdate=true)
    public List<AccountSimpleInfo> ownerAccountList;
    //
    @DomainFieldValid(comment="分管领导",canUpdate=true)
    public List<Integer> leaderAccountIdList;

    @DomainFieldValid(comment="分管领导(冗余)",canUpdate=true)
    public List<AccountSimpleInfo> leaderAccountList;

    @DomainFieldValid(comment="预计总工时",canUpdate=true)
    public  int expectWorkTime;

    public static class ProjectInfo extends Project{
    		//
        @NonPersistent
        @DomainField(foreignKeyFields="companyId",field="name")
        @DomainFieldValid(comment = "企业名称")
        public String companyName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人")
        public String createAccountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
        
        @NonPersistent
        @DomainField(foreignKeyFields="templateId",field="name")
        @DomainFieldValid(comment = "模板名称")
        public String templateName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="templateId",field="uuid")
        @DomainFieldValid(comment = "模板uuid")
        public String templateUuid;
        
        @DomainField(foreignKeyFields = "workflowStatus",field = "name",persistent = false)
        public String workflowStatusName;
    
        @DomainField(foreignKeyFields = "workflowStatus",field = "type",persistent = false)
        public int workflowStatusType;

        @NonPersistent
        @DomainField(ignoreWhenSelect=true)
        public List<ProjectMemberInfo> memberList; 
        
        @DomainField(persistent=false,foreignKeyFields="iterationId")
        public ProjectIteration iteration;
        
        @DomainField(persistent=false,ignoreWhenSelect=true)
        public boolean star;
        
        @DomainField(persistent=false,ignoreWhenSelect=true)
        public Date lastAccessTime;

        /**
         * 项目集任务对应的serialNo
         */
        @NonPersistent
        @DomainField(ignoreWhenSelect=true)
        public String serialNo;
        /**
         * 项目集任务对应的id
         */
        @NonPersistent
        @DomainField(ignoreWhenSelect=true)
        public int taskId;
        /**
         * 项目集任务对应的uuid
         */
        @NonPersistent
        @DomainField(ignoreWhenSelect=true)
        public String taskUuid;
    }
    //
    //   
    @QueryDefine(domainClass=ProjectInfo.class)
    public static class ProjectQuery extends BizQuery{
        //
        public Integer id;
        
        @QueryField(operator = "!=",field = "id")
        public Integer excludeId;

        public Integer companyId;

        public String uuid;

        public Integer type;

        public String name;
        
        public String group;
        
        @QueryField(whereSql = "a.group is not null")
        public Boolean groupSet;

        public String description;

        public Integer status;
        
        public Integer workflowStatus;

        public Integer createAccountId;
        
        public Integer updateAccountId;
        
        public Integer iterationId;

        public Boolean isTemplate;

        public Boolean isFinish;

        public Boolean isPriavteDeploy;
        
        public Boolean isDelete;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;
        
        @QueryField(operator=">=",field="endDate")
        public Date endDateStart;
        
        @QueryField(operator="<=",field="endDate")
        public Date endDateEnd;

        //in or not in fields
        @QueryField(operator="in",field="id")
        public int[] idInList;
        
        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="companyId",field="name")
        public String companyName;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int companyNameSort;
        public int uuidSort;
        public int typeSort;
        public int nameSort;
        public int descriptionSort;
        public int statusSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createAccountNameSort;
        public int isTemplateSort;
        public int createTimeSort;
        public int updateTimeSort;
        
        //
        @InnerJoin(table2=ProjectMember.class,table2Field="projectId")
        @QueryField(field="accountId")
        public Integer memberAccountId;
    }

}