package cornerstone.biz.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.dao.SurveysDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.Attachment;
import cornerstone.biz.domain.ExportData;
import cornerstone.biz.domain.OptLog;
import cornerstone.biz.domain.Permission;
import cornerstone.biz.domain.SurveysDefine;
import cornerstone.biz.domain.SurveysDefine.SurveysDefineInfo;
import cornerstone.biz.domain.SurveysDefine.SurveysDefineQuery;
import cornerstone.biz.domain.SurveysFormDefine;
import cornerstone.biz.domain.SurveysFormDefine.SurveysFormDefineInfo;
import cornerstone.biz.domain.SurveysFormDefine.SurveysFormDefineQuery;
import cornerstone.biz.domain.SurveysFormDefine.SurveysFormField;
import cornerstone.biz.domain.SurveysInstance;
import cornerstone.biz.domain.SurveysInstance.SurveysInstanceInfo;
import cornerstone.biz.domain.SurveysInstance.SurveysInstanceQuery;
import cornerstone.biz.domain.SurveysSubmitForm;
import cornerstone.biz.domain.SurveysSubmitInfo;
import cornerstone.biz.poi.TableData;
import cornerstone.biz.srv.SurveysService;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.util.DumpUtil;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 *
 */
public interface SurveysAction {

	//
	/**通过ID查询问卷调查*/
	SurveysDefineInfo getSurveysDefineByUuid(String token, String uuid);

	/**查询问卷调查列表和总数*/
	Map<String, Object> getSurveysDefineList(String token,SurveysDefineQuery query);
	
	/**新增问卷调查*/
	String addSurveysDefine(String token, SurveysDefineInfo bean);

	/**编辑问卷调查*/
	void updateSurveysDefine(String token, SurveysDefineInfo bean);
	
	/**启用问卷调查*/
	void enableSurveysDefine(String token, int SurveysDefine);

	/**禁用问卷调查*/
	void disableSurveysDefine(String token, int SurveysDefine);

	/**删除问卷调查*/
	void deleteSurveysDefine(String token, int SurveysDefine);
	
	/**复制问卷调查*/
	String copySurveysDefine(String token, int surveysDefineId);
	
	/**查询表单设计*/
	SurveysFormDefineInfo getSurveysFormDefineById(String token, int id);

	/**查询表单设计列表*/
	Map<String, Object> getSurveysFormDefineList(String token,SurveysFormDefineQuery query);
	
	/**编辑表单设计*/
	void updateSurveysFormDefine(String token, SurveysFormDefineInfo bean);
	
	/**获取提交信息*/
	SurveysSubmitInfo getSurveysSubmitInfo(String token,String uuid,String accountUuid);
	
	/**提交问卷调查*/
	SurveysInstance submitSurveysInstance(String token, SurveysSubmitForm submitInfo);
	
	/**编辑模板权限*/
	void updateSurveysDefinePermission(String token, SurveysDefine bean);
	
	/**删除问卷调查实例*/
	void deleteSurveysInstance(String token, int id);
	
	/**通过ID查询问卷调查实例*/
	SurveysInstanceInfo getSurveysInstanceById(String token, int id);

	/**查询问卷调查实例列表和总数*/
	Map<String, Object> getSurveysInstanceList(String token,SurveysInstanceQuery query);

	/**查询我发起的问卷调查实例列表和总数*/
	Map<String, Object> getMyCreateSurveysInstanceList(String token,SurveysInstanceQuery query);
	
	/**导出xlsx*/
	ExportData exportSurveysInstancesToExcel(String token,SurveysInstanceQuery query);
	
	/**上传附件*/
	Attachment addSurveysAttachment(String token,String surveysDefineUuid,Attachment bean);
	
	/**重置问卷调查实例列表*/
	void resetSurveysInstances(String token, int surveysDefineId);
	//
	@RpcService
	public static class SurveysActionImpl extends CommActionImpl implements SurveysAction {
		//
		private static Logger logger=LoggerFactory.get(SurveysActionImpl.class);
		//
		@AutoWired
		SurveysService surveysService;
		@AutoWired
		SurveysDAO surveysDAO;
		//
		@Override
		public SurveysDefineInfo getSurveysDefineByUuid(String token, String uuid) {
			SurveysDefineInfo bean=dao.getExistedByUuid(SurveysDefineInfo.class, uuid);
			if(bean.isDelete) {
				return null;
			}
			return bean;
		}

		@Override
		public Map<String, Object> getSurveysDefineList(String token, SurveysDefineQuery query) {
			Account account=surveysService.getExistedAccountByToken(token);
			setupQuery(account, query);
			query.pageSize=Integer.MAX_VALUE;
			List<SurveysDefineInfo> ret=null;
			int count=0;
			if(query.type!=null&&query.type==SurveysDefineQuery.TYPE_我发起的) {
				query.createAccountId=account.id;
				ret=surveysDAO.getSurveysNoPermissions(query);
				count=dao.getListCount(query);
				setInstanceStatus(account.uuid, ret);
			}else if(query.type!=null&&query.type==SurveysDefineQuery.TYPE_我填写过) {
				query.status=SurveysDefine.STATUS_有效;
				query.fillAccountId=account.id;	
				ret=surveysDAO.getSurveysNoPermissions(query);
				count=dao.getListCount(query);
				for (SurveysDefineInfo e : ret) {
					e.instanceStatus=SurveysInstance.STATUS_已填写;
				}
			}else if(query.type!=null&&query.type==SurveysDefineQuery.TYPE_我待填写) {
				query.status=SurveysDefine.STATUS_有效;
				List<SurveysDefineInfo> list=getAllMyHavePermissionList(account,query);
				List<Integer> surveysDefineIds=new ArrayList<>();
				for (SurveysDefineInfo e : list) {
					surveysDefineIds.add(e.id);
				}
				ret=new ArrayList<>();
				List<Integer> filledIds=surveysDAO.getMyFilledSurveysDefineIds(account.uuid, surveysDefineIds);
				for (SurveysDefineInfo e : list) {
					if(!filledIds.contains(e.id)) {
						e.instanceStatus=SurveysInstance.STATUS_待填写;
						ret.add(e);
					}
				}
				count=ret.size();
			}else {//判断是否有发起权限
				query.status=SurveysDefine.STATUS_有效;
				ret=getAllMyHavePermissionList(account,query);
				setInstanceStatus(account.uuid, ret);
				count=ret.size();
			}
			for (SurveysDefineInfo e : ret) {
				e.accountList=null;
				e.companyRoleList=null;
				e.projectRoleList=null;
				e.departmentList=null;	
			}
			return createResult(ret, count);
		}
		
		//查出所有我有权限填写的问卷调查
		private List<SurveysDefineInfo> getAllMyHavePermissionList(Account account,SurveysDefineQuery query){
			List<SurveysDefineInfo> list=dao.getList(query);
			List<SurveysDefineInfo> ret=new ArrayList<>();
			for (SurveysDefineInfo e : list) {
				if(surveysService.havePermission(account.id,e)) {
					ret.add(e);
				}
			}
			return ret;
		}
		
		private void setInstanceStatus(String accountUuid,List<SurveysDefineInfo> ret) {
			List<Integer> surveysDefineIds=new ArrayList<>();
			for (SurveysDefineInfo e : ret) {
				surveysDefineIds.add(e.id);
			}
			List<Integer> filledIds=surveysDAO.getMyFilledSurveysDefineIds(accountUuid, surveysDefineIds);
			for (SurveysDefineInfo e : ret) {
				if(filledIds.contains(e.id)) {
					e.instanceStatus=SurveysInstance.STATUS_已填写;
				}else {
					e.instanceStatus=SurveysInstance.STATUS_待填写;
				}
			}
		}

		@Transaction
		@Override
		public String addSurveysDefine(String token, SurveysDefineInfo bean) {
			Account account=surveysService.getExistedAccountByToken(token);
			bean.createAccountId=account.id;
			bean.companyId=account.companyId;
			bean.uuid=BizUtil.randomUUID();
			bean.status=SurveysDefineInfo.STATUS_有效;
			surveysService.checkCompanyPermission(account, Permission.ID_创建问卷调查);
			checkValid(bean);
			dao.add(bean);
			//
			SurveysFormDefine formDefine=new SurveysFormDefine();
			//
			formDefine.title=bean.name;
			formDefine.surveysDefineId=bean.id;
			formDefine.companyId=account.companyId;
			formDefine.createAccountId=account.id;
			dao.add(formDefine);
			//
			bean.formDefineId=formDefine.id;
			dao.update(bean);
			//
			bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_新增问卷调查, "name:"+bean.name);
			//
			return bean.uuid;
		}

		@Transaction
		@Override
		public void updateSurveysDefine(String token, SurveysDefineInfo bean) {
			Account account=surveysService.getExistedAccountByToken(token);
			SurveysDefine old=dao.getExistedByIdForUpdate(SurveysDefine.class, bean.id);
			if(old.createAccountId!=account.id) {
				throw new AppException("没有权限编辑问卷调查");
			}
			bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_编辑问卷调查,
					old,bean,"name");
			old.name=bean.name;
			old.anonymous=bean.anonymous;
			old.submitEdit=bean.submitEdit;
			old.startTime=bean.startTime;
			old.endTime=bean.endTime;
			old.remark=bean.remark;
			old.updateAccountId=account.id;
			checkValid(old);
			dao.update(old);
		}
		
		@Transaction
		@Override
		public void enableSurveysDefine(String token, int surveysDefineId) {
			Account account=surveysService.getExistedAccountByToken(token);
			SurveysDefine old=dao.getExistedByIdForUpdate(SurveysDefine.class, surveysDefineId);
			if(old.createAccountId!=account.id) {
				throw new AppException("权限不足");
			}
			if(old.status==SurveysDefine.STATUS_有效) {
				return;
			}
			old.status=SurveysDefine.STATUS_有效;
			old.updateAccountId=account.id;
			dao.update(old);
			bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_启用问卷调查, "name:"+old.name);
		}

		@Transaction
		@Override
		public void disableSurveysDefine(String token, int surveysDefineId) {
			Account account=surveysService.getExistedAccountByToken(token);
			SurveysDefine old=dao.getExistedByIdForUpdate(SurveysDefine.class, surveysDefineId);
			if(old.createAccountId!=account.id) {
				throw new AppException("权限不足");
			}
			if(old.status==SurveysDefine.STATUS_无效) {
				return;
			}
			old.status=SurveysDefine.STATUS_无效;
			old.updateAccountId=account.id;
			dao.update(old);
			bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_禁用问卷调查, "name:"+old.name);
		}

		@Transaction
		@Override
		public void deleteSurveysDefine(String token, int id) {
			Account account=surveysService.getExistedAccountByToken(token);
			SurveysDefine old=dao.getExistedByIdForUpdate(SurveysDefine.class,id);
			if(old.isDelete) {
				return;
			}
			if(old.createAccountId!=account.id) {
				throw new AppException("权限不足");
			}
			old.isDelete=true;
			dao.update(old);
			//
			bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除问卷调查, "name:"+old.name);
		}
		//
		/**复制问卷调查*/
		@Override
		@Transaction
		public String copySurveysDefine(String token, int surveysDefineId) {
			Account account=surveysService.getExistedAccountByToken(token);
			SurveysDefine define=dao.getExistedById(SurveysDefine.class, surveysDefineId);
			bizService.checkPermission(account, define.companyId);
			surveysService.checkCompanyPermission(account, Permission.ID_创建问卷调查);
			SurveysFormDefine form=dao.getExistedById(SurveysFormDefine.class, define.formDefineId);
			define.uuid=BizUtil.randomUUID();
			define.formDefineId=0;
			define.name=define.name+"-副本";
			define.createAccountId=account.id;
			define.updateAccountId=0;
			define.lastSubmitTime=null;
			define.submitCount=0;
			dao.add(define);
			//
			form.surveysDefineId=define.id;
			form.createAccountId=account.id;
			form.updateAccountId=0;
			dao.add(form);
			//
			define.formDefineId=form.id;
			dao.update(define);
			//
			return define.uuid;
		}
		//
		/**通过ID查询表单定义*/
		@Override
		public SurveysFormDefineInfo getSurveysFormDefineById(String token, int surveysDefineId){
			Account account=surveysService.getExistedAccountByToken(token);
		    return surveysDAO.getExistedSurveysFormDefine(surveysDefineId);
		}

		/**查询表单定义列表和总数*/
		@Override
		public Map<String, Object> getSurveysFormDefineList(String token,SurveysFormDefineQuery query){
			Account account=surveysService.getExistedAccountByToken(token);
			setupQuery(account, query);
			return createResult(dao.getList(query), dao.getListCount(query));
		}
		//
		/**编辑表单定义*/
		@Transaction
		@Override
		public void updateSurveysFormDefine(String token, SurveysFormDefineInfo bean) {
			Account account=surveysService.getExistedAccountByToken(token);
			SurveysFormDefineInfo old=dao.getExistedByIdForUpdate(SurveysFormDefineInfo.class, bean.id);
			//surveysService.checkCompanyPermission(account, Permission.ID_管理流程模板,old.companyId);
			if(old.createAccountId!=account.id) {
				throw new AppException("权限不足");
			}
			old.title=bean.title;
			old.fieldList=bean.fieldList;
			old.updateAccountId=account.id;
			checkValid(old);
			dao.update(old);
			//
			List<SurveysFormField> formFields=JSONUtil.fromJsonList(old.fieldList, SurveysFormField.class);
			SurveysDefine define=dao.getExistedById(SurveysDefine.class, old.surveysDefineId);
//			checkSurveysDefine(define, formFields);
			//
			bizService.addOptLog(account, old.id, old.surveysDefineName, OptLog.EVENT_ID_编辑问卷调查表单设计, "name:"+old.surveysDefineName);
		}
		
		@Override
		public SurveysSubmitInfo getSurveysSubmitInfo(String token,String uuid,String accountUuid) {
			Account account=surveysService.getAccountByToken(token);//Account可能是空的
			SurveysSubmitInfo info=new SurveysSubmitInfo();
			info.surveysDefine=dao.getExistedByUuid(SurveysDefine.class, uuid);
			info.formDefine=dao.getExistedById(SurveysFormDefine.class, info.surveysDefine.formDefineId);
			//权限检查 如果不能匿名 那就检查权限
			if(!info.surveysDefine.anonymous) {
				if(account==null) {
					throw new AppException("请先登录");
				}
				if(!surveysService.havePermission(account.id, info.surveysDefine)) {
					logger.warn("not havePermission accountId:{} surveysDefine:{}",
							account.id,DumpUtil.dump(info.surveysDefine));
					throw new AppException("没有权限填写问卷调查");
				}
			}
			if(account!=null) {
				info.instance=surveysDAO.getSurveysInstance(account.uuid, info.surveysDefine.id);
			}else {
				info.instance=surveysDAO.getSurveysInstance(accountUuid, info.surveysDefine.id);
			}
			return info;
		}
		/**
		 * 提交表单
		 */
		@Transaction
		@Override
		public SurveysInstance submitSurveysInstance(String token, SurveysSubmitForm submitInfo) {
			Account account=surveysService.getAccountByToken(token);//Account可能是空的
			int surveysDefineId=submitInfo.surveysDefineId;
			SurveysDefine define=dao.getExistedByIdForUpdate(SurveysDefine.class, surveysDefineId);
			if(define==null||define.isDelete||define.status!=SurveysDefine.STATUS_有效) {
				throw new AppException("问卷调查不存在");
			}
			Date now=new Date();
			if(define.startTime!=null) {
				if(now.before(define.startTime)) {
					throw new AppException("问卷调查未开始");
				}
			}
			if(define.endTime!=null) {
				if(now.after(define.endTime)) {
					throw new AppException("问卷调查已结束");
				}
			}
			if(!define.anonymous) {
				if(account==null) {
					throw new AppException("请先登录");
				}
				if(!surveysService.havePermission(account.id, define)) {
					logger.warn("not havePermission accountId:{} surveysDefine:{}",
							account.id,DumpUtil.dump(define));
					throw new AppException("没有权限填写问卷调查");
				}
			}
			SurveysInstance instance=null;
			if(account!=null) {
				surveysService.checkCompanyPermission(account, define.companyId);
				instance=surveysDAO.getSurveysInstanceForUpdate(account.uuid,surveysDefineId);
			}else {
				if(!define.anonymous) {
					throw new AppException("问卷调查不支持匿名填写");
				}
				if(!StringUtil.isEmpty(submitInfo.accountUuid)) {
					instance=surveysDAO.getSurveysInstanceForUpdate(submitInfo.accountUuid,surveysDefineId);
				}
			}
			if((!define.submitEdit)&&instance!=null&&instance.status==SurveysInstance.STATUS_已填写) {
				throw new AppException("问卷调查已填写，不能重复填写");
			}
			if(instance==null) {
				instance=new SurveysInstance();
				instance.uuid=BizUtil.randomUUID();
				instance.companyId=define.companyId;
				instance.surveysDefineId=define.id;
				if(account!=null) {
					instance.accountUuid=account.uuid;
					instance.createAccountId=account.id;
					instance.updateAccountId=account.id;
				}else {
					if(!StringUtil.isEmpty(submitInfo.accountUuid)) {
						instance.accountUuid=submitInfo.accountUuid;
					}else {
						instance.accountUuid=BizUtil.randomUUID();
					}
				}
				instance.surveysFormDefineId=define.formDefineId;
				dao.add(instance);
			}
			if(submitInfo.userAgent!=null&&submitInfo.userAgent.length()>500) {
				submitInfo.userAgent=submitInfo.userAgent.substring(0,500);
			}
			instance.userAgent=submitInfo.userAgent;
			instance.status=submitInfo.submit?SurveysInstance.STATUS_已填写:SurveysInstance.STATUS_待填写;
			instance.formData=submitInfo.formData;
			dao.update(instance);
			//
			if(submitInfo.submit) {
				define.submitCount=surveysDAO.getSurveysDefineSubmitCount(define.id);
				define.lastSubmitTime=new Date();
				dao.update(define);
			}
			return instance;
		}
		
		@Transaction
		@Override
		public void updateSurveysDefinePermission(String token, SurveysDefine bean) {
			Account account=surveysService.getExistedAccountByToken(token);
			SurveysDefine old=dao.getExistedByIdForUpdate(SurveysDefine.class, bean.id);
			//surveysService.checkCompanyPermission(account, Permission.ID_管理流程模板,old.companyId);
			if(old.createAccountId!=account.id) {
				throw new AppException("权限不足");
			}
			old.accountList=bean.accountList;
			old.companyRoleList=bean.companyRoleList;
			old.projectRoleList=bean.projectRoleList;
			old.departmentList=bean.departmentList;
			dao.update(old);
		}
		
		@Transaction
		@Override
		public void deleteSurveysInstance(String token, int id) {
			Account account=surveysService.getExistedAccountByToken(token);
			SurveysInstance old=dao.getExistedById(SurveysInstance.class, id);
			SurveysDefine surveysDefine=dao.getExistedByIdForUpdate(SurveysDefine.class, old.surveysDefineId);
			if(surveysDefine.createAccountId!=account.id) {
				throw new AppException("权限不足");
			}
			dao.deleteById(SurveysInstance.class, id);
			//
			surveysDefine.submitCount=surveysDAO.getSurveysDefineSubmitCount(old.surveysDefineId);
			dao.update(surveysDefine);
			//
			logger.info("SurveysInstance:{}",DumpUtil.dump(old));
			bizService.addOptLog(account, old.id, old.title, OptLog.EVENT_ID_删除问卷调查实例, "title:"+old.title);
		}

		@Override
		public SurveysInstanceInfo getSurveysInstanceById(String token, int id) {
			Account account=surveysService.getExistedAccountByToken(token);
			return dao.getExistedById(SurveysInstanceInfo.class, id);
		}

		@Override
		public Map<String, Object> getSurveysInstanceList(String token, SurveysInstanceQuery query) {
			Account account=surveysService.getExistedAccountByToken(token);
			setupQuery(account, query);
			return createResult(dao.getList(query), dao.getListCount(query));
		}

		@Override
		public Map<String, Object> getMyCreateSurveysInstanceList(String token, SurveysInstanceQuery query) {
			Account account=surveysService.getExistedAccountByToken(token);
			if(query.surveysDefineId==null) {
				throw new AppException("参数错误");
			}
			SurveysDefine surveysDefine=dao.getExistedById(SurveysDefine.class, query.surveysDefineId);
			if(surveysDefine.createAccountId!=account.id) {
				throw new AppException("权限不足");
			}
			setupQuery(account, query);
			query.status=SurveysInstance.STATUS_已填写;
			return createResult(dao.getList(query), dao.getListCount(query));
		}

		@Override
		public ExportData exportSurveysInstancesToExcel(String token, SurveysInstanceQuery query) {
			if(query==null||query.surveysDefineId==null) {
				throw new AppException("请选择问卷调查");
			}
			int surveysDefineId=query.surveysDefineId;
			ExportData ret=new ExportData();
			Account account=bizService.getExistedAccountByToken(token);
			setupQuery(account, query);
			SurveysDefine define=dao.getExistedById(SurveysDefine.class, surveysDefineId);
			if(define.createAccountId!=account.id) {
				throw new AppException("权限不足");
			}
			List<String> headers=new ArrayList<>(Arrays.asList("编号","提交人","提交时间"));
			//
			SurveysFormDefine form = dao.getExistedById(SurveysFormDefine.class, define.formDefineId);
			List<SurveysFormField> fieldDefines = JSONUtil.fromJsonList(form.fieldList, SurveysFormField.class);
			List<SurveysFormField> importField=new ArrayList<>();
			if (fieldDefines != null) {
				for (SurveysFormField e : fieldDefines) {
					if(e.type.equals(SurveysFormField.TYPE_TABLE)||e.type.startsWith("static-")) {
						continue;//不导出表格和静态文本
					}
					headers.add(e.name);
					importField.add(e);
				}
			}
			//
			query.pageSize=10000;
			List<SurveysInstanceInfo> list=dao.getList(query);
			TableData data=new TableData();
			data.headers=headers;
			for (SurveysInstanceInfo e : list) {
				List<String> content=new ArrayList<>();
				content.add(e.id+"");
				content.add(e.createAccountName);
				content.add(DateUtil.formatDate(e.createTime));
				//
				Map<String,Object> formFieldValue=surveysService.getFormFieldValues(e);
				for (SurveysFormField fieldDefine : importField) {
					String fieldId=fieldDefine.id;
					String value=surveysService.getFormFieldValueForExport(fieldDefine,formFieldValue.get(fieldId));
					content.add(value);
				}
				//
				data.contents.add(content);
			}
			ret.fileName=define.name+"-"+DateUtil.formatDate(new Date(), "MMddHHmm")+".xlsx";
			ret.tableData=data;
			return ret;
		}
		//
		@Transaction
		@Override
		public Attachment addSurveysAttachment(String token,String surveysDefineUuid,Attachment bean) {
			Account account=bizService.getAccountByToken(token);
			SurveysDefine surveysDefine=dao.getExistedByUuid(SurveysDefine.class, surveysDefineUuid);
			if(account!=null) {
				bean.createAccountId=account.id;
			}
			bean.companyId=surveysDefine.companyId;
			BizUtil.checkValid(bean);
			dao.add(bean);
			return bean;
		}
		//
		@Transaction
		@Override
		public void resetSurveysInstances(String token, int surveysDefineId) {
			Account account=bizService.getAccountByToken(token);
			SurveysDefine old=dao.getExistedByIdForUpdate(SurveysDefine.class, surveysDefineId);
			if(old.createAccountId!=account.id) {
				throw new AppException("没有权限重置问卷调查");
			}
			surveysDAO.resetSurveysInstances(surveysDefineId);
			//
			old.submitCount=0;
			old.lastSubmitTime=null;
			dao.update(old);
			//
			bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_重置问卷调查, "name:"+old.name);
		}
	}
}
