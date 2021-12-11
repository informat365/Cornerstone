package cornerstone.biz.domain;

import cornerstone.biz.poi.ExcelCell;

import java.util.Date;

/**
 * 
 * 系统导入
 *
 */
public class CreateCompanyVersionRepositoryReq {

	@ExcelCell(name="系统名称")
	public String name;

	@ExcelCell(name="系统经理")
	public String ownerAccountName;

	@ExcelCell(name="开发科室")
	public String ownerDepartmentName;

	@ExcelCell(name="业务经理")
	public String businessLeader;

	@ExcelCell(name="系统主管部门")
	public String department;

	@ExcelCell(name="系统最新版本号")
	public String latest;

	@ExcelCell(name="系统发布时间")
	public String releaseDateStr;

	@ExcelCell(name="系统状态")
	public String statusName;

	@ExcelCell(name="详细描述")
	public String description;

	@ExcelCell(name="是否纳入322N架构体系")
	public String isArch332nName;

	@ExcelCell(name="332N板块分类")
	public String archName;


}
