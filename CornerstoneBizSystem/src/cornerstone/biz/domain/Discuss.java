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
 * 讨论
 * 
 * @author 杜展扬 2019-02-02 20:18
 *
 */
@DomainDefine(domainClass = Discuss.class)
@DomainDefineValid(comment ="讨论" )
public class Discuss extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128,needTrim=true)
    public String name;
    
    @DomainFieldValid(comment="讨论内容",required=true,canUpdate=true,needTrim=true)
    public String content;
    
    @DomainFieldValid(comment="最后一条消息",canUpdate=true)
    public String lastMessage;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="最后回复人名称",required=true,canUpdate=true)
    public int lastMessageAccountId;
    
    @DomainFieldValid(comment = "最后回复时间")
    public Date lastMessageTime;
    
    @DomainFieldValid(comment="回复次数")
    public int replyCount;
    
    @DomainFieldValid(comment="成员列表",canUpdate=true,maxValue=2048)
    public List<Integer> members;
    
    public List<AccountSimpleInfo> memberInfos;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    @DomainFieldValid(comment="是否删除",canUpdate=true)
    public boolean isDelete;
    //
    //   
    public static class DiscussInfo extends Discuss{
    //
        @DomainField(foreignKeyFields="projectId",field="name",persistent=false)
        @DomainFieldValid(comment = "项目名称")
        public String projectName;
    
        @DomainField(foreignKeyFields="lastMessageAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "最后回复人名称头像")
        public String lastMessageImageId;
    
        @DomainField(foreignKeyFields="lastMessageAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "最后回复人名称名称")
        public String lastMessageAccountName;
    
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;

    }
    //
    //   
    @QueryDefine(domainClass=DiscussInfo.class)
    public static class DiscussQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public Integer projectId;

        public String name;

        public String lastMessage;

        public Integer lastMessageAccountId;

        public Integer createAccountId;

        public Integer updateAccountId;
        
        public Boolean isDelete;

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
        
        @QueryField(foreignKeyFields="lastMessageAccountId",field="imageId")
        public String lastMessageImageId;
        
        @QueryField(foreignKeyFields="lastMessageAccountId",field="name")
        public String lastMessageAccountName;
        
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        @QueryField(whereSql="(a.create_account_id=${createAccountOrMember} or json_contains(a.members,'${createAccountOrMember}')) ")
        public Integer createAccountOrMember;//创建人或责任人
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int projectIdSort;
        public int projectNameSort;
        public int nameSort;
        public int lastMessageSort;
        public int lastMessageAccountIdSort;
        public int lastMessageImageIdSort;
        public int lastMessageAccountNameSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}