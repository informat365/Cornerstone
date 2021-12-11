package cornerstone.biz.domain;

import java.util.Date;

import jazmin.driver.jdbc.smartjdbc.Query;

/**
 * 
 * @author cs
 *
 */
public class TaskStatDayData {
	
	public int projectId;

	public int objectType;
	
	public int iterationId;
	
	public Date statDate;
	
	public int status;
	
	public int createAccountId;
	
	public String statusName;
	
	public String statusColor;
	
	public long num;
	//
	public static class TaskStatDayDataQuery extends Query{
	
		public Integer companyId;
		
		public Integer projectId;

		public Integer objectType;
		
		public Integer iterationId;
		
		public Date statDateStart;

		public Date statDateEnd;
		
		public Filter filter;
	}
}
