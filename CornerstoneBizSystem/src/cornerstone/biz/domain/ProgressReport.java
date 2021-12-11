package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.poi.ExcelCell;

/**
 * 项目或迭代进度报告
 * @author cs
 *
 */
public class ProgressReport {

	@ExcelCell(name="项目名称")
	public String projectName;

	@ExcelCell(name="迭代")
	public String iterationName;
	
	@ExcelCell(name="迭代状态")
	public String strIterationStatus;
	
	@ExcelCell(name="开始时间",dateFormat ="yyyy-MM-dd")
	public Date startDate;
	
	@ExcelCell(name="结束时间",dateFormat ="yyyy-MM-dd")
	public Date endDate;
	
	@ExcelCell(name="完成率")
	public String finishRate;
	
	@ExcelCell(name="延期率")
	public String delayRate;
		
	@ExcelCell(name="总任务数")
	public int totalNum;
	
	@ExcelCell(name="完成任务数")
	public int finishNum;
	
	@ExcelCell(name="延期任务数")
	public int delayNum;
	
	public int iterationStatus;

	public String projectUuid;
}
