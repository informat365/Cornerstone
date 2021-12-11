package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.domain.query.BizQuery;

/**
 * 
 * @author cs
 *
 */
public class AccountChangeLogNum {

	public int accountId;
	
	public String accountName;
	
	public String companyName;
	
	public int num;
	//
	public static class AccountChangeLogNumQuery extends BizQuery{
		public Date createTimeStart;
	}
}
