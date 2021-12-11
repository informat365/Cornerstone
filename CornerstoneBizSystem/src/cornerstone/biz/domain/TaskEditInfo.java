package cornerstone.biz.domain;

import java.util.List;

import cornerstone.biz.domain.Project.ProjectInfo;
import cornerstone.biz.domain.ProjectFieldDefine.ProjectFieldDefineInfo;
import cornerstone.biz.domain.ProjectIteration.ProjectIterationInfo;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.ProjectPriorityDefine.ProjectPriorityDefineInfo;
import cornerstone.biz.domain.ProjectRelease.ProjectReleaseInfo;
import cornerstone.biz.domain.ProjectStatusDefine.ProjectStatusDefineInfo;
import cornerstone.biz.domain.ProjectSubSystem.ProjectSubSystemInfo;
import cornerstone.biz.domain.Stage.StageInfo;

/**
 * 创建或编辑任务所需要的信息
 * @author cs
 *
 */
public class TaskEditInfo {
	
	public ProjectInfo project;

	public List<ProjectMemberInfo> memberList;
	
	public List<ProjectStatusDefineInfo> statusList;
	
	public List<ProjectPriorityDefineInfo> priorityList;

	public List<ProjectReleaseInfo> releaseList;

	public List<ProjectSubSystemInfo> subSystemList;
	
	public List<ProjectFieldDefineInfo> fieldList;//所有字段列表（包含自定义字段）
	
	public List<ProjectIterationInfo> iterationList;
	
	public List<CategoryNode> categoryNodeList;
	
	public ProjectObjectTypeTemplate objectTypeTemplate;

	public List<StageInfo> stageList;

	public List<CompanyVersionRepository.CompanyVersionRepositoryInfo> repositoryList;
	
}
