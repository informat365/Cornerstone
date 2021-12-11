package cornerstone.biz.domain;

import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.poi.ExcelCell;
import cornerstone.biz.util.DateUtil;

/**
 * 成员任务
 * @author cs
 *
 */
public class AccountTask4Excel {

	@ExcelCell(name="项目名称")
    public String projectName;
     
    @ExcelCell(name="类型")
    public String objectTypeName;
    
    @ExcelCell(name = "名称")
    public String taskName;

    @ExcelCell(name = "责任人")
    public String ownerAccountName;
    
    @ExcelCell(name="创建人")
    public String createAccountName;
    
    @ExcelCell(name = "时间")
    public String createTime;
    
    @ExcelCell(name = "状态")
    public String statusName;
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public static AccountTask4Excel create(TaskInfo e) {
		AccountTask4Excel bean=new AccountTask4Excel();
		bean.projectName=e.projectName;
		bean.objectTypeName=e.objectTypeName;
		bean.taskName="#"+e.serialNo+" "+e.name;
		bean.ownerAccountName=Task.getOwnerAccountNames(e);
		bean.createAccountName=e.createAccountName;
		bean.createTime=DateUtil.formatDate(e.createTime, "yyyy-MM-dd");
		bean.statusName=e.statusName;
		return bean;
	}

}
