package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.domain.query.BizQuery;

/**
 * 企业新增任务数量
 * @author cs
 *
 */
public class CompanyTaskNum {
	
	public int companyId; 
	
	public String companyName; 
	
	/**最近一周创建的任务数量*/
	public int num;
	
	/**员工数量*/
	public int memberNum;
	
	/**项目数量*/
	public int projectNum;
	
	/**文件数量*/
	public int fileNum;
	
	/**wikiPage数量*/
	public int wikiPageNum;
	//
	public static class CompanyTaskNumQuery extends BizQuery{
		public Date createTimeStart;
	}
}
