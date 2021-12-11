package cornerstone.biz.domain;

import cornerstone.biz.domain.TaskWorkTimeLog.TaskWorkTimeLogInfo;
import cornerstone.biz.poi.ExcelCell;
import cornerstone.biz.util.DateUtil;

/**
 * 成员工时报表
 * @author cs
 *
 */
public class TaskWorkTimeLogMember4Excel {

	@ExcelCell(name = "项目名称")
    public String projectName;
	
	@ExcelCell(name = "任务名称")
    public String taskName;
	
	@ExcelCell(name = "开始时间")
    public String startDate;
	
	@ExcelCell(name = "工时")
    public String hour;
	
	@ExcelCell(name = "内容")
    public String content;
		
	@ExcelCell(name = "创建人")
    public String createAccountName;
	
	//
	public static TaskWorkTimeLogMember4Excel create(TaskWorkTimeLogInfo info) {
		TaskWorkTimeLogMember4Excel bean=new TaskWorkTimeLogMember4Excel();
		bean.projectName=info.projectName;
		bean.taskName="#"+info.taskSerialNo+" "+info.taskName;
		bean.startDate=DateUtil.formatDate(info.startTime,"yyyy-MM-dd");
		bean.hour=info.hour+"小时";
		bean.content=info.content;
		bean.createAccountName=info.createAccountName;
		return bean;
	}
}
