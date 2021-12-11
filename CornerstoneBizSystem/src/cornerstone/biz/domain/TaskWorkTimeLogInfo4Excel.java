package cornerstone.biz.domain;

import cornerstone.biz.domain.TaskWorkTimeLog.TaskWorkTimeLogInfo;
import cornerstone.biz.poi.ExcelCell;
import cornerstone.biz.util.DateUtil;

/**
 * 
 * @author cs
 *
 */
public class TaskWorkTimeLogInfo4Excel {

	@ExcelCell(name = "项目名称")
    public String projectName;
	
	@ExcelCell(name = "任务名称")
    public String taskName;

	@ExcelCell(name = "任务序列号")
    public String taskSerialNo;

	@ExcelCell(name = "工时所属人")
	public String createAccountName;

	@ExcelCell(name = "实际工时")
    public String hour;

	@ExcelCell(name = "计划工时")
    public String expectWorkTime;

	@ExcelCell(name = "工时描述")
    public String content;

	@ExcelCell(name = "录入时间")
    public String createTime;
	//
	public static TaskWorkTimeLogInfo4Excel create(TaskWorkTimeLogInfo info) {
		TaskWorkTimeLogInfo4Excel bean=new TaskWorkTimeLogInfo4Excel();
		bean.createAccountName=info.createAccountName;
		bean.projectName = info.projectName;
		bean.taskName = info.taskName;
		bean.taskSerialNo ="#"+info.taskSerialNo;
		bean.expectWorkTime = info.expectWorkTime+"";
		bean.hour=info.hour+"";
		bean.content = info.content;
		bean.createTime = DateUtil.formatDate(info.createTime);
		return bean;
	}
}
