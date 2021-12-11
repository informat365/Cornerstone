package cornerstone.biz.domain;

import java.util.List;
import java.util.Map;

import cornerstone.biz.domain.Account.AccountInfo;
import cornerstone.biz.domain.Project.ProjectInfo;
import cornerstone.biz.domain.Role.RoleInfo;

/**
 * 
 * @author cs
 *
 */
public class AccountProfileInfo {
	
	/***/
	public AccountInfo account;
	
	public Company company;
	
	public List<ProjectInfo> projectList;
	
	/**我的企业角色列表*/
	public List<RoleInfo> roles;
	
	/**我的部门列表*/
	public List<DepartmentSimpleInfo> departmentList;
	
	/***/
	public Map<String,Object> changeLogList;
	
	/**待办任务数量*/
	public int todoTaskNum;
	
	/**创建任务数量*/
	public int createTaskNum;
	
	/**延期任务数量*/
	public int delayTaskNum;
	

}
