package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cornerstone.biz.domain.Remind.RemindInfo;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.domain.query.BizQuery;

/**
 * 
 * @author cs
 *
 */
public class TaskCalendarInfo {

	public List<TaskInfo> taskList;
	
	public List<RemindInfo> remindList;
	//
	public TaskCalendarInfo() {
		taskList=new ArrayList<>();
		remindList=new ArrayList<>();
	}
	//
	public static class CalendarInfoQuery extends BizQuery{
	    public Date monthDate;
    }
}
