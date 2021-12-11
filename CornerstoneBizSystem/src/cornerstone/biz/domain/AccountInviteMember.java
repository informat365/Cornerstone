package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 账号邀请好友
 * 
 * @author 杜展扬 2019-12-28 19:03
 *
 */
@DomainDefine(domainClass = AccountInviteMember.class)
@DomainDefineValid(comment ="账号邀请好友" ,uniqueKeys={@UniqueKey(fields={"accountId"})})
public class AccountInviteMember extends BaseDomain{
    //
    //
    @DomainFieldValid(comment="账号",required=true,canUpdate=true)
    public int accountId;
    
    @DomainFieldValid(comment="每日发送邮件数量",required=true,canUpdate=true)
    public int dailySendMailNum;
    
    @DomainFieldValid(comment="总共发送邮件数量",required=true,canUpdate=true)
    public int totalSendMailNum;
    
    //
    //   
    public static class AccountInviteMemberInfo extends AccountInviteMember{
    //

    }
    //
    //   
    @QueryDefine(domainClass=AccountInviteMemberInfo.class)
    public static class AccountInviteMemberQuery extends BizQuery{
        //
        public Integer id;

        public Integer accountId;

        public Integer dailySendMailNum;

        public Integer totalSendMailNum;

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
        public int accountIdSort;
        public int dailySendMailNumSort;
        public int totalSendMailNumSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}