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
 * 仪表盘
 * 
 * @author 杜展扬 2019-01-15 18:03
 *
 */
@DomainDefine(domainClass = Dashboard.class)
@DomainDefineValid(comment ="仪表盘" )
public class Dashboard extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64,needTrim=true)
    public String name;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=false)
    public boolean isDelete;
    
    @DomainFieldValid(comment="分享给别人",required=false,canUpdate=true)
    public List<AccountSimpleInfo> shareAccountList;
    
    @DomainFieldValid(comment="分享给别人ID",required=false,canUpdate=true)
    public List<Integer> shareAccountIdList;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    //
    //   
    public static class DashboardInfo extends Dashboard{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
        
        @DomainField(ignoreWhenSelect=true,field="name",persistent=false)
        @DomainFieldValid(comment = "项目ID")
        public Integer projectId;
    
    }
    //
    //   
    @QueryDefine(domainClass=DashboardInfo.class)
    public static class DashboardQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String name;

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

        //ForeignQueryFields
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        @QueryField(whereSql="(a.create_account_id=${createAccountIdOrshareAccountId} or json_contains(a.share_account_id_list,'${createAccountIdOrshareAccountId}')) ")
        public Integer createAccountIdOrshareAccountId;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nameSort;
        public int isDeleteSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}