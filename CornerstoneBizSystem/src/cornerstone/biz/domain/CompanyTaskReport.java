package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

/**
 * 企业任务报告（周报）
 * @author cs
 *
 */
public class CompanyTaskReport {
	//
	public static class TaskAccount{
		public int accountId;
		public String accountName;
		public String accountImageId;
		public int num;
	}
	//
	public static class CompanyTaskReportQuery{
		//
		public Integer departmentId;
		//
		public List<Integer> accountIds;

		public Integer projectId;

		public List<Integer> projectIdInList;
		
		public Date startDate;

		public Date endDate;
		
	}
	//
	public static class ReportTaskInfo {
		public int taskId;
		public String taskUuid;
		public String taskSerialNo;
		public String taskName;
		public String projectName;
		public Date startDate;
		public Date endDate;
		public Date createDate;
		public Date finishDate;
		public int accountId;
		public String accountName;
		public String accountImageId;
		public String objectTypeName;
	}
	//
	/**完成任务数量*/
	public int finishTaskNum;
	
	/**延期任务数量*/
	public int delayTaskNum;
	
	/**新增任务数量*/
	public int createTaskNum;
	
	/**任务完成排行*/
	public List<TaskAccount> finishTaskAccountList;
	
	/**延期任务排行*/
	public List<TaskAccount> delayTaskAccountList;

	/**新增任务排行*/
	public List<TaskAccount> createTaskAccountList;

	/**延期任务列表*/
	public List<ReportTaskInfo> delayTaskList;
	/**完成任务列表*/
	public List<ReportTaskInfo> finishTaskList;
	/**新建任务列表*/
	public List<ReportTaskInfo> createTaskList;
}
