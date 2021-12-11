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
@DomainDefine(domainClass=DesignerTemplateChangeLog.class)
@DomainDefineValid(comment = "设计模板变更日志")
public class DesignerTemplateChangeLog extends BaseDomain{

	@DomainFieldValid(comment="设计模板id")
	public int designerTemplateId;
	
	@DomainFieldValid(comment="名称")
	public String name;
	
	@DomainFieldValid(comment="语言")
	public int language;
	
	@DomainFieldValid(comment="内容")
	public String content;
	
	@ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
}
