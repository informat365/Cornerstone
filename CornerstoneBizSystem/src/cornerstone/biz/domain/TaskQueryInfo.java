package cornerstone.biz.domain;

import java.util.List;

import cornerstone.biz.domain.Category.CategoryInfo;
import cornerstone.biz.domain.Filter.FilterInfo;
import cornerstone.biz.domain.ProjectFieldDefine.ProjectFieldDefineInfo;
import cornerstone.biz.domain.ProjectIteration.ProjectIterationInfo;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.ProjectPriorityDefine.ProjectPriorityDefineInfo;
import cornerstone.biz.domain.ProjectRelease.ProjectReleaseInfo;
import cornerstone.biz.domain.ProjectStatusDefine.ProjectStatusDefineInfo;
import cornerstone.biz.domain.ProjectSubSystem.ProjectSubSystemInfo;

/***
 * 
 * @author cs
 *
 */
public class TaskQueryInfo {

	public Project project;
	
	public List<ProjectIterationInfo> iterationList;
	
	public List<ProjectPriorityDefineInfo> priorityList;
	
	public List<ProjectStatusDefineInfo> statusList;
	
	public List<ProjectFieldDefineInfo> fieldList;//所有字段列表（包含自定义字段）
	
	public List<FilterInfo> filterList;
	
	public List<ProjectMemberInfo> memberList;

	public List<ProjectReleaseInfo> releaseList;
	
	public List<ProjectSubSystemInfo> subSystemList;
	
	public List<CategoryInfo> categoryList;
	
	public boolean isPublic;//可见性
}
