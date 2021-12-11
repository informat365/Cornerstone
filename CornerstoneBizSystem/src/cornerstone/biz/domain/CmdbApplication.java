package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
 * Applicatio
 * 
 * @author 杜展扬 2018-08-21
 *
 */
@DomainDefine(domainClass = CmdbApplication.class)
@DomainDefineValid(comment ="Application" ,uniqueKeys={@UniqueKey(fields={"companyId","name"})})
public class CmdbApplication extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="依赖项",canUpdate=true,maxValue=512)
    public List<String> depends;
    
    @DomainFieldValid(comment="类型",canUpdate=true,maxValue=64)
    public String type;
    
    @DomainFieldValid(comment="优先级",required=true,canUpdate=true)
    public int priority;
    
    @DomainFieldValid(comment="是否被删除")
    public boolean isDelete;
    
    @DomainFieldValid(comment="分组")
    public String group;
    
    @DomainFieldValid(comment="属性",required=true,canUpdate=true)
    public Map<String,String> properties;
    
    @DomainFieldValid(comment="备注")
    public String remark;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class CmdbApplicationInfo extends CmdbApplication{
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    }
    //
    //   
    @QueryDefine(domainClass=CmdbApplicationInfo.class)
    public static class CmdbApplicationQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String name;

        public String depends;

        public String type;

        public Integer priority;
        
        public String group;

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
        @QueryField(foreignKeyFields="projectId",field="name")
        public String projectName;
        
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int projectNameSort;
        public int nameSort;
        public int dependsSort;
        public int typeSort;
        public int prioritySort;
        public int groupSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}