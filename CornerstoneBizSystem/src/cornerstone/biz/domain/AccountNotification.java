package cornerstone.biz.domain;

import java.util.Date;

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
 * 通知
 * 
 * @author 杜展扬 2018-08-15
 *
 */
@DomainDefine(domainClass = AccountNotification.class)
@DomainDefineValid(comment ="通知" )
public class AccountNotification extends BaseDomain{
    //
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="用户",required=true,canUpdate=true)
    public int accountId;
    
    @ForeignKey(domainClass=Company.class)
    public int companyId;
    
    @ForeignKey(domainClass=Project.class)
    public int projectId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="操作人",required=true,canUpdate=true)
    public int optAccountId;
    
    @DomainFieldValid(comment = "操作人用户头像")
    public String optAccountImageId;

    @DomainFieldValid(comment = "操作人用户名称")
    public String optAccountName;
    
    @DomainFieldValid(comment="类型",required=true,canUpdate=true)
    public int type;//见 AccountNotificationSetting.java
   
    @DomainFieldValid(comment="关联id")
    public int associatedId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="内容",canUpdate=true)
    public String content;
    
    @DomainFieldValid(comment="预计推送时间",required=true,canUpdate=true)
    public Date expectPushTime;
    
    @DomainFieldValid(comment="web是否推送",required=true,canUpdate=true)
    public boolean isWebSend;
    
    @DomainFieldValid(comment="邮件是否推送",required=true,canUpdate=true)
    public boolean isEmailSend;
    
    @DomainFieldValid(comment="微信是否推送",required=true,canUpdate=true)
    public boolean isWeixinSend;
    
    @DomainFieldValid(comment="飞书是否推送",required=true,canUpdate=true)
    public boolean isLarkSend;
    

    @DomainFieldValid(comment="钉钉是否推送",required=true,canUpdate=true)
    public boolean isDingtalkSend;

    //
    //   
    public static class AccountNotificationInfo extends AccountNotification{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "用户头像")
        public String accountImageId;
    
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="name")
        @DomainFieldValid(comment = "用户名称")
        public String accountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="projectId",field="name")
        @DomainFieldValid(comment = "项目名称")
        public String projectName;
    }
    //
    //   
    @QueryDefine(domainClass=AccountNotificationInfo.class)
    public static class AccountNotificationQuery extends BizQuery{
        //
        public Integer id;

        public Integer accountId;

        public Integer type;
        
        @QueryField(operator="!=",field="type")
        public Integer excludeType;

        public String name;

        public String content;

        @QueryField(operator=">=",field="expectPushTime")
        public Date expectPushTimeStart;
        
        @QueryField(operator="<=",field="expectPushTime")
        public Date expectPushTimeEnd;

        public Boolean isWebSend;

        public Boolean isEmailSend;

        public Boolean isWeixinSend;
        
        public Boolean isLarkSend;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="type")
        public int[] typeInList;
        
        @QueryField(operator="not in",field="type")
        public int[] typeNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="accountId",field="imageId")
        public String accountImageId;
        
        @QueryField(foreignKeyFields="accountId",field="name")
        public String accountName;
        
        //inner joins
        //sort
        public int idSort;
        public int accountIdSort;
        public int accountImageIdSort;
        public int accountNameSort;
        public int typeSort;
        public int nameSort;
        public int contentSort;
        public int expectPushTimeSort;
        public int isWebSendSort;
        public int isEmailSendSort;
        public int isWeixinSendSort;
        public int isLarkSendSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}