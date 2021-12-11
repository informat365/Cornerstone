package cornerstone.biz.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.OptLog;
import cornerstone.biz.domain.Permission;
import cornerstone.biz.domain.SystemHook;
import cornerstone.biz.domain.SystemHook.SystemHookInfo;
import cornerstone.biz.domain.SystemHook.SystemHookQuery;
import cornerstone.biz.domain.SystemHookLog;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.srv.SystemHookService;
import cornerstone.biz.systemhook.JavaScriptSystemHook;
import cornerstone.biz.util.BizUtil;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.server.rpc.RpcService;

/**
 *
 *系统钩子Action
 */
@ApiDefine("系统钩子接口")
public interface SystemHookAction {

	@ApiDefine(value = "通过ID查询系统钩子",params = {"登录Token","系统钩子ID"},resp = "系统钩子")
	SystemHookInfo getSystemHookById(String token, int id);

	@ApiDefine(value = "查询系统钩子列表和总数",params = {"登录Token","系统钩子查询类"},resp = "系统钩子集合")
	Map<String, Object> getSystemHookList(String token,SystemHookQuery query);

	@ApiDefine(value = "查询系统钩子列表",params = {"登录Token"},resp = "系统钩子集合")
	List<SystemHookInfo> getMySystemHookList(String token);

	@ApiDefine(value = "新增系统钩子",params = {"登录Token","系统钩子信息"},resp = "系统钩子ID")
	int addSystemHook(String token, SystemHookInfo bean);

	@ApiDefine(value = "编辑系统钩子",params = {"登录Token","系统钩子信息"})
	void updateSystemHook(String token, SystemHookInfo bean);

	@ApiDefine(value = "删除系统钩子",params = {"登录Token","系统钩子ID"})
	void deleteSystemHook(String token, int id);

	@ApiDefine(value = "更新系统钩子状态",params = {"登录Token","系统钩子ID","状态值"})
	 void updateSystemHookStatus(String token, int id,int status);
	//
	@RpcService
	class SystemHookActionImpl extends CommActionImpl implements SystemHookAction {
		//
		@AutoWired
		BizDAO dao;
		@AutoWired
		BizService bizService;
		@AutoWired
		SystemHookService dataTableService;//一定要有
		//
		//
		@Override
		public SystemHookInfo getSystemHookById(String token, int id){
			Account account=bizService.getExistedAccountByToken(token);
			bizService.checkCompanyPermission(account, Permission.ID_系统设置);
			SystemHookInfo info=dao.getById(SystemHookInfo.class, id);
			return info;
		}

		@Override
		public Map<String, Object> getSystemHookList(String token,SystemHookQuery query){
			Account account=bizService.getExistedAccountByToken(token);
			bizService.checkCompanyPermission(account, Permission.ID_系统设置);
			setupQuery(account, query);
			return createResult(dao.getList(query,"systemHookDefine"), dao.getListCount(query));
		}

		@Override
		public List<SystemHookInfo> getMySystemHookList(String token){
			Account account=bizService.getExistedAccountByToken(token);
			SystemHookQuery query=new SystemHookQuery();
			query.status=SystemHook.STATUS_有效;
			setupQuery(account, query);
			query.pageSize=Integer.MAX_VALUE;
			List<SystemHookInfo> allList=dao.getList(query,"dataTableDefine","script");
			List<SystemHookInfo> list=new ArrayList<>();
			for (SystemHookInfo e : allList) {
				if(bizService.checkAccountCompanyPermission(account.id,true,e.roles, null)) {
					list.add(e);
				}
			}
			return list;
		}

		/**新增数据表*/
		@Transaction
		@Override
		public int addSystemHook(String token, SystemHookInfo bean) {
			Account account=bizService.getExistedAccountByToken(token);
			bizService.checkCompanyPermission(account, Permission.ID_系统设置);
			bean.createAccountId=account.id;
			bean.companyId=account.companyId;
			bean.status=SystemHookInfo.STATUS_有效;
			BizUtil.checkValid(bean);
			bean.lastRunTime=null;
			bean.runCount=0;
			if(bean.type==SystemHook.TYPE_定时任务) {
				bean.nextRunTime=BizUtil.nextRunTime(null, bean.cron);
			}else {
				bean.cron=null;
			}
			JavaScriptSystemHook dataTable=new JavaScriptSystemHook(bean.script);
			bean.systemHookDefine=dataTable.getSystemHookDefine();
			bean.functionNames=bean.systemHookDefine.functions;
			dao.add(bean);
			bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_新增系统钩子, "name:"+bean.name);
			return bean.id;
		}

		/**编辑数据表*/
		@Transaction
		@Override
		public void updateSystemHook(String token, SystemHookInfo bean) {
			Account account=bizService.getExistedAccountByToken(token);
			SystemHook old=dao.getExistedByIdForUpdate(SystemHook.class, bean.id);
			bizService.checkCompanyPermission(account, Permission.ID_系统设置,old.companyId);
			//
			if(!old.script.equals(bean.script)) {//变更记录
				SystemHookLog log=new SystemHookLog();
				log.systemHookId=bean.id;
				log.companyId=bean.companyId;
				log.name=bean.name;
				log.group=bean.group;
				log.projectIds=bean.projectIds;
				log.type=bean.type;
				log.cron=bean.cron;
				log.lastRunTime=bean.lastRunTime;
				log.lastRunAccountId=bean.lastRunAccountId;
				log.nextRunTime=bean.nextRunTime;
				log.runCount=bean.runCount;
				log.isAllProject=bean.isAllProject;
				log.roles=bean.roles;
				log.enableRole=bean.enableRole;
				log.status=bean.status;
				log.isDelete=bean.isDelete;
				log.script=bean.script;
				log.remark=bean.remark;
				log.systemHookDefine=bean.systemHookDefine;
				log.createAccountId=account.id;
				dao.add(log);
			}
			//
			old.name=bean.name;
			old.script=bean.script;
			old.remark=bean.remark;
			old.group=bean.group;
			old.projectIds=bean.projectIds;
			old.type=bean.type;
			old.cron=bean.cron;
			old.isAllProject=bean.isAllProject;
			old.roles=bean.roles;
			old.enableRole=bean.enableRole;
			old.updateAccountId=account.id;
			if(bean.type==SystemHook.TYPE_定时任务) {
				old.nextRunTime=BizUtil.nextRunTime(null, old.cron);
			}else {
				old.nextRunTime=null;
				old.cron=null;
			}
			BizUtil.checkValid(old);
			//
			JavaScriptSystemHook dataTable=new JavaScriptSystemHook(bean.script);
			old.systemHookDefine=dataTable.getSystemHookDefine();
			old.functionNames=old.systemHookDefine.functions;
			dao.update(old);
			//
			bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_编辑系统钩子, "name:"+old.name);
		}

		/**删除数据表*/
		@Transaction
		@Override
		public void deleteSystemHook(String token, int id) {
			Account account=bizService.getExistedAccountByToken(token);
			SystemHook old=dao.getExistedByIdForUpdate(SystemHook.class, id);
			if(old.isDelete) {
				return;
			}
			bizService.checkCompanyPermission(account, Permission.ID_系统设置,old.companyId);
			old.isDelete=true;
			old.updateAccountId=account.id;
			dao.update(old);
			//
			bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除系统钩子, "name:"+old.name);
		}

		@Transaction
		@Override
		public void updateSystemHookStatus(String token, int id,int status) {
			Account account=bizService.getExistedAccountByToken(token);
			SystemHook old=dao.getExistedByIdForUpdate(SystemHook.class,id);
			bizService.checkCompanyPermission(account, Permission.ID_系统设置,old.companyId);
			checkDataDictValueValid("Common.status", status, "状态错误");
			old.updateAccountId=account.id;
			old.status=status;
			dao.update(old);
		}
	}
}
