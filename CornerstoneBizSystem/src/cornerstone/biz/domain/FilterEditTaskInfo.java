package cornerstone.biz.domain;

import cornerstone.biz.domain.Category.CategoryInfo;
import cornerstone.biz.domain.ProjectFieldDefine.ProjectFieldDefineInfo;
import cornerstone.biz.domain.ProjectIteration.ProjectIterationInfo;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.ProjectPriorityDefine.ProjectPriorityDefineInfo;
import cornerstone.biz.domain.ProjectRelease.ProjectReleaseInfo;
import cornerstone.biz.domain.ProjectStatusDefine.ProjectStatusDefineInfo;
import cornerstone.biz.domain.ProjectSubSystem.ProjectSubSystemInfo;
import cornerstone.biz.domain.Stage.StageInfo;

import java.util.List;

/**
 * 任务过滤器所需要的信息
 * @author cs
 *
 */
public class FilterEditTaskInfo {

	public List<ProjectMemberInfo> memberList;
	
	public List<ProjectStatusDefineInfo> statusList;
	
	public List<ProjectPriorityDefineInfo> priorityList;

	public List<ProjectReleaseInfo> releaseList;

	public List<ProjectSubSystemInfo> subSystemList;
	
	public List<ProjectFieldDefineInfo> fieldList;//所有字段列表（包含自定义字段）
	
	public List<ProjectIterationInfo> iterationList;
	
	public List<CategoryInfo> categoryList;

	public List<StageInfo> stageList;

}
