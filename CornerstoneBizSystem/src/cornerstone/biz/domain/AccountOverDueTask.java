package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class AccountOverDueTask {

	public int accountId;
	
	public String accountName;
	
	public int companyId;
	
	public int num; 
	
	//
	public static class AccountProjectOverDueTask{
		public int accountId;
		public int companyId;
		public int projectId;
		public String projectName;
		public int num;
	}
}
