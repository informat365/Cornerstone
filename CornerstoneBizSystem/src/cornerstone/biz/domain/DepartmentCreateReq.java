package cornerstone.biz.domain;

import cornerstone.biz.poi.ExcelCell;

/**
 * 
 * @author cs
 *
 */
public class DepartmentCreateReq {

	@ExcelCell(name="所属部门")
	public String belongDepartmentName;
	
	@ExcelCell(name="部门名称")
	public String name;
	
	@ExcelCell(name="备注")
	public String remark;
	
	@ExcelCell(name="负责人")
	public String owner;
}
