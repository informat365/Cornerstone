package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.domain.Task.TaskInfo;

/**
 * 
 * @author cs
 *
 */
public class TaskWeekInfo {

	public Date weekStart;

	public Date weekEnd;
	
	/**本周要完成的任务*/
	public List<TaskInfo> thisWeekTasks;

	/**下周要完成的任务*/
	public List<TaskInfo> nextWeekTasks;
	
	/**已过期的未完成任务*/
	public List<TaskInfo> dueTasks;
}
