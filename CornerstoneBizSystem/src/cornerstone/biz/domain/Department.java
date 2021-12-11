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
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 组织架构
 * 
 * @author 杜展扬 2018-07-30
 *
 */
@DomainDefine(domainClass = Department.class)
@DomainDefineValid(comment ="组织架构" ,uniqueKeys={@UniqueKey(fields={"companyId","name","parentId","type","accountId"})})
public class Department extends BaseDomain{
    //
    public static final int TYPE_组织架构 = 1;
    public static final int TYPE_人员 = 2;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @ForeignKey(domainClass=Department.class)
    @DomainFieldValid(comment="父id",required=true,canUpdate=true)
    public int parentId;
    
    @DomainFieldValid(comment="类型",canUpdate=true,dataDict="Department.type")//从1开始
    public int type;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="用户",required=true,canUpdate=true)
    public int accountId;
    
    @DomainFieldValid(comment="层级",required=true,canUpdate=true)
    public int level;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建用户",canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class DepartmentInfo extends Department{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="name")
        @DomainFieldValid(comment = "用户名称")
        public String accountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="userName")
        @DomainFieldValid(comment = "用户名")
        public String accountUserName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="pinyinName")
        @DomainFieldValid(comment = "用户名")
        public String accountPinyinName;
        
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<AccountSimpleInfo> ownerAccountList;
        
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<Integer> ownerAccountIdList;
    }
    //
    //   
    @QueryDefine(domainClass=DepartmentInfo.class)
    public static class DepartmentQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String name;
        
        @QueryField(field="name",operator="=")
        public String eqName;

        public Integer parentId;

        public Integer type;

        public Integer accountId;
        
        @QueryField(foreignKeyFields="accountId",field="status")
        public Integer accountStatus;

        public Integer level;

        public String remark;

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
        @QueryField(operator="in",field="id")
        public int[] idInList;
        
        @QueryField(operator="in",field="type")
        public int[] typeInList;
        
        @QueryField(operator="not in",field="type")
        public int[] typeNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="accountId",field="name")
        public String accountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nameSort;
        public int parentIdSort;
        public int typeSort;
        public int accountIdSort;
        public int accountNameSort;
        public int levelSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}