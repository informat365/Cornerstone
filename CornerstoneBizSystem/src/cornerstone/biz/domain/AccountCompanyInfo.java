package cornerstone.biz.domain;

import java.util.List;

import cornerstone.biz.domain.Project.ProjectInfo;

/**
 * 
 * @author cs
 *
 */
public class AccountCompanyInfo {

	public int id;//accountId
	
	public String name;
	
	public String userName;    
	    
	public String mobileNo;
	    
	public String email;
	    
	public String imageId;
	
	public int status;
	
	public boolean needUpdatePassword;
	
	public int dailyLoginFailCount;
	
	public List<ProjectInfo> projectList;
	
	public List<Integer> roleList;

	public List<Integer> departmentIdList;
	
	public List<DepartmentSimpleInfo> departmentList;

	public int superBoss;
    public boolean isActivated;

	public String adName;
}
