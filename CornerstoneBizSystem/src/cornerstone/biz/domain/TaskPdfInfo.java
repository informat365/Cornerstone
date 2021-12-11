package cornerstone.biz.domain;

import java.util.List;

import cornerstone.biz.domain.ChangeLog.ChangeLogInfo;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.Task.TaskDetailInfo;
import cornerstone.biz.domain.TaskComment.TaskCommentInfo;

/**
 * 
 * @author cs
 *
 */
public class TaskPdfInfo {
	
	public TaskDetailInfo task;

	public List<Category> categories;

	public List<TaskCommentInfo> commentList;

	public List<ChangeLogInfo> changeLogList;
	
	public List<ProjectFieldDefine> projectFieldDefineList;
	
	public List<ProjectMemberInfo> memberList;
}
