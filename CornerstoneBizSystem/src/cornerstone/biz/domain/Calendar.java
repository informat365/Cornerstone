package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 日历
 * 
 * @author 杜展扬 2019-12-04 18:39
 *
 */
@DomainDefine(domainClass = Calendar.class)
@DomainDefineValid(comment ="日历" ,uniqueKeys={@UniqueKey(fields={"companyId","name","createAccountId"})})
public class Calendar extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="成员列表",canUpdate=true,maxValue=512)
    public List<Integer> members;
    
    @DomainFieldValid(comment="成员列表详情",canUpdate=true,maxValue=512)
    public List<AccountSimpleInfo> memberInfos;
    
    @DomainFieldValid(comment="是否删除")
    public boolean isDelete;
    
    @DomainFieldValid(comment="是否默认")
    public boolean isDefault;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class CalendarInfo extends Calendar{
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
    @QueryDefine(domainClass=CalendarInfo.class)
    public static class CalendarQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String name;
        
        public Boolean isDelete;
        
        public Boolean isDefault;

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
        
        ///
        @QueryField(whereSql="(a.create_account_id=${createAccountOrMember} or json_contains(a.members,'${createAccountOrMember}')) ")
        public Integer createAccountOrMember;//创建人或成员
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nameSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}