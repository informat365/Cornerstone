package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

/**
 * 企业成员待办
 *
 */
public class CompanyMemberTodoTask {

	public int accountId;
	public String accountName;
	public String accountImageId;
	public Date startDate;
	public Date endDate;
	public int num;
	public List<AccountTodoTask> tasks;

    public static class AccountTodoTask{

    	public int accountId;

		public int companyId;

		public int projectId;

		public String projectName;

		public int taskId;

		public String uuid;

		public String name;

		public String serialNo;

		public Date startDate;

		public Date endDate;

		public boolean isFinish;

		public int objectType;

		public String objectTypeName;

		public int status;

		public String statusName;

		public String statusColor;

		public int priority;

		public String priorityName;

		public String priorityColor;

		public List<CategorySimpleInfo> categoryList;

	}

	public static class CompanyMemberTodoTaskQuery{
		//
		public Integer departmentId;
		//
		public List<Integer> accountIds;

		public Integer projectId;

		public List<Integer> projectIdInList;

		public Date startDate;

		public Date endDate;

	}
}
