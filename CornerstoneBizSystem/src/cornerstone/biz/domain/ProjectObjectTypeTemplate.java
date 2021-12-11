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
 * 项目对象模板
 * 
 * @author 杜展扬 2018-09-28 15:09
 *
 */
@DomainDefine(domainClass = ProjectObjectTypeTemplate.class)
@DomainDefineValid(comment = "项目对象模板", uniqueKeys = { @UniqueKey(fields = { "projectId", "objectType" }) })
public class ProjectObjectTypeTemplate extends BaseDomain {
	//
	//
	@ForeignKey(domainClass = Company.class)
	@DomainFieldValid(comment = "企业", required = true, canUpdate = true)
	public int companyId;

	@ForeignKey(domainClass = Project.class)
	@DomainFieldValid(comment = "项目", required = true, canUpdate = true)
	public int projectId;

	@ForeignKey(domainClass = ObjectType.class)
	@DomainFieldValid(comment = "对象类型", required = true, canUpdate = true)
	public int objectType;

	@DomainFieldValid(comment = "名称", required = true, canUpdate = true, maxValue = 64)
	public String name;

	@DomainFieldValid(comment = "详情", canUpdate = true)
	public String content;

	@ForeignKey(domainClass = Account.class)
	public int createAccountId;

	@ForeignKey(domainClass = Account.class)
	public int updateAccountId;

	//
	//
	public static class ProjectObjectTypeTemplateInfo extends ProjectObjectTypeTemplate {
		//
		@NonPersistent
		@DomainField(foreignKeyFields = "createAccountId", field = "imageId")
		@DomainFieldValid(comment = "创建人头像")
		public String createAccountImageId;

		@NonPersistent
		@DomainField(foreignKeyFields = "createAccountId", field = "name")
		@DomainFieldValid(comment = "创建人名称")
		public String createAccountName;
	}

	//
	//
	@QueryDefine(domainClass = ProjectObjectTypeTemplateInfo.class)
	public static class ProjectObjectTypeTemplateQuery extends BizQuery {
		//
		public Integer id;

		public Integer companyId;

		public Integer projectId;

		public Integer objectType;

		public String name;

		public String content;

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
		// inner joins
		// sort
		public int idSort;
		public int companyIdSort;
		public int projectIdSort;
		public int objectTypeSort;
		public int nameSort;
		public int contentSort;
		public int createTimeSort;
		public int updateTimeSort;
	}

}