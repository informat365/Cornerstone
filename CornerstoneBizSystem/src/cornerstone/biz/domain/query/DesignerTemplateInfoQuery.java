package cornerstone.biz.domain.query;

import cornerstone.biz.domain.DesignerTemplateInfo;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;

/**
 * 
 * @author cs
 *
 */
@QueryDefine(domainClass=DesignerTemplateInfo.class)
public class DesignerTemplateInfoQuery extends BizQuery{
	
	public String name;
	
	public Integer language;
	
	public String content;
}
