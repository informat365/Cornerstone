package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;
import java.util.Set;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import cornerstone.biz.systemhook.SystemHookDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 系统钩子
 * 
 * @author 杜展扬 2019-06-14 15:50
 *
 */
@DomainDefine(domainClass = SystemHook.class)
@DomainDefineValid(comment ="系统钩子" )
public class SystemHook extends BaseDomain{
    //
    public static final int STATUS_有效 = 1;
    public static final int STATUS_无效 = 2;
    //
    public static final int TYPE_系统钩子 = 1;
    public static final int TYPE_定时任务 = 2;
    //
    public static final String FUNCTION_RUN_JOB="runJob";//定时任务跑的函数
    //
    public static final String FUNCTION_PRE_CREATE_TASK="preCreateTask";
    public static final String FUNCTION_POST_CREATE_TASK="postCreateTask"; 
    public static final String FUNCTION_PRE_UPDATE_TASK="preUpdateTask";
    public static final String FUNCTION_POST_UPDATE_TASK="postUpdateTask"; 
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=100)
    public String name;
    
    @DomainFieldValid(comment="分组",canUpdate=true,maxValue=64)
    public String group;
    
    @DomainFieldValid(comment="项目列表",canUpdate=true,maxValue=60*1024)
    public List<Integer> projectIds;
    
    @DomainFieldValid(comment="类型",required=true,canUpdate=true,dataDict="SystemHook.type")
    public int type;
    
    @DomainFieldValid(comment="cron",canUpdate=true,maxValue=128)
    public String cron;
    
    @DomainFieldValid(comment="上次执行时间")
    public Date lastRunTime;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="上次执行人")
    public int lastRunAccountId;
    
    @DomainFieldValid(comment="下次自动跑的时间")
    public Date nextRunTime;
    
    @DomainFieldValid(comment="执行次数")
    public int runCount;
    
    @DomainFieldValid(comment="是否是所有项目",canUpdate=true)
    public boolean isAllProject;
    
    @DomainFieldValid(comment="授权角色",canUpdate=true,maxValue=512)
    public List<Integer> roles;
    
    @DomainFieldValid(comment="是否启用授权角色",required=true,canUpdate=true)
    public int enableRole;
    
    @DomainFieldValid(comment="状态",required=true,canUpdate=true,dataDict="Common.status")
    public int status;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="脚本",canUpdate=true)
    public String script;
    
    @DomainFieldValid(comment="函数名称列表",canUpdate=true)
    public Set<String> functionNames;//用Set没问题
    
    @DomainFieldValid(comment="备注",canUpdate=true)
    public String remark;
    
    @DomainFieldValid(comment="js对象",canUpdate=true)
    public SystemHookDefine systemHookDefine;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class SystemHookInfo extends SystemHook{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    
        @DomainField(foreignKeyFields="updateAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "更新人头像")
        public String updateAccountImageId;
    
        @DomainField(foreignKeyFields="updateAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "更新人名称")
        public String updateAccountName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="lastRunAccountId",field="name")
        @DomainFieldValid(comment = "最后执行人")
        public String lastRunAccountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="lastRunAccountId",field="imageId")
        @DomainFieldValid(comment = "最后执行人")
        public String lastRunAccountImageId;

    }
    //
    //   
    @QueryDefine(domainClass=SystemHookInfo.class)
    public static class SystemHookQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String name;

        public String group;
        
        public Integer type;

        public String cron;

        public String roles;

        public Integer enableRole;

        public Integer status;

        public Boolean isDelete;

        public String script;

        public String functionNames;

        public String remark;

        public String systemHookDefine;

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
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        @QueryField(foreignKeyFields="updateAccountId",field="imageId")
        public String updateAccountImageId;
        
        @QueryField(foreignKeyFields="updateAccountId",field="name")
        public String updateAccountName;
        
        @QueryField(whereSql="json_contains(a.function_names,'${functionName}')")
        public String functionName;//函数名
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nameSort;
        public int groupSort;
        public int rolesSort;
        public int enableRoleSort;
        public int statusSort;
        public int isDeleteSort;
        public int scriptSort;
        public int functionNamesSort;
        public int remarkSort;
        public int systemHookDefineSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int updateAccountImageIdSort;
        public int updateAccountNameSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}