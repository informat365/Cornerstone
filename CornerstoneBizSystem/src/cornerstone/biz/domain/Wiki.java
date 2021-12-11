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
 * Wiki
 * 
 * @author 杜展扬 2018-08-16
 *
 */
@DomainDefine(domainClass = Wiki.class)
@DomainDefineValid(comment ="Wiki" ,uniqueKeys={@UniqueKey(fields={"projectId","name"})})
public class Wiki extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
	
	@DomainFieldValid(comment="uuid",required=true,canUpdate=true)
	public String uuid;
	
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=100)
    public String name;
    
    @DomainFieldValid(comment="是否被删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="授权角色",canUpdate=true,maxValue=512)
    public List<Integer> roles;
    
    @DomainFieldValid(comment="是否启用授权角色",canUpdate=true,maxValue=512)
    public boolean enableRole;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class WikiInfo extends Wiki{
    //
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
    @QueryDefine(domainClass=WikiInfo.class)
    public static class WikiQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer projectId;

        @QueryField(operator="in",field="projectId")
        public int[] projectIdInList;

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
        
        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
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