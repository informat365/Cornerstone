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
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField.OrGroup;
/**
 * 过滤器
 * 
 * @author 杜展扬 2018-08-01
 *
 */
@DomainDefine(domainClass = Filter.class)
@DomainDefineValid(comment ="过滤器" )
public class Filter extends BaseDomain{
    //
	public int companyId;
    //
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="对象类型",required=true,canUpdate=true)
    public int objectType;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="责任人",required=true,canUpdate=true)
    public int ownerAccountId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true)
    public String name;
    
    @DomainFieldValid(comment="过滤条件",canUpdate=true)
    public FilterCondition condition;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class FilterInfo extends Filter{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="ownerAccountId",field="name")
        @DomainFieldValid(comment = "责任人名称")
        public String ownerAccountName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="projectId",field="companyId")
        @DomainFieldValid(comment = "企业")
        public int companyId;
        //
        public FilterInfo() {
        	
        }
        //
        public FilterInfo(int id,String name) {
        		this.id=id;
        		this.name=name;
        }

    }
    //
    //   
    @QueryDefine(domainClass=FilterInfo.class)
    public static class FilterQuery extends BizQuery{
        //
        public Integer id;

        public Integer projectId;

        public Integer objectType;

        public Integer ownerAccountId;

        public String name;

        public String condition;

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
        
        @QueryField(orGroup=@OrGroup(group="orGroup"),field="createAccountId")
        public Integer orCreateAccountId;
        
        @QueryField(orGroup=@OrGroup(group="orGroup"),field="ownerAccountId")
        public Integer orOwnerAccountId;

        //in or not in fields

        //ForeignQueryFields
        @QueryField(foreignKeyFields="ownerAccountId",field="name")
        public String ownerAccountName;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int objectTypeSort;
        public int ownerAccountIdSort;
        public int ownerAccountNameSort;
        public int nameSort;
        public int conditionSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}