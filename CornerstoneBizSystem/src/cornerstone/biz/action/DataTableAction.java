package cornerstone.biz.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.datatable.DataTableDefine.DataTableDefineQuery;
import cornerstone.biz.datatable.DataTableResult;
import cornerstone.biz.datatable.DataTableResult.XSheet;
import cornerstone.biz.datatable.JavaScriptDataTable;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.DataTable;
import cornerstone.biz.domain.DataTable.DataTableInfo;
import cornerstone.biz.domain.DataTable.DataTableQuery;
import cornerstone.biz.domain.DataTableLog;
import cornerstone.biz.domain.OptLog;
import cornerstone.biz.domain.Permission;
import cornerstone.biz.poi.ExcelUtils;
import cornerstone.biz.poi.TableDataInfo;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.srv.DataTableService;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.server.rpc.RpcService;
import jazmin.util.JSONUtil;

/**
 * 数据表格Action
 *
 */
@ApiDefine("数据表格接口")
public interface DataTableAction {

	@ApiDefine(value = "通过ID查询数据表格",resp = "数据表格信息",params = {"登录Token","数据表格ID"})
	DataTableInfo getDataTableById(String token, int id);

	@ApiDefine(value = "查询数据表列表和总数",resp = "数据表格信息集合",params = {"登录Token","表格信息查询类"})
	Map<String, Object> getDataTableList(String token,DataTableQuery query);

	@ApiDefine(value = "查询我的数据表格列表",resp = "数据表格列表",params = {"登录Token"})
	List<DataTableInfo> getMyDataTableList(String token);

	@ApiDefine(value = "新增数据表",resp = "数据表格ID",params = {"登录Token","数据表格信息"})
	int addDataTable(String token, DataTableInfo bean);

	@ApiDefine(value = "编辑数据表",params = {"登录Token","数据表格信息"})
	void updateDataTable(String token, DataTableInfo bean);

	@ApiDefine(value = "删除数据表",params = {"登录Token","数据表格ID"})
	void deleteDataTable(String token, int id);

	@ApiDefine(value = "启用禁用数据表",params = {"登录Token","数据表格ID","状态"})
	void updateDataTableStatus(String token, int id,int status);

	@ApiDefine(value = "执行数据表格",resp = "数据表格信息",params = {"登录Token","数据表格ID","数据表格定义查询类"})
	Map<String,Object> runDataTable(String token,int id,DataTableDefineQuery query);

	@ApiDefine(value = "导出excel",resp = "数据表格",params = {"登录Token","数据表格定义查询类"})
	TableDataInfo exportToExcel(String token,int id,DataTableDefineQuery query);

	@RpcService
	class DataTableActionImpl extends CommActionImpl implements DataTableAction {
		//
		@AutoWired
		BizDAO dao;
		@AutoWired
		BizService bizService;
		@AutoWired
		DataTableService dataTableService;//一定要有
		//
		//
		/**通过ID查询数据表*/
		@Override
		public DataTableInfo getDataTableById(String token, int id){
			bizService.getExistedAccountByToken(token);
//			bizService.checkCompanyPermission(account, Permission.ID_系统设置);
			DataTableInfo info=dao.getById(DataTableInfo.class, id);
			//20200131新增 每次都计算 因为有变量
			JavaScriptDataTable dataTable=new JavaScriptDataTable(info.script);
			info.dataTableDefine=dataTable.getDataTableDefine();
			return info;
		}

		/**查询数据表列表和总数*/
		@Override
		public Map<String, Object> getDataTableList(String token,DataTableQuery query){
			Account account=bizService.getExistedAccountByToken(token);
			bizService.checkCompanyPermission(account, Permission.ID_系统设置);
			setupQuery(account, query);
			return createResult(dao.getList(query,"dataTableDefine"), dao.getListCount(query));
		}

		/**查询数据表列表和总数*/

		@Override
		public List<DataTableInfo> getMyDataTableList(String token){
			Account account=bizService.getExistedAccountByToken(token);
			DataTableQuery query=new DataTableQuery();
			query.status=DataTable.STATUS_有效;
			setupQuery(account, query);
			query.pageSize=Integer.MAX_VALUE;
			List<DataTableInfo> allList=dao.getList(query,"dataTableDefine","script");
			List<DataTableInfo> list=new ArrayList<>();
			for (DataTableInfo e : allList) {
				if(bizService.isCompanySuperBoss(account)||bizService.checkAccountCompanyPermission(account.id,true,e.roles, null)) {
					list.add(e);
				}
			}
			return list;
		}

		/**新增数据表*/
		@Transaction
		@Override
		public int addDataTable(String token, DataTableInfo bean) {
			Account account=bizService.getExistedAccountByToken(token);
			bizService.checkCompanyPermission(account, Permission.ID_系统设置);
			bean.createAccountId=account.id;
			bean.companyId=account.companyId;
			bean.status=DataTableInfo.STATUS_有效;
			BizUtil.checkValid(bean);
			JavaScriptDataTable dataTable=new JavaScriptDataTable(bean.script);
			bean.dataTableDefine=dataTable.getDataTableDefine();
			dao.add(bean);
			bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_新增数据表格, "name:"+bean.name);
			return bean.id;
		}

		/**编辑数据表*/
		@Transaction
		@Override
		public void updateDataTable(String token, DataTableInfo bean) {
			Account account=bizService.getExistedAccountByToken(token);
			DataTable old=dao.getExistedByIdForUpdate(DataTable.class, bean.id);
			bizService.checkCompanyPermission(account, Permission.ID_系统设置,old.companyId);
			old.name=bean.name;
			//
			if(!old.script.equals(bean.script)) {//变更记录
				DataTableLog log=new DataTableLog();
				log.dataTableId=bean.id;
				log.companyId=bean.companyId;
				log.name=bean.name;
				log.group=bean.group;
				log.roles=bean.roles;
				log.enableRole=bean.enableRole;
				log.status=bean.status;
				log.isDelete=bean.isDelete;
				log.script=bean.script;
				log.remark=bean.remark;
				log.dataTableDefine=bean.dataTableDefine;
				log.createAccountId=account.id;
				dao.add(log);
			}
			//
			old.script=bean.script;
			old.remark=bean.remark;
			old.group=bean.group;
			old.roles=bean.roles;
			old.enableRole=bean.enableRole;
			old.updateAccountId=account.id;
			BizUtil.checkValid(old);
			//
			JavaScriptDataTable dataTable=new JavaScriptDataTable(bean.script);
			old.dataTableDefine=dataTable.getDataTableDefine();
			dao.update(old);
			//
			bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_编辑数据表格, "name:"+old.name);
		}

		/**删除数据表*/
		@Transaction
		@Override
		public void deleteDataTable(String token, int id) {
			Account account=bizService.getExistedAccountByToken(token);
			DataTable old=dao.getExistedByIdForUpdate(DataTable.class, id);
			if(old.isDelete) {
				return;
			}
			bizService.checkCompanyPermission(account, Permission.ID_系统设置,old.companyId);
			old.isDelete=true;
			old.updateAccountId=account.id;
			dao.update(old);
			//
			bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除数据表格, "name:"+old.name);
		}

		@Transaction
		@Override
		public void updateDataTableStatus(String token, int id,int status) {
			Account account=bizService.getExistedAccountByToken(token);
			DataTable old=dao.getExistedByIdForUpdate(DataTable.class,id);
			bizService.checkCompanyPermission(account, Permission.ID_系统设置,old.companyId);
			checkDataDictValueValid("DataTable.status", status, "状态错误");
			old.updateAccountId=account.id;
			old.status=status;
			dao.update(old);
		}

		@Override
		public Map<String, Object> runDataTable(String token, int id, DataTableDefineQuery query) {
			Account account=bizService.getExistedAccountByToken(token);
			return runDataTable0(account, id, query);
		}

		private Map<String, Object> runDataTable0(Account account, int id, DataTableDefineQuery query) {
			DataTable old=dao.getExistedById(DataTable.class,id);
			if(old.isDelete||old.status!=DataTable.STATUS_有效) {
				throw new AppException("数据不存在或已删除");
			}
			if(!bizService.isPrivateDeploy(account)) {//只有私有部署版才有权限
				throw new AppException("权限不足");
			}
			//权限校验
			if(old.enableRole) {
				if(!bizService.isCompanySuperBoss(account)){
					Set<Integer> myRoleIds=bizService.getMyCompanyRoleIdList(account.id, account.companyId);
					if(!BizUtil.contains(myRoleIds, BizUtil.convert(old.roles))) {
						throw new AppException("权限不足");
					}
				}
			}
			JavaScriptDataTable dataTable=new JavaScriptDataTable(old.script);
			dataTable.query=query;
			dataTable.account=account;
			dataTable.run(ConstDefine.GLOBAL_KEY);
			DataTableResult table=dataTable.getTable();
			DataTableResult.checkValid(table);
			//
			String json=JSONUtil.toJson(table);
			table=JSONUtil.fromJson(json, DataTableResult.class);
			//
			Map<String,Object> ret=new HashMap<>();
			ret.put("table", table);
			ret.put("messages", dataTable.messages);
			return ret;
		}

		@Override
		public TableDataInfo exportToExcel(String token, int id, DataTableDefineQuery query) {
			Account account=bizService.getExistedAccountByToken(token);
			Map<String, Object> result=runDataTable0(account, id, query);
			DataTableResult table=(DataTableResult) result.get("table");
			if(table.sheets==null||table.sheets.isEmpty()) {
				throw new AppException("sheet不能为空");
			}
			int index=1;
			for (XSheet sheet : table.sheets) {
				if(StringUtil.isEmpty(sheet.name)) {
					sheet.name="Sheet"+index;
				}
				index++;
			}
			DataTable dt=dao.getExistedById(DataTable.class, id);
			TableDataInfo info=new TableDataInfo();
			info.tableDatas=ExcelUtils.createTableDatas(table);
			info.fileName=dt.name;
			return info;
		}
	}
}
