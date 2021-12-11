package cornerstone.biz.taskjob;

import cornerstone.biz.CornerstoneBizSystem;
import cornerstone.biz.action.TaskJobAction;
import cornerstone.biz.domain.AccountNotification.AccountNotificationInfo;
import cornerstone.biz.domain.AccountNotification.AccountNotificationQuery;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.Company.CompanyInfo;
import cornerstone.biz.domain.WorkflowInstance.WorkflowInstanceInfo;
import jazmin.core.Jazmin;
import jazmin.core.job.JobDefine;
import jazmin.core.task.TaskDefine;
import jazmin.driver.jdbc.DruidConnectionDriver;
import jazmin.log.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author cs
 *
 */
public class TaskJobs{
	//
	private static jazmin.log.Logger logger=LoggerFactory.get(TaskJobs.class);
	//
	public static TaskJobAction taskJobAction;
	//
	public TaskJobs(){
	}
	
	
	/**
	 * 发送提醒
	 */
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=1)
	public void sendExpiredRemindList(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.sendExpiredRemindList();
	}
	
	/**
	 * 同步发送微信邮件通知
	 */
	@TaskDefine(initialDelay=60,unit=TimeUnit.SECONDS,period=60)
	public void asyncSendNotification(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		AccountNotificationQuery query=new AccountNotificationQuery();
		query.isWeixinSend=false;
		query.pageSize=1000;
		List<AccountNotificationInfo> notifications=taskJobAction.getAccountNotificationInfoList(query);
		for (AccountNotificationInfo e : notifications) {
			try {
				taskJobAction.asyncSendNotification(e);
			} catch (Exception ex) {
				logger.error(ex.getMessage(),ex);
			}
		}
	}
	
	/**
	 * 跑全文检索
	 */
	@TaskDefine(initialDelay=60,unit=TimeUnit.SECONDS,period=300)
	public void initDocumentIndex(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.initDocumentIndex();
	}
	
	/**
	 * 跑Note全文检索
	 */
	@TaskDefine(initialDelay=30,unit=TimeUnit.SECONDS,period=300)
	public void initNoteIndex(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.initNoteIndex();
	}
	
	
	/**
	 * 计算Task的已用天数和剩余天数
	 */
	@TaskDefine(initialDelay=5,unit=TimeUnit.SECONDS,period=60*30)
	public void updateTaskStartEndDays(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.updateTaskStartEndDays();
	}
	
	/**
	 * 计算迭代项目每日统计
	 */
	@JobDefine(cron="5 0 0 * * ?")
	public void createTaskDayDataByIteration() {
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.doCreateTaskDayData();
	}
	
	/**
	 * 计算迭代项目每日总的统计
	 */
//	@JobDefine(cron="5 0 0 * * ?")
	public void createTaskDayTotalDataByIteration() {
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.calcNoObjectTypeTaskDayData();
	}

	/**
	 * Pipeline定时跑
	 */
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=1)
	public void runProjectPipeline() {
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		List<ProjectPipeline> list=taskJobAction.getNeedRunProjectPipelineInfos();
		for (ProjectPipeline bean : list) {
			try {
				taskJobAction.runProjectPipeline(bean);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 
	 */
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=1)
	public void runCmdbRobot() {
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		List<CmdbRobot> list=taskJobAction.getNeedRunCmdbRobots();
		for (CmdbRobot e : list) {
			taskJobAction.runCmdbRobot(e);
		}
	}
	
	/**
	 * 判断数据库有没有变更
	 */
//	@TaskDefine(initialDelay=60*3,unit=TimeUnit.MINUTES,period=60*3)
//	public void judgeDatabaseChangeLog() {
//		if(CornerstoneBizSystem.isDebug) {
//			return;
//		}
//		long totalStartTime=System.currentTimeMillis();
//		List<DesignerDatabase> list=taskJobAction.getAllDesignerDatabases();
//		int index=0;
//		for (DesignerDatabase e : list) {
//			index++;
//			long startTime=System.currentTimeMillis();
//			try {
//				taskJobAction.doJudgeDatabaseChangeLog(e);
//			} catch (Exception ex) {
//				logger.error("DesignerDatabase:"+DumpUtil.dump(e));
//				logger.error(ex.getMessage(),ex);
//			}finally {
//				logger.info("doJudgeDatabaseChangeLog using {}ms {} {}/{}",
//						System.currentTimeMillis()-startTime,e.instanceId,index,list.size());
//			}
//		}
//		logger.info("judgeDatabaseChangeLog using {}ms",System.currentTimeMillis()-totalStartTime);
//	}
	//
	/**
	 * 发送任务提醒
	 */
	@TaskDefine(initialDelay=5,unit=TimeUnit.SECONDS,period=300)
	public void sendTaskRemind(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		List<TaskRemindTime> list=taskJobAction.getNeedRunTaskRemindTimes();
		for (TaskRemindTime bean : list) {
			taskJobAction.doSendTaskRemind(bean);
		}
	}
	
	/**
	 * 根据汇报模板自动生成汇报
	 */
	@TaskDefine(initialDelay=5,unit=TimeUnit.SECONDS,period=300)
	public void generateReport(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		List<ReportTemplate> list=taskJobAction.getNeedRunReportTemplates();
		for (ReportTemplate bean : list) {
			taskJobAction.doGenerateReport(bean);
		}
	}
	
	/**
	 * 计算文件或目录的path
	 */
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=60)
	public void calcFilePath(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.doCalcFilePath();
	}
	
	/**
	 * 计算项目15天内的活动次数
	 */
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=60*24)
	public void calcProjectActivity(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.doCalcProjectActivity();
	}
	
	/**
	 * 重置每日登陆失败次数以及验证码错误次数
	 */
	@JobDefine(cron="0 0 0 * * ?")
	public void resetDailyLoginFailCountKaptchaErrorCount() {
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.doResetDailyLoginFailCountKaptchaErrorCount();
	}
	
	/**
	 * 个人已超期任务提醒
	 */
	@JobDefine(cron="0 0 10 * * ?")
	public void sendTaskOverDueNotification() {
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.doSendTaskOverDueNotification();
	}
	
	/**
	 * 修复任务责任人数据(20200131注释)
	 */
//	@JobDefine(cron="0 0 4 * * ?")
//	public void fixTaskOwnerDatas() {
//		if(CornerstoneBizSystem.isDebug) {
//			return;
//		}
//		taskJobAction.doFixTaskOwnerDatas();
//	}
	
	/**
	 * 系统钩子定时跑
	 */
	@TaskDefine(initialDelay=2,unit=TimeUnit.MINUTES,period=1)
	public void runSystemHook() {
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		List<SystemHook> list=taskJobAction.getNeedRunSystemHooks();
		for (SystemHook bean : list) {
			taskJobAction.doRunSystemHook(bean);
		}
	}
	
	/**
	 * 更新公司许可证
	 */
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=60)
	public void updateCompanyLiceneInfo() {
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		List<CompanyInfo> list=taskJobAction.getNeedRunCompanies();
		for (CompanyInfo bean : list) {
			taskJobAction.doUpdateCompanyLiceneInfo(bean);
		}
	}
	
	/**
	 * 检查数据字典数据对不对
	 */
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=60*24)
	public void checkDataDictData() {
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.doCheckDataDictData();
	}
	
	/**
	 * 删除草稿流程
	 */
	@TaskDefine(initialDelay=2,unit=TimeUnit.MINUTES,period=60)
	public void deleteDraftWorkflowInstance() {
		List<WorkflowInstanceInfo> list=taskJobAction.getNeedDeleteDraftWorkflowInstances();
		for (WorkflowInstanceInfo bean : list) {
			taskJobAction.deleteWorkflowInstance(bean.id);
		}
	}
	
	/**
	 * 调用存储过程
	 */
	@JobDefine(cron="0 0 6 * * ?")
	public void callProcCheckData() {
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		if("CornerstoneBizSystemPrd".equals(Jazmin.getServerName())) {
			return;
		}
		taskJobAction.doCallProcCheckData();
	}
	
	/**
	 * taskJobAction定时跑
	 */
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=5)
	public void runTaskJobAction() {
		List<TaskActionJob> list=taskJobAction.getNeedRunTaskActionJobs();
		String message="SUCCESS";
		for (TaskActionJob bean : list) {
			try {
				Method method=taskJobAction.getClass().getMethod(bean.actionName);
				method.invoke(taskJobAction);
			}catch (InvocationTargetException e) {  
				logger.error(e.getMessage(),e);
				Throwable exception = e.getTargetException();
				if(exception!=null) {
					message=exception.getMessage(); 
				}
	        }catch (Exception e) {
				logger.error(e.getMessage(),e);
				if(e!=null) {
					message=e.getMessage();
				}
			}
			bean.isRun=true;
			bean.message=message;
			taskJobAction.updateTaskActionJob(bean);
		}
	}
	/**
	 * 重置邀请好友每日发送邮件数量
	 */
	@JobDefine(cron="10 0 0 * * ?")
	public void resetAccountInviteMemberDailySendMailNum(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.doResetAccountInviteMemberDailySendMailNum();
	}
	
	//
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=10)
	public void deleteInvalidKaptchas(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		taskJobAction.doDeleteInvalidKaptchas();
	}
	
	/**
	 * 
	 */
	@TaskDefine(initialDelay=5,unit=TimeUnit.MINUTES,period=5)
	public void logDruidInfo(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		DruidConnectionDriver connectionDriver=Jazmin.getDriver(DruidConnectionDriver.class);
		if(connectionDriver==null) {
			return;
		}
		logger.info(connectionDriver.info());
	}
	
	/**
	 * 
	 */
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=1)
	public void sendSystemNotifaction(){
		if(CornerstoneBizSystem.isDebug) {
			return;
		}
		List<Integer> list=taskJobAction.getNeedSendSystemNotifactionList();
		for (Integer id : list) {
			try {
				taskJobAction.doSendSystemNotifaction(id);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
	}


	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=1)
	public void retryScmCommit(){
		try {
			taskJobAction.retryScmCommit();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}


	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=2)
	public void asyncSendEmail(){
		try {
			taskJobAction.asyncSendEmail();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * 同步项目集
	 */
	@TaskDefine(initialDelay=1,unit=TimeUnit.MINUTES,period=30)
	public void syncProjectSet(){
		try {
			taskJobAction.syncProjectSet();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}


	@TaskDefine(initialDelay=0,unit=TimeUnit.HOURS,period=3)
	public void fixDataV60(){
		try {
			taskJobAction.doFixDataForV60();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * 修复未能正确赋予对象创建权限的问题
	 */
	@TaskDefine(initialDelay=0,unit=TimeUnit.HOURS,period=12)
	public void fixDataV65(){
		try {
			taskJobAction.doFixDataForV65();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * 每天凌晨一点同步钉钉用户信息
	 */
	@JobDefine(cron="0 0 1 * * ?")
	public void syncDingtalkAccount(){
		try {
			taskJobAction.syncDingtalkAccount();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * 每月8号2点同步上个月的钉钉用户考勤信息
	 */
	@JobDefine(cron="0 0 2 8 * ?")
//	@JobDefine(cron="0 */2 * * * ?")
	public void syncDingtalkAttendance(){
		try {
			taskJobAction.syncDingtalkAttendance();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}


	/**
	 *同步AD域账号状态
	 */
	@JobDefine(cron="0 0 1,13 * * ?")
	public void syncAdAccount(){
		try {
			taskJobAction.syncAdAccount();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 *每天凌晨更新项目的spi进度
	 */
	@JobDefine(cron="0 0 1 * * ?")
	public void syncProjectSpiProcess(){
		try {

			taskJobAction.syncProjectSpiProcess();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
}
