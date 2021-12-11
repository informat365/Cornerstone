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
import jazmin.driver.jdbc.smartjdbc.annotations.InnerJoin;
import jazmin.driver.jdbc.smartjdbc.annotations.InnerJoins;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 企业成员
 * 
 * @author 杜展扬 2018-07-28
 *
 */
@DomainDefine(domainClass = CompanyMember.class)
@DomainDefineValid(comment = "企业成员", uniqueKeys = { @UniqueKey(fields = { "companyId", "accountId" }) })
public class CompanyMember extends BaseDomain {
	//
	//
	@ForeignKey(domainClass = Company.class)
	@DomainFieldValid(comment = "企业", canUpdate = true)
	public int companyId;

	@ForeignKey(domainClass = Account.class)
	@DomainFieldValid(comment = "用户", canUpdate = true)
	public int accountId;

	@DomainFieldValid(comment = "部门列表", canUpdate = true)
	public List<DepartmentSimpleInfo> departmentList;

	//
	//
	public static class CompanyMemberInfo extends CompanyMember {
		//
		@NonPersistent
		@DomainField(foreignKeyFields = "accountId", field = "imageId")
		@DomainFieldValid(comment = "用户头像")
		public String accountImageId;

		@NonPersistent
		@DomainField(foreignKeyFields = "accountId", field = "userName")
		@DomainFieldValid(comment = "用户用户名")
		public String accountUserName;
		
		@NonPersistent
		@DomainField(foreignKeyFields = "accountId", field = "mobileNo")
		@DomainFieldValid(comment = "用户手机号")
		public String accountMobileNo;
		
		@NonPersistent
		@DomainField(foreignKeyFields = "accountId", field = "email")
		@DomainFieldValid(comment = "用户邮箱")
		public String accountEmail;

		@NonPersistent
		@DomainField(foreignKeyFields = "accountId", field = "name")
		@DomainFieldValid(comment = "用户名称")
		public String accountName;
		
		@NonPersistent
		@DomainField(foreignKeyFields = "accountId", field = "status")
		@DomainFieldValid(comment = "用户状态")
		public int accountStatus;
		
		@NonPersistent
		@DomainField(foreignKeyFields = "accountId")
		@DomainFieldValid(comment = "是否需要强制设置密码")
		public boolean needUpdatePassword;
		
		@NonPersistent
		@DomainField(foreignKeyFields = "accountId", field = "wxOpenId")
		@DomainFieldValid(comment = "用户微信openId")
		public String accountWxOpenId;
		
		@NonPersistent
		@DomainField(foreignKeyFields = "accountId")
		@DomainFieldValid(comment = "每日登陆失败次数")
		public int dailyLoginFailCount;
//		
		@NonPersistent
		@DomainField(ignoreWhenSelect=true)
		public List<RoleInfo> roleList;
	}

	//
	//
	@QueryDefine(domainClass = CompanyMemberInfo.class)
	public static class CompanyMemberQuery extends BizQuery {
		//
		public Integer id;

		public Integer companyId;

		public Integer accountId;

		@QueryField(field = "accountId")
		public int[] accountIdInList;

		public String departmentList;

		@QueryField(operator = ">=", field = "createTime")
		public Date createTimeStart;

		@QueryField(operator = "<=", field = "createTime")
		public Date createTimeEnd;

		@QueryField(operator = ">=", field = "updateTime")
		public Date updateTimeStart;

		@QueryField(operator = "<=", field = "updateTime")
		public Date updateTimeEnd;

		// in or not in fields

		// ForeignQueryFields
		@QueryField(foreignKeyFields = "accountId", field = "imageId")
		public String accountImageId;

		@QueryField(foreignKeyFields = "accountId", field = "userName")
		public String accountUserName;

		@QueryField(foreignKeyFields = "accountId", field = "name")
		public String accountName;
		
		@QueryField(foreignKeyFields = "accountId", field = "status")
		public Integer accountStatus;

		// inner joins
		//member.account_id= department.account_id and member.cid = department.cid where department.parent_id = departid
		//@QueryField(whereSql="JSON_SEARCH(a.department_list, 'all', \"${departmentId}\", NULL, '$[*].id') is not null")
		@InnerJoin(table1Field="accountId",table2=Department.class,table2Field="accountId",
	        		table1Fields={"companyId"},table2Fields= {"companyId"})
		@QueryField(field="parentId")
		public Integer departmentId;
		
		@InnerJoin(table1Field="accountId",table2=Department.class,table2Field="accountId",
        		table1Fields={"companyId"},table2Fields= {"companyId"})
		@QueryField(field="parentId")
		public int[] departmentIds;//多部门
		
		@InnerJoins(innerJoins= {
			@InnerJoin(table1Field="accountId",table2=CompanyMemberRole.class,table2Field="accountId",
	        		table1Fields={"companyId"},table2Fields= {"companyId"}),
			@InnerJoin(table1Field="roleId",table2=Role.class)
		})
		@QueryField(field="name")
		public String roleName;
		// sort
		public int idSort;
		public int companyIdSort;
		public int accountIdSort;
		public int accountImageIdSort;
		public int accountUserNameSort;
		public int accountNameSort;
		public int departmentListSort;
		public int createTimeSort;
		public int updateTimeSort;
	}

}