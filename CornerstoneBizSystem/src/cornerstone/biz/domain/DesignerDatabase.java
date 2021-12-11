package cornerstone.biz.domain;

import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;

/**
 * 
 * @author cs
 *
 */
@DomainDefine(domainClass = DesignerDatabase.class)
@DomainDefineValid(comment = "数据库", uniqueKeys = { @UniqueKey(fields = { "name" }) })
public class DesignerDatabase extends BaseDomain {
	//
	public static final int DB_TYPE_MYSQL = 1;
	public static final int DB_TYPE_SQLSERVER = 2;
	//
	@ForeignKey(domainClass = Company.class)
	@DomainFieldValid(canUpdate = true, comment = "企业")
	public int companyId;

	@DomainFieldValid(required = true, canUpdate = true, comment = "名称", needTrim = true)
	public String name;

	@DomainFieldValid(canUpdate = true, comment = "数据库名", needTrim = true)
	public String instanceId;

	@DomainFieldValid(required = true, canUpdate = true, comment = "主机", needTrim = true)
	public String host;

	@DomainFieldValid(required = true, canUpdate = true, comment = "端口")
	public int port;

	@DomainFieldValid(required = true, canUpdate = true, comment = "用户名", needTrim = true)
	public String dbUser;

	@DomainFieldValid(canUpdate = true, comment = "密码", needTrim = true)
	public String dbPassword;

	@DomainFieldValid(canUpdate = true, comment = "包名", needTrim = true) // com.lzhbzl
	public String packageName;

	@DomainFieldValid(canUpdate = true, comment = "数据库类型") //
	public int dbType;

	@DomainFieldValid(canUpdate = true, comment = "dml表", needTrim = true) //
	public String dmlTables;

	@DomainFieldValid(comment = "角色列表", canUpdate = true, maxValue = 512)
	public List<Integer> roles;

	@DomainFieldValid(comment = "成员列表", canUpdate = true, maxValue = 512, needTrim = true)
	public String members;

	@ForeignKey(domainClass = Account.class)
	@DomainFieldValid(comment = "创建人", required = true, canUpdate = true)
	public int createAccountId;

	@ForeignKey(domainClass = Account.class)
	@DomainFieldValid(comment = "更新人", required = true, canUpdate = true)
	public int updateAccountId;
}
