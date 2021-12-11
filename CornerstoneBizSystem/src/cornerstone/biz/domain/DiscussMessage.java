package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 讨论消息
 * 
 * @author 杜展扬 2019-02-02 20:35
 *
 */
@DomainDefine(domainClass = DiscussMessage.class)
@DomainDefineValid(comment ="讨论消息" )
public class DiscussMessage extends BaseDomain{
    //
	public int companyId;
    //
    @ForeignKey(domainClass=Discuss.class)
    @DomainFieldValid(comment="讨论ID",required=true,canUpdate=true)
    public int discussId;
    
    @ForeignKey(domainClass=DiscussMessage.class)
    @DomainFieldValid(comment="回复消息ID",required=true,canUpdate=true)
    public int replyMessageId;
    
    @DomainFieldValid(comment="内容",required=true,canUpdate=true,needTrim=true)
    public String message;
    
    @DomainFieldValid(comment="附件",canUpdate=true,maxValue=512)
    public List<AttachmentSimpleInfo> attachments;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class DiscussMessageInfo extends DiscussMessage{
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
    @QueryDefine(domainClass=DiscussMessageInfo.class)
    public static class DiscussMessageQuery extends BizQuery{
        //
        public Integer id;

        public Integer discussId;

        public String message;

        public String attachments;

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
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int discussIdSort;
        public int messageSort;
        public int attachmentsSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}