package cornerstone.biz.domain;

import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;

/**
 * 
 * @author cs
 *
 */
public class DesignerColumnInfo extends DesignerColumn{

	@DomainField(foreignKeyFields="createAccountId",field="name")
	public String createAccountName;

	@DomainField(foreignKeyFields="updateAccountId",field="name")
	public String updateAccountName;
	
	@DomainField(foreignKeyFields="relationDesignerColumnId",field="columnName")
	public String relationDesignerColumnName;
	
	@DomainField(foreignKeyFields="relationDesignerColumnId",field="domainName")
	public String relationDesignerDomainName;
}
