package cornerstone.biz.domain;

import java.util.Date;

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
 * 项目成员角色
 * 
 * @author 杜展扬 2018-07-30
 *
 */
@DomainDefine(domainClass = ProjectMemberRole.class)
@DomainDefineValid(comment ="项目成员角色" ,uniqueKeys={@UniqueKey(fields={"projectMemberId","roleId"})})
public class ProjectMemberRole extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
	public int companyId;
	
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="用户",required=true,canUpdate=true)
    public int accountId;
    
    @ForeignKey(domainClass=ProjectMember.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectMemberId;
    
    @ForeignKey(domainClass=Role.class)
    @DomainFieldValid(comment="角色",required=true,canUpdate=true)
    public int roleId;
    
    //
    //   
    public static class ProjectMemberRoleInfo extends ProjectMemberRole{
    		//
    		@NonPersistent
    		@DomainField(foreignKeyFields="roleId",field="name")
    		@DomainFieldValid(comment = "角色名称")
    		public String roleName;
    }
    //
    //   
    @QueryDefine(domainClass=ProjectMemberRoleInfo.class)
    public static class ProjectMemberRoleQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer projectId;

        public Integer accountId;

        public Integer projectMemberId;

        public Integer roleId;

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
        public int projectMemberIdSort;
        public int roleIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}