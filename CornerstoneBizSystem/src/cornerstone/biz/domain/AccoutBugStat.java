package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.domain.query.BizQuery;

/**
 * 
 * @author cs
 *
 */
public class AccoutBugStat {
	
	public int accountId;
	
	public String accountName;
	
	public int totalTaskNum;
	
	public int totalBugNum;
	
	public int reopenBugNum;
	
	//
	public static class AccoutBugStatQuery extends BizQuery{

		public Integer companyId;
		
		public Integer projectId;
		
		public List<Integer> accountIdList;

		public List<Integer> projectIdList;

		public Integer objectType;
		
		public Boolean isReOpen;
		
		public Date createTimeStart;
		
		public Date createTimeEnd;
	}
}
