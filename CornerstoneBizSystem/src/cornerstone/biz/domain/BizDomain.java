package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainFieldValid;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;

/**
 * 
 * @author cs
 *
 */
public class BizDomain extends BaseDomain {

	@DomainFieldValid(comment="创建人")
	@ForeignKey(domainClass = Account.class)
	public int createAccountId;

	@DomainFieldValid(comment="更新人")
	@ForeignKey(domainClass = Account.class)
	public int updateAccountId;
}
