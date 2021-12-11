package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainFieldValid;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;

/**
 * 
 * @author cs
 *
 */
@DomainDefine(domainClass=DesignerTable.class)
public class DesignerTable extends BaseDomain{
	//
	@DomainFieldValid(comment="数据库id")
	public int designerDatabaseId;
	
	@DomainFieldValid(comment="表名")
	public String name;
	
	@DomainFieldValid(canUpdate=true,comment="显示名称")
	public String displayName;
	
	@DomainFieldValid(canUpdate=true,comment="是否显示详情")
	public boolean isShowDetailPage;
	
	@DomainFieldValid(canUpdate=true,comment="是否可以编辑")
	public boolean isCanUpdate;
	
	@DomainFieldValid(canUpdate=true,comment="列表可选")
	public boolean isShowSelect;
	
	@DomainFieldValid(canUpdate=true,comment="内联")
	public boolean isFormInline;
	
	@DomainFieldValid(canUpdate=true,comment="是否可排序")
	public boolean isSortable;
	
}
