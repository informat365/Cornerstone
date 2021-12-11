package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.SurveysDefine.PermissionRole;
import cornerstone.biz.domain.SurveysDefine.ProjectPermissionRole;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 系统通知
 * 
 * @author 杜展扬 2020-03-05 23:16
 *
 */
@DomainDefine(domainClass = SystemNotification.class)
@DomainDefineValid(comment ="系统通知" )
public class SystemNotification extends BaseDomain{
    //
    public static final int STATUS_有效 = 1;
    public static final int STATUS_无效 = 2;
    //
    public static final int PERIOD_日 = 1;
    public static final int PERIOD_周 = 2;
    public static final int PERIOD_月 = 3;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="状态",required=true,canUpdate=true,dataDict="SystemNotification.status")
    public int status;
    
    @DomainFieldValid(comment="周期",required=true,canUpdate=true)
    public int period;
    
    @DomainFieldValid(comment="周期设置",canUpdate=true,maxValue=256)
    public List<Long> periodSetting;
    
    @DomainFieldValid(comment="提醒时间",canUpdate=true,maxValue=32)
    public String notifyTime;
    
    @DomainFieldValid(comment="下次提醒时间",canUpdate=true)
    public Date nextNotifyTime;
    
    @DomainFieldValid(comment="成员列表",canUpdate=true)
    public List<PermissionRole> accountList;
    
    @DomainFieldValid(comment="企业角色列表",canUpdate=true)
    public List<PermissionRole> companyRoleList;
    
    @DomainFieldValid(comment="项目角色列表",canUpdate=true)
    public List<ProjectPermissionRole> projectRoleList;
    
    @DomainFieldValid(comment="部门列表",canUpdate=true)
    public List<PermissionRole> departmentList;
    
    @DomainFieldValid(comment="通知标题",required=true,canUpdate=true,maxValue=128)
    public String title;
    
    @DomainFieldValid(comment="通知内容",required=true,canUpdate=true,maxValue=10240)
    public String content;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class SystemNotificationInfo extends SystemNotification{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=SystemNotificationInfo.class)
    public static class SystemNotificationQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String name;

        public Integer status;

        public Integer period;

        public String periodSetting;

        public String notifyTime;

        @QueryField(operator=">=",field="nextNotifyTime")
        public Date nextNotifyTimeStart;
        
        @QueryField(operator="<=",field="nextNotifyTime")
        public Date nextNotifyTimeEnd;

        public String title;

        public String content;

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
        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nameSort;
        public int statusSort;
        public int periodSort;
        public int periodSettingSort;
        public int notifyTimeSort;
        public int nextNotifyTimeSort;
        public int titleSort;
        public int contentSort;
        public int isDeleteSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}