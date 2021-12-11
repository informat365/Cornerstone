package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;

/**
 * 
 * @author cs
 *
 */
@DomainDefine(domainClass=DesignerTemplate.class)
@DomainDefineValid(comment = "设计模板")
public class DesignerTemplate extends BaseDomain{

	@DomainFieldValid(canUpdate=true,comment="名称")
	public String name;
	
	@DomainFieldValid(canUpdate=true,comment="语言")
	public int language;
	
	@DomainFieldValid(canUpdate=true,comment="内容")
	public String content;
	
	@ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
}
