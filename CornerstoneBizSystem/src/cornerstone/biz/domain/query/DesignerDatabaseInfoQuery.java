package cornerstone.biz.domain.query;

import cornerstone.biz.domain.DesignerDatabaseInfo;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;

/**
 * 
 * @author cs
 *
 */
@QueryDefine(domainClass=DesignerDatabaseInfo.class)
public class DesignerDatabaseInfoQuery extends BizQuery{

	public String name;
	
	public String instanceId;
	
	public String host;
	
	public Integer port;
	
	public String dbUser;
	
	public String dbPassword;
	//
	  public int idSort;
      public int createTimeSort;
      public int updateTimeSort;
}
