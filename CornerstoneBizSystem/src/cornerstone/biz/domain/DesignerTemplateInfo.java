package cornerstone.biz.domain;

import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;

/**
 * 
 * @author cs
 *
 */
public class DesignerTemplateInfo extends DesignerTemplate{
	
	@DomainField(foreignKeyFields="createAccountId",field="name")
	public String createAccountName;

	@DomainField(foreignKeyFields="updateAccountId",field="name")
	public String updateAccountName;
}
