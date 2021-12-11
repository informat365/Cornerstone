package cornerstone.biz.action;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.AccountNotificationSetting;
import cornerstone.biz.domain.Machine;
import cornerstone.biz.domain.ParameterValue;
import cornerstone.biz.domain.PipelineNotification;
import cornerstone.biz.domain.Project;
import cornerstone.biz.domain.ProjectArtifact;
import cornerstone.biz.domain.ProjectPipeline.ProjectPipelineInfo;
import cornerstone.biz.domain.ProjectPipelineRunDetailLog;
import cornerstone.biz.domain.ProjectPipelineRunLog;
import cornerstone.biz.domain.ProjectPipelineRunLog.ProjectPipelineRunLogInfo;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.util.BizUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;

/**
 *
 *PipelineAction(不能暴露给ApiController)
 */
@ApiDefine("Pipeline接口")
public interface PipelineAction {

	@ApiDefine(value = "更新Pipeline执行日志",params = "Pipeline执行日志信息")
	void updateProjectPipelineRunLog(ProjectPipelineRunLog bean);

	@ApiDefine(value = "新增Pipeline执行日志",params = "Pipeline执行日志信息",resp = "执行日志ID")
	int addDetailLog(ProjectPipelineRunDetailLog log);

	@ApiDefine(value = "追加Pipeline执行日志",params = {"日志ID","内容"})
	void appendDetailLogMessage(int id,String message);

	@ApiDefine(value = "查询最新的ProjectPipelineRunLog",params = "日志ID",resp = "Pipeline执行日志信息")
	ProjectPipelineRunLog getProjectPipelineRunLog(int id);
	
	@ApiDefine(value = "查询最新的ProjectPipelineRunLog",params = "日志ID",resp = "Pipeline执行日志信息")
	ProjectPipelineRunLog getProjectPipelineRunLogById(int id);

	@ApiDefine(value = "查询机器",params = {"日志ID","名称"},resp = "机器信息")
	Machine getMachineByRunLogIdName(int runLogId,String name);

	@ApiDefine(value = "获取参数值",params = {"日志ID","参数"},resp = "参数值")
	ParameterValue getParamaterValue(int runLogId,String parameter);

	@ApiDefine(value = "新增项目打包文件",params = {"日志ID","项目打包文件类"})
	void addProjectArtifact(int runLogId,ProjectArtifact bean);

	@ApiDefine(value = "查询项目打包文件",params = {"日志ID","项目打包文件UUID"},resp = "项目打包文件")
	ProjectArtifact getProjectArtifact(int runLogId, String uuid);

	@ApiDefine(value = "发送通知",params = {"日志ID","用户名","内容"})
	void sendNotification(int runLogId, String userName, String content);
	//
	@RpcService
	class PipelineActionImpl implements PipelineAction {
		//
		private static Logger logger=LoggerFactory.get(PipelineActionImpl.class);
		//
		@AutoWired
		BizDAO dao;
		@AutoWired
		BizService service;
		//
		@Transaction
		@Override
		public void updateProjectPipelineRunLog(ProjectPipelineRunLog bean) {
			ProjectPipelineRunLog old=dao.getExistedByIdForUpdate(ProjectPipelineRunLog.class, bean.id);
			old.status=bean.status;
			old.node=bean.node;
			old.stage=bean.stage;
			old.step=bean.step;
			old.parameter=bean.parameter;
			old.errorMessage=bean.errorMessage;
			old.stepInfo=bean.stepInfo;
			old.endTime=bean.endTime;
			old.useTime=bean.useTime;
			dao.update(old);
		}
		//
		@Transaction
		@Override
		public int addDetailLog(ProjectPipelineRunDetailLog log) {
			return dao.add(log);
		}

		@Transaction
		@Override
		public void appendDetailLogMessage(int id,String message) {
			ProjectPipelineRunDetailLog old=dao.getExistedByIdForUpdate(ProjectPipelineRunDetailLog.class, id);
			old.message=old.message+message;
			dao.update(old);

		}

		@Override
		public ProjectPipelineRunLog getProjectPipelineRunLogById(int id) {
			return dao.getById(ProjectPipelineRunLog.class, id);
		}
		
		@Override
		public ProjectPipelineRunLog getProjectPipelineRunLog(int id) {
			return dao.getExistedById(ProjectPipelineRunLog.class, id);
		}

		@Override
		public Machine getMachineByRunLogIdName(int runLogId,String name) {
			ProjectPipelineRunLog log=dao.getExistedById(ProjectPipelineRunLog.class, runLogId);
			return dao.getMachineByProjectIdName(log.projectId,name);
		}

		@Override
		public ParameterValue getParamaterValue(int runLogId, String parameter) {
			ProjectPipelineRunLogInfo old=dao.getExistedById(ProjectPipelineRunLogInfo.class, runLogId);
			String key=old.stage+"-"+old.parameter;
			ParameterValue value=old.parameterMap.get(key);
			return value;
		}
		@Transaction
		@Override
		public void addProjectArtifact(int runLogId,ProjectArtifact bean) {
			addProjectArtifact0(runLogId, bean);
		}

		private synchronized void addProjectArtifact0(int runLogId,ProjectArtifact bean) {
			ProjectPipelineRunLog log=dao.getExistedById(ProjectPipelineRunLog.class, runLogId);
			//把之前相同版本的设置为删除
			List<ProjectArtifact> oldList=dao.getProjectArtifactList(log.projectId,bean.name,bean.version);
			for (ProjectArtifact e : oldList) {
				e.isDelete=true;
				dao.update(e);
			}
			//删除文件(只保留10个)
			List<ProjectArtifact> deleteFileList=dao.getNeedDeleteFileProjectArtifactList(log.projectId,bean.name,bean.version);
			for (ProjectArtifact e : deleteFileList) {
				try {
					java.io.File file=BizUtil.getArtifactFile(e.uuid);
					if(file!=null&&file.exists()) {
						file.delete();
						logger.info("delete file {} {}",DumpUtil.dump(e),file.getAbsolutePath());
					}
					e.isFileDelete=true;
					dao.updateSpecialFields(e, "isFileDelete");
				} catch (Exception e2) {
					logger.error(e2.getMessage(),e2);
				}
			}
			bean.projectId=log.projectId;
			Project project=dao.getExistedById(Project.class, log.projectId);
			bean.companyId=project.companyId;
			bean.createAccountId=log.createAccountId;
			BizUtil.checkValid(bean);
			dao.add(bean);
		}

		@Override
		public ProjectArtifact getProjectArtifact(int runLogId, String uuid) {
			ProjectPipelineRunLog log=dao.getExistedById(ProjectPipelineRunLog.class, runLogId);
			ProjectArtifact bean=dao.getProjectArtifact(log.projectId,uuid);
			return bean;
		}

		@Override
		public void sendNotification(int runLogId, String userName, String content) {
			Account optAccount=null;
			ProjectPipelineRunLog log=dao.getExistedById(ProjectPipelineRunLog.class, runLogId);
			if(log.createAccountId>0) {//可能是Cron自动跑的
				optAccount=dao.getById(Account.class, log.createAccountId);
			}
			ProjectPipelineInfo pipeline=dao.getById(ProjectPipelineInfo.class, log.pipelineId);
			Account toAccount=dao.getAccountByUserName(userName);
			if(toAccount==null) {
				throw new AppException("待通知用户不存在"+userName);
			}
			if(toAccount.companyId!=pipeline.companyId) {
				logger.error("toAccount.companyId {}!=pipeline.companyId{}",
						toAccount.companyId,pipeline.companyId);
				throw new AppException("待通知用户不存在"+userName);
			}
			//发送推送
			PipelineNotification bean=new PipelineNotification();
			bean.pipelineName=pipeline.name;
			bean.content="Pipeline提醒："+content;
			bean.projectName=pipeline.projectName;
			service.addAccountNotification(toAccount.id, AccountNotificationSetting.TYPE_PIPELINE提醒,
					pipeline.companyId, pipeline.projectId, log.id,
					pipeline.name, JSONUtil.toJson(bean), new Date(), optAccount);
		}
	}
}
