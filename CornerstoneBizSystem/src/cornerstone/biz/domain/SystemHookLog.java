package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import cornerstone.biz.systemhook.SystemHookDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 系统钩子日志
 * 
 * @author 杜展扬 2019-06-14 17:01
 *
 */
@DomainDefine(domainClass = SystemHookLog.class)
@DomainDefineValid(comment ="系统钩子日志" )
public class SystemHookLog extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=SystemHook.class)
    @DomainFieldValid(comment="系统钩子",canUpdate=true)
    public int systemHookId;
    
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=100)
    public String name;
    
    @DomainFieldValid(comment="分组",canUpdate=true,maxValue=64)
    public String group;
    
    @DomainFieldValid(comment="项目列表",canUpdate=true,maxValue=60*1024)
    public List<Integer> projectIds;
    
    @DomainFieldValid(comment="类型",dataDict="SystemHook.type")
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
    
    @DomainFieldValid(comment="状态",canUpdate=true)
    public int status;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="脚本",canUpdate=true)
    public String script;
    
    @DomainFieldValid(comment="函数名称列表",canUpdate=true)
    public String functionNames;
    
    @DomainFieldValid(comment="备注",canUpdate=true)
    public String remark;
    
    @DomainFieldValid(comment="js对象",canUpdate=true)
    public SystemHookDefine systemHookDefine;
    
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class SystemHookLogInfo extends SystemHookLog{
    //

    }
    //
    //   
    @QueryDefine(domainClass=SystemHookLogInfo.class)
    public static class SystemHookLogQuery extends BizQuery{
        //
        public Integer id;

        public Integer systemHookId;

        public Integer companyId;

        public String name;

        public String group;

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

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int systemHookIdSort;
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
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}