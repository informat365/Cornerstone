package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 企业邀请好友
 * 
 * @author 杜展扬 2018-08-20
 *
 */
@DomainDefine(domainClass = CompanyMemberInvite.class)
@DomainDefineValid(comment ="企业邀请好友" ,uniqueKeys={@UniqueKey(fields={"companyId","accountId","invitedEmail"}),@UniqueKey(fields={"uuid"})})
public class CompanyMemberInvite extends BaseDomain{
    //
    @DomainFieldValid(comment="uuid",required=true,canUpdate=true,maxValue=64)
    public String uuid;
    
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="邀请人",required=true,canUpdate=true)
    public int accountId;
    
    @DomainFieldValid(comment="被邀请人邮箱",required=true,canUpdate=true,maxValue=128)
    public String invitedEmail;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="被邀请人",required=true,canUpdate=true)
    public int invitedAccountId;
    
    @DomainFieldValid(comment="是否已同意",required=true,canUpdate=true)
    public boolean isAgree;
    
    //
    //   
    public static class CompanyMemberInviteInfo extends CompanyMemberInvite{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="name")
        @DomainFieldValid(comment = "邀请人名称")
        public String accountName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="invitedAccountId",field="imageId")
        @DomainFieldValid(comment = "被邀请人头像")
        public String invitedAccountImageId;
    
        @NonPersistent
        @DomainField(foreignKeyFields="invitedAccountId",field="name")
        @DomainFieldValid(comment = "被邀请人名称")
        public String invitedAccountName;
    
    }
    //
    //   
    @QueryDefine(domainClass=CompanyMemberInviteInfo.class)
    public static class CompanyMemberInviteQuery extends BizQuery{
        //
        public Integer id;

        public String uuid;

        public Integer companyId;
        
        public Integer projectId;

        public Integer accountId;

        public String invitedEmail;

        public Integer invitedAccountId;
        
        public Boolean isAgree;

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
        @QueryField(foreignKeyFields="accountId",field="name")
        public String accountName;
        
        @QueryField(foreignKeyFields="invitedAccountId",field="imageId")
        public String invitedAccountImageId;
        
        @QueryField(foreignKeyFields="invitedAccountId",field="name")
        public String invitedAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int uuidSort;
        public int companyIdSort;
        public int accountIdSort;
        public int accountNameSort;
        public int invitedEmailSort;
        public int invitedAccountIdSort;
        public int invitedAccountImageIdSort;
        public int invitedAccountNameSort;
        public int isAgreeSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}