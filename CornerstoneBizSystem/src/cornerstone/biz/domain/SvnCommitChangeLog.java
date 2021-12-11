package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.util.BizUtil;

/**
 * 
 * @author cs
 *
 */
public class SvnCommitChangeLog extends TaskSimpleInfo{

	@DomainFieldValid(comment="作者",canUpdate=true)
	public String author;
	    
	@DomainFieldValid(comment="提交时间",canUpdate=true)
	public String commitTime;
	    
	@DomainFieldValid(comment="提交注释",canUpdate=true)
	public String comment;
	
	@DomainFieldValid(comment="版本库",canUpdate=true)
    public String repo;

    @DomainFieldValid(comment="提交版本",canUpdate=true)
    public String version;
    
	//
	public static SvnCommitChangeLog createSvnCommitLog(TaskInfo bean,ScmCommitLog log) {
		SvnCommitChangeLog info=new SvnCommitChangeLog();
		info.id=bean.id;
		info.uuid=bean.uuid;
		info.name=bean.name;
		info.serialNo=bean.serialNo;
		info.statusName=bean.statusName;
		info.projectId=bean.projectId;
		info.projectName=bean.projectName;
		info.objectType=bean.objectType;
		info.objectTypeName=bean.objectTypeName;
		info.isDelete=bean.isDelete;
		//
		info.author=BizUtil.trim(log.author);
		info.comment=BizUtil.trim(log.comment);
		info.version=BizUtil.trim(log.version);
		info.repo=BizUtil.trim(log.repo);
		info.commitTime=BizUtil.trim(log.commitTime);
		//
		return info;
}
}
