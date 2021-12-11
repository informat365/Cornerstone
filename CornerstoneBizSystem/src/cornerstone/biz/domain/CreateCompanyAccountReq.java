package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

import cornerstone.biz.poi.ExcelCell;

/**
 * 
 * @author cs
 *
 */
public class CreateCompanyAccountReq {

	@ExcelCell(name="姓名")
	public String name;

	@ExcelCell(name="用户名")
	public String userName;

	@ExcelCell(name="手机号码")
	public String mobileNo;

	@ExcelCell(name="邮箱")
	public String email;

	@ExcelCell(name="密码")
	public String password;
	
	@ExcelCell(name="全局角色")
	public String roleNames;
	
	@ExcelCell(name="所属部门")
	public String departmentNames;

	public List<Integer> roleList;

	public List<Integer> departmentList;
	//
	public CreateCompanyAccountReq() {
		roleList=new ArrayList<>();
		departmentList=new ArrayList<>();
	}
}
