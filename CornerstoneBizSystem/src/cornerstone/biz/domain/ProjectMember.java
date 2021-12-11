package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.Role.RoleInfo;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 项目成员
 * 
 * @author 杜展扬 2018-07-29
 *
 */
@DomainDefine(domainClass = ProjectMember.class)
@DomainDefineValid(comment ="项目成员" ,uniqueKeys={@UniqueKey(fields={"projectId","accountId"})})
public class ProjectMember extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
	public int companyId;
	
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="用户",required=true,canUpdate=true)
    public int accountId;
    
    @DomainFieldValid(comment="是否星标",required=true,canUpdate=true)
    public boolean star;
    
    @DomainFieldValid(comment="最后访问时间",required=true,canUpdate=true)
    public Date lastAccessTime;
    
    @DomainFieldValid(comment="标签",canUpdate=true,maxValue = 500)
    public List<String> tag;
    
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    //
    //   
    public static class ProjectMemberInfo extends ProjectMember{
    		//
		@NonPersistent
		@DomainField(ignoreWhenSelect=true)
		public List<RoleInfo> roleList;
    		
    	@NonPersistent
        @DomainField(foreignKeyFields="projectId",field="name")
        @DomainFieldValid(comment = "项目名称")
        public String projectName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="name")
        @DomainFieldValid(comment = "用户名称")
        public String accountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="userName")
        @DomainFieldValid(comment = "用户登录名")
        public String accountUserName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="imageId")
        @DomainFieldValid(comment = "用户头像")
        public String accountImageId;
        
        @NonPersistent
		@DomainField(foreignKeyFields = "accountId", field = "status")
		@DomainFieldValid(comment = "用户状态")
		public int accountStatus;
        
        @NonPersistent
        @DomainField(foreignKeyFields="accountId",field="pinyinName")
        @DomainFieldValid(comment = "用户拼音")
        public String accountPinyinName;
    }
    //
    //   
    @QueryDefine(domainClass=ProjectMemberInfo.class)
    public static class ProjectMemberQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer projectId;
        
        @QueryField(foreignKeyFields="projectId",field="status")
        public Integer projectStatus;
        
        @QueryField(foreignKeyFields="projectId,templateId",field="id",operator = "!=")
        public Integer excludeTemplateId;//排除的模板id

        @QueryField(operator="in",field="accountId")
        public int [] accountIdInList;

        public Integer accountId;

        public Boolean star;
        
        public String tag;

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
        public int companyIdSort;
        public int projectIdSort;
        public int accountIdSort;
        public int starSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}