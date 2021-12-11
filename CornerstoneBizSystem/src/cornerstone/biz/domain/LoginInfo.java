package cornerstone.biz.domain;

import cornerstone.biz.domain.Account.AccountInfo;
import cornerstone.biz.domain.Company.CompanyInfo;
import cornerstone.biz.domain.Project.ProjectInfo;
import cornerstone.biz.domain.Role.RoleInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author cs
 *
 */
public class LoginInfo {

	public AccountInfo account;
	
	public List<CompanyInfo> companyList;
	
	public List<ProjectInfo> projectList;
	
	public Map<String,List<DataDict>> dataDicts;
	
	public List<AccountMention> mentionList;
	
	public Set<String> permissionList;
	
	/**我的企业角色列表*/
	public List<RoleInfo> roles;
	
	/**我的部门列表*/
	public List<DepartmentSimpleInfo> departmentList;
	
	
	public String owaUrl;//office 预览地址
	
	public int webEventPort;
	//
	public CompanyReportConfig reportConfig;

	/**文件上传大小Mb*/
	public int uploadFileSize;

	/**可访问的项目(仅针对部门boss权限用户)*/
	public Set<Integer> accessProjectList;

	public boolean showMultiUpload;

	/**是否显示新版桌面*/
	public boolean showNewHome;

	/***是否开启全局看板排序功能(爱华)*/
	public boolean globalKanbanSort;
	/**
	 * AD域信息
	 */
    public boolean isAdSet;
	/**
	 * 是否开启供应商功能通商银行)
	 */
	public boolean isSupplierEnable;
	/**
	 * 是否开启钉钉考勤功能(通商银行)
	 */
	public boolean isAttendanceEnable;
	/**
	 * office方案类型 wps（默认）、office、pageoffice、kkfileview
	 */
	public String officeType;

	/**
	 * 自定义项目运行状态值（海亮）
	 */
	public boolean isSpiEnable;

	/**
	 * 项目经理添加成员时是否可以添加为项目管理员角色
	 */
	public boolean isAddMemberLimit;

	/**
	 * 版本库扩展（通商版本新增定制字段，需在公版进行屏蔽）
	 */
	public boolean isRepositoryVersionExtension;


}
