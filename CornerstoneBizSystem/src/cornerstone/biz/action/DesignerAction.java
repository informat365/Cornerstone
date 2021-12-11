package cornerstone.biz.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import cornerstone.biz.annotations.ApiDefine;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.app.Velocity;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.DesignerColumn;
import cornerstone.biz.domain.DesignerColumnInfo;
import cornerstone.biz.domain.DesignerColumnRender;
import cornerstone.biz.domain.DesignerColumnRender.DataDictValue;
import cornerstone.biz.domain.DesignerDatabase;
import cornerstone.biz.domain.DesignerDatabaseChangeLog.DesignerDatabaseChangeLogInfo;
import cornerstone.biz.domain.DesignerDatabaseChangeLog.DesignerTableChangeLogQuery;
import cornerstone.biz.domain.DesignerDatabaseInfo;
import cornerstone.biz.domain.DesignerMysqlProxyInstance;
import cornerstone.biz.domain.DesignerMysqlProxyInstance.DesignerMysqlProxyInstanceInfo;
import cornerstone.biz.domain.DesignerMysqlProxyInstance.DesignerMysqlProxyInstanceQuery;
import cornerstone.biz.domain.DesignerTable;
import cornerstone.biz.domain.DesignerTableRender;
import cornerstone.biz.domain.DesignerTableRender.UniqueIndex;
import cornerstone.biz.domain.DesignerTemplate;
import cornerstone.biz.domain.DesignerTemplateChangeLog;
import cornerstone.biz.domain.DesignerTemplateInfo;
import cornerstone.biz.domain.GenerateCodeReq;
import cornerstone.biz.domain.GenerateCodeResp;
import cornerstone.biz.domain.KeyValue;
import cornerstone.biz.domain.Permission;
import cornerstone.biz.domain.query.DesignerDatabaseInfoQuery;
import cornerstone.biz.domain.query.DesignerTemplateInfoQuery;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.srv.MysqlProxyService;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DataBaseCache;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.util.GenCodeUtil;
import cornerstone.biz.util.JdbcUtil.ColumnInfo;
import cornerstone.biz.util.JdbcUtil.IndexInfo;
import cornerstone.biz.util.JdbcUtil.TableInfo;
import cornerstone.biz.util.StringUtil;
import cornerstone.biz.util.TripleDESUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;

/**
 * 代码助手Action
 * @author cs
 *
 */
@ApiDefine("代码助手接口")
public interface DesignerAction{

	@ApiDefine(value = "查询设计数据库列表",resp = "设计数据库集合信息",params = {"登录Token","设计数据库查询类"})
	Map<String, Object> getDesignerDatabases(String token, DesignerDatabaseInfoQuery query);

	@ApiDefine(value = "查询我的设计数据库列表 不分页 ",resp = "设计数据库列表",params = {"登录Token"})
	List<DesignerDatabaseInfo> getMyDesignerDatabases(String token);

	/***/
	@ApiDefine(value = "获取设计数据库详情",resp = "设计数据库信息",params = {"登录Token","设计数据库ID"})
	DesignerDatabaseInfo getDesignerDatabaseById(String token, int id);

	@ApiDefine(value = "增加设计数据库",resp = "设计数据库ID",params = {"登录Token","设计数据库"})
	int addDesignerDatabase(String token, DesignerDatabase bean);

	@ApiDefine(value = "编辑设计数据库",params = {"登录Token","设计数据库"})
	void updateDesignerDatabase(String token, DesignerDatabase bean);

	@ApiDefine(value = "删除设计数据库",params = {"登录Token","设计数据库ID"})
	void deleteDesignerDatabase(String token, int id);

	@ApiDefine(value = "查询设计模板列表",params = {"登录Token","设计模板查询类"},resp = "设计模板集合信息")
	Map<String, Object> getDesignerTemplates(String token, DesignerTemplateInfoQuery query);

	@ApiDefine(value = "获取设计模板信息",params = {"登录Token","设计模板ID"},resp = "设计模板")
	DesignerTemplateInfo getDesignerTemplateById(String token, int id);

	@ApiDefine(value = "增加设计模板",params = {"登录Token","设计模板"},resp = "设计模板ID")
	int addDesignerTemplate(String token, DesignerTemplate bean);

	@ApiDefine(value = "编辑设计模板",params = {"登录Token","设计模板"})
	void updateDesignerTemplate(String token, DesignerTemplate bean);

	@ApiDefine(value = "删除设计模板",params = {"登录Token","设计模板ID"})
	void deleteDesignerTemplate(String token, int id);

	@ApiDefine(value = "刷新设计数据库",params = {"登录Token","设计数据库ID"})
	void refreshDesignerDatabase(String token,int designerDatabaseId);

	@ApiDefine(value = "查询表列表",params = {"登录Token","设计数据库ID"},resp = "表集合信息")
	Map<String, Object> getTableInfos(String token, int designerDatabaseId);

	@ApiDefine(value = "查询字段列表",params = {"登录Token","设计数据库ID","表名称","是否需要MetaData"},resp = "字段列表")
	List<DesignerColumnInfo> getDesignerColumns(String token, int designerDatabaseId, String table,
			boolean needMetaData);

	@ApiDefine(value = "查询所有字段列表",params = {"登录Token","设计数据库ID"},resp = "字段列表")
	List<DesignerColumnInfo> getAllDesignerColumns(String token, int designerDatabaseId);

	@ApiDefine(value = "新增外键字段",params = {"登录Token","列信息"})
	void addForeignColumn(String token, DesignerColumn bean);

	@ApiDefine(value = "新增关联字段",params = {"登录Token","列信息"})
	void addRelationColumn(String token, DesignerColumn bean);

	@ApiDefine(value = "编辑表",params = {"登录Token","表信息"})
	void updateDesignerTable(String token, DesignerTable bean);

	@ApiDefine(value = "编辑字段",params = {"登录Token","列信息"})
	void updateDesignerColumn(String token, DesignerColumn bean);

	@ApiDefine(value = "移动列",params = {"登录Token","列ID","移动状态"})
	void moveDesignerColumn(String token, int id, int delta);

	@ApiDefine(value = "重置字段顺序",params = {"登录Token","表ID","表名称","排序列集合"})
	void resetDesignerColumnListOrder(String token,int designerDatabaseId,String tableName,List<Integer> sortedColumnIds);

	@ApiDefine(value = "删除字段",params = {"登录Token","字段ID"})
	void deleteDesignerColumn(String token, int id);

	@ApiDefine(value = "生成代码",params = {"登录Token","代码请求"},resp = "代码列表")
	List<GenerateCodeResp> generateCode(String token, GenerateCodeReq req) ;

	@ApiDefine(value = "通过ID查询MYSQL代理",params = {"登录Token","数据库ID"},resp = "MYSQL代理")
	DesignerMysqlProxyInstanceInfo getDesignerMysqlProxyInstanceById(String token, int id);

	@ApiDefine(value = "查询MYSQL代理列表和总数",params = {"登录Token","MYSQL代理"},resp = "MYSQL代理信息")
	Map<String, Object> getDesignerMysqlProxyInstanceList(String token,DesignerMysqlProxyInstanceQuery query);

	@ApiDefine(value = "新增MYSQL代理",params = {"登录Token","MYSQL代理"},resp = "MYSQL代理ID")
	int addDesignerMysqlProxyInstance(String token, DesignerMysqlProxyInstanceInfo bean);

	@ApiDefine(value = "编辑MYSQL代理",params = {"登录Token","MYSQL代理"})
	void updateDesignerMysqlProxyInstance(String token, DesignerMysqlProxyInstanceInfo bean);

	@ApiDefine(value = "删除MYSQL代理",params = {"登录Token","MYSQL代理ID","原因"})
	void deleteDesignerMysqlProxyInstance(String token, int id, String reason);

	@ApiDefine(value = "通过ID查询表变更记录",params = {"登录Token","设计数据库变更ID"},resp = "设计数据库变更信息")
	DesignerDatabaseChangeLogInfo getDesignerTableChangeLogById(String token, int id);

	@ApiDefine(value = "查询表变更记录列表和总数",params = {"登录Token","设计数据库变更查询类"},resp = "设计数据库变更信息集合")
	Map<String, Object> getDesignerTableChangeLogList(String token,DesignerTableChangeLogQuery query);
	//
	public static class DesignerActionImpl extends CommActionImpl implements DesignerAction{
		//
		private static Logger logger = LoggerFactory.get(DesignerActionImpl.class);
		//
		@AutoWired
		BizService bizService;
		@AutoWired
		MysqlProxyService mysqlProxyService;
		//
		@Override
		public Map<String, Object> getDesignerDatabases(String token, DesignerDatabaseInfoQuery query) {
			Class<?> domainClass=getQueryDomainClass(query);
			beforeGetListAndCount(token,domainClass,query,null);
			Map<String, Object> result = createResult(dao.getList(query,"dbPassword"), dao.getListCount(query));
			afterGetListAndCount(token,domainClass,query, result);
			return result;
		}

		@Override
		public List<DesignerDatabaseInfo> getMyDesignerDatabases(String token) {
			Account account=checkPermission(token,null);
			DesignerDatabaseInfoQuery query=new DesignerDatabaseInfoQuery();
			setupQuery(account, query);
			List<DesignerDatabaseInfo> all=dao.getAll(query,"dbPassword");
			List<DesignerDatabaseInfo> list=new ArrayList<>();
			for (DesignerDatabaseInfo e : all) {
				if(bizService.checkAccountCompanyPermission(account.id,true, e.roles, e.members)) {
					list.add(e);
				}
			}
			return list;
		}

		@Override
		public DesignerDatabaseInfo getDesignerDatabaseById(String token, int id) {
			return getById(token, id, DesignerDatabaseInfo.class,null);
		}

		@Transaction
		@Override
		public int addDesignerDatabase(String token, DesignerDatabase bean) {
			Account account=checkPermission(token,Permission.ID_数据库配置);
			bean.companyId=account.companyId;
			BizUtil.checkValid(bean);
			BizUtil.checkUniqueKeysOnAdd(dao, bean);
			bean.dbPassword=TripleDESUtil.encrypt(bean.dbPassword, ConstDefine.GLOBAL_KEY);
			return dao.add(bean);
		}

		@Transaction
		@Override
		public void updateDesignerDatabase(String token, DesignerDatabase bean) {
			Account account=checkPermission(token,Permission.ID_数据库配置);
			DesignerDatabase old=dao.getExistedByIdForUpdate(DesignerDatabase.class,bean.id);
			BizUtil.checkUniqueKeysOnUpdate(dao, bean, old);
			old.name=bean.name;
			old.host=bean.host;
			old.dbUser=bean.dbUser;
			old.dbType=bean.dbType;
			old.instanceId=bean.instanceId;
			old.packageName=bean.packageName;
			old.dmlTables=bean.dmlTables;
			old.roles=bean.roles;
			old.members=bean.members;
			old.updateAccountId=account.id;
			if(bean.dbPassword!=null&&!bean.dbPassword.equals(old.dbPassword)) {//修改密码
				old.dbPassword=TripleDESUtil.encrypt(bean.dbPassword, ConstDefine.GLOBAL_KEY);
			}
			BizUtil.checkValid(old);
			dao.update(old);
		}

		@Override
		public void deleteDesignerDatabase(String token, int id) {
			deleteById(token, id, "", DesignerDatabase.class,Permission.ID_数据库配置);
		}

		@Override
		public Map<String, Object> getDesignerTemplates(String token, DesignerTemplateInfoQuery query) {
			return getListAndCount(token, query,null);
		}

		@Override
		public DesignerTemplateInfo getDesignerTemplateById(String token, int id) {
			return getById(token, id, DesignerTemplateInfo.class,null);
		}

		@Transaction
		@Override
		public int addDesignerTemplate(String token, DesignerTemplate bean) {
			return add(token, bean,Permission.ID_模板配置);
		}

		@Transaction
		@Override
		public void updateDesignerTemplate(String token, DesignerTemplate bean) {
			updateDesignerTemplate0(token, bean);
		}

		private synchronized void updateDesignerTemplate0(String token, DesignerTemplate bean) {
			Account account=bizService.getExistedAccountByToken(token);
			DesignerTemplate old=dao.getById(DesignerTemplate.class, bean.id);
			DesignerTemplateChangeLog log=new DesignerTemplateChangeLog();
			log.designerTemplateId=old.id;
			log.name=old.name;
			log.language=old.language;
			log.content=old.content;
			log.createAccountId=account.id;
			dao.add(log);
			//
			update(token, bean, Permission.ID_模板配置);
		}

		@Transaction
		@Override
		public void deleteDesignerTemplate(String token, int id) {
			DesignerTemplate designerTemplate=dao.getExistedById(DesignerTemplate.class, id);
			if(designerTemplate!=null) {
				deleteById(token, id, "", DesignerTemplate.class,Permission.ID_模板配置);
			}
		}

		@Override
		public Map<String, Object> getTableInfos(String token, int designerDatabaseId) {
			checkPermission(token,Permission.ID_代码设计器);
			//
			Map<String, Object> result = new HashMap<>();
			DesignerDatabase bean = dao.getExistedById(DesignerDatabase.class, designerDatabaseId);
			List<TableInfo> tableInfos=DataBaseCache.getTableInfos(bean);
			List<DesignerTable> designerTables = dao.getAll(DesignerTable.class,
					QueryWhere.create().where("designer_database_id", designerDatabaseId));
			for (TableInfo tableInfo : tableInfos) {
				DesignerTable table=dao.getDomain(DesignerTable.class,
						QueryWhere.create().
						where("designer_database_id", designerDatabaseId).
						where("name", tableInfo.name).forUpdate());
				if(table!=null) {
					if(StringUtil.isEmpty(table.displayName)) {
						table.displayName=tableInfo.remarks;
						if(table.displayName!=null&&table.displayName.length()>10) {
							table.displayName=table.displayName.substring(0,10);
						}
						dao.update(table);
						continue;
					}
				}
				if(!BizUtil.contains(designerTables, "name", tableInfo.name)) {
					if(table!=null) {
						continue;
					}
					table=new DesignerTable();
					table.designerDatabaseId=designerDatabaseId;
					table.name=tableInfo.name;
					table.displayName=tableInfo.remarks;
					if(table.displayName!=null&&table.displayName.length()>10) {
						table.displayName=table.displayName.substring(0,10);
					}
					table.isShowDetailPage=true;
					table.isCanUpdate=true;
					table.isFormInline=true;
					DesignerColumn sortColumn=dao.getDomain(DesignerColumn.class,
							QueryWhere.create().
							where("designer_database_id", designerDatabaseId).
							where("table_name", table.name).
							where("column_name", "sort_weight"));
					if(sortColumn!=null) {
						table.isSortable=true;
					}
					dao.add(table);
					designerTables.add(table);
				}
			}
			for (DesignerTable e : designerTables) {
				if(!BizUtil.contains(tableInfos, "name", e.name)) {
					dao.deleteById(DesignerTable.class, e.id);
					dao.delete(DesignerColumn.class, QueryWhere.create().
							where("table_name", e.name).
							where("designer_database_id", e.designerDatabaseId));
				}
			}
			result.put("tableInfos", designerTables);
			List<ColumnInfo> columnInfos = DataBaseCache.getColumns(bean);
			result.put("columnInfos", columnInfos);
			return result;
		}

		//
		DesignerColumn getDesignerColumnByName(List<DesignerColumn> designerColumns, String name) {
			for (DesignerColumn e : designerColumns) {
				if (e.columnName.equals(name)) {
					return e;
				}
			}
			return null;
		}

		@Transaction
		@Override
		public List<DesignerColumnInfo> getAllDesignerColumns(String token, int designerDatabaseId){
			checkPermission(token,Permission.ID_代码设计器);
			return dao.getAll(DesignerColumnInfo.class,
					QueryWhere.create().
					where("designer_database_id", designerDatabaseId).
					orderBy("`order` asc"));
		}

		@Transaction
		@Override
		public List<DesignerColumnInfo> getDesignerColumns(String token, int designerDatabaseId, String tableName,
				boolean needMetaData) {
			checkPermission(token,Permission.ID_代码设计器);
			//
			if (!needMetaData) {
				return dao.getAll(DesignerColumnInfo.class,
						QueryWhere.create().where("designer_database_id", designerDatabaseId)
								.where("table_name", tableName).orderBy("`order` asc"));
			}
			DesignerDatabase bean = dao.getExistedById(DesignerDatabase.class, designerDatabaseId);
			List<ColumnInfo> list = DataBaseCache.getColumnInfos(bean, tableName);
			List<DesignerColumn> designerColumns = getDesignerColumns(designerDatabaseId, tableName);
			for (DesignerColumn e : designerColumns) {
				if (!BizUtil.contains(list, "name", e.columnName) && e.fieldType == DesignerColumn.FIELD_TYPE_DB) {
					dao.deleteById(DesignerColumn.class, e.id);
				}
			}
			int order = 1;
			for (ColumnInfo columnInfo : list) {
				DesignerColumn designerColumn = getDesignerColumnByName(designerColumns, columnInfo.name);
				String columnName = columnInfo.name;
				if (designerColumn == null) {
					DesignerColumn column = new DesignerColumn();
					column.designerDatabaseId = designerDatabaseId;
					column.tableName = tableName;
					column.columnName = columnInfo.name;
					column.domainName = GenCodeUtil.getDomainField(column.columnName);
					column.dbDef = columnInfo.columnDef;
					column.dbType = columnInfo.type.toUpperCase();
					column.dbSize = columnInfo.dataSize;
					column.dbIsAutoincrement = columnInfo.isAutoincrement;
					column.dbNullable = columnInfo.nullable;
					column.comment = columnInfo.remarks;
					if(StringUtil.isEmpty(column.comment)){
						column.comment=column.domainName;
					}else {
						if(column.comment.length()>10) {
							column.comment=column.comment.substring(0, 10);
						}
					}
					column.fieldType = DesignerColumn.FIELD_TYPE_DB;
					column.displayName = column.comment;
					column.isQuery = isQuery(column);
					column.isRangeQuery=isRangeQuery(column);
					column.isShowInList = isShowInList(column);
					column.isRequire = "NO".equals(columnInfo.nullable);
					column.isShowInDetailPage=isShowInDetailPage(column);
					column.isCanModify = isCanModify(columnName);
					column.maxValue = getMaxSize(columnInfo.type, columnInfo.dataSize);
					column.showType = getShowType(columnInfo, columnInfo.name);
					column.order = order++;
					dao.add(column);
				} else {
					designerColumn.dbDef = columnInfo.columnDef;
					designerColumn.dbType = columnInfo.type.toUpperCase();
					designerColumn.dbSize = columnInfo.dataSize;
					designerColumn.dbIsAutoincrement = columnInfo.isAutoincrement;
					designerColumn.dbNullable = columnInfo.nullable;
					designerColumn.comment = columnInfo.remarks;
					dao.update(designerColumn);
				}
			}
			return dao.getAll(DesignerColumnInfo.class,
					QueryWhere.create().where("designer_database_id", designerDatabaseId).where("table_name", tableName)
							.orderBy("`order` asc"));
		}

		private boolean isShowInList(DesignerColumn column) {
			GenCodeUtil.getJavaType(column);
			if("update_time".equals(column.columnName)) {
				return true;
			}
			return true;
		}

		private boolean isShowInDetailPage(DesignerColumn column) {
			GenCodeUtil.getJavaType(column);
			if("update_time".equals(column.columnName)) {
				return true;
			}
			return true;
		}

		private boolean isQuery(DesignerColumn column) {
			if("id".equals(column.columnName)) {
				return true;
			}
			if(!StringUtil.isEmpty(column.dataDict)) {
				return true;
			}
			if("VARCHAR".equals(column.dbType)) {
				if(column.dbSize>64) {
					return false;
				}
				String[] excludes=new String[] {"image_id","password","headimgurl","session_key"};
				for (String columnName : excludes) {
					if(column.columnName.equals(columnName)) {
						return false;
					}
				}
				return true;
			}
			return false;
		}

		private boolean isRangeQuery(DesignerColumn column) {
			String[] names=new String[] {"price","rank"};
			for (String columnName : names) {
				if(column.columnName.equals(columnName)) {
					return true;
				}
			}
			return false;
		}

		private int getMaxSize(String dbType, int dbSize) {
			if ("VARCHAR".equalsIgnoreCase(dbType)) {
				return dbSize;
			}
			return 0;
		}

		private boolean contains(String[] currency, String target) {
			for (String e : currency) {
				if (target.indexOf(e) != -1) {
					return true;
				}
			}
			return false;
		}

		private int getShowType(ColumnInfo column, String columnName) {
			if (column != null) {
				if ("DATETIME".equalsIgnoreCase(column.type) || "TIMESTAMP".equalsIgnoreCase(column.type)) {
					return DesignerColumn.SHOW_TYPE_日期时间;
				}
				if ("DATE".equalsIgnoreCase(column.type)) {
					return DesignerColumn.SHOW_TYPE_日期;
				}
				if ("INT".equalsIgnoreCase(column.type) || "TINYINT".equalsIgnoreCase(column.type)) {
					if (column.name.endsWith("type") || column.name.endsWith("status")) {
						return DesignerColumn.SHOW_TYPE_单选框;
					}
				}
				if ("TINYINT".equalsIgnoreCase(column.type)&&column.name.startsWith("is")) {
					return DesignerColumn.SHOW_TYPE_开关;
				}
				if ("TEXT".equalsIgnoreCase(column.type)) {
					return DesignerColumn.SHOW_TYPE_富文本;
				}
			}
			String[] currency = { "money", "amount", "price" };
			if (contains(currency, columnName)) {
				return DesignerColumn.SHOW_TYPE_货币分;
			}
			String[] images = { "image","logo" };
			if (contains(images, columnName)) {
				return DesignerColumn.SHOW_TYPE_图片小;
			}
			String[] passwords = { "password" };
			if (contains(passwords, columnName)) {
				return DesignerColumn.SHOW_TYPE_密码;
			}
			String[] mobiles = { "mobile" };
			if (contains(mobiles, columnName)) {
				return DesignerColumn.SHOW_TYPE_手机号;
			}
			String[] mails = { "mail" };
			if (contains(mails, columnName)) {
				return DesignerColumn.SHOW_TYPE_邮箱;
			}
			if (column != null) {
				if ("INT".equalsIgnoreCase(column.type) || "TINYINT".equalsIgnoreCase(column.type)||
						"BIGINT".equalsIgnoreCase(column.type)|| "SMALLINT".equalsIgnoreCase(column.type)||
						column.type.indexOf("FLOAT")!=-1||column.type.indexOf("DOUBLE")!=-1) {
					return DesignerColumn.SHOW_TYPE_数字;
				}
			}
			return DesignerColumn.SHOW_TYPE_字符串;
		}

		private boolean isCanModify(String columnName) {
			String[] list = { "id", "create_time", "update_time", "create_user_id", "update_user_id" };
			for (String e : list) {
				if (e.equals(columnName)) {
					return false;
				}
			}
			return true;
		}

		@Transaction
		@Override
		public void updateDesignerTable(String token, DesignerTable bean) {
			update(token, bean,Permission.ID_代码设计器);
		}

		@Transaction
		@Override
		public void updateDesignerColumn(String token, DesignerColumn bean) {
			updateDesignerColumn0(token, bean);
		}

		private synchronized void updateDesignerColumn0(String token, DesignerColumn bean) {
			update(token, bean,Permission.ID_代码设计器);
			// 数据字典
			if (!StringUtil.isEmpty(bean.dataDict)) {
				if (bean.dataDict.indexOf(":") != -1) {
					String[] values = bean.dataDict.split(",");
					List<KeyValue> keyValues = new ArrayList<>();
					for (String e : values) {
						KeyValue keyValue = new KeyValue();
						String[] keyVal = e.split(":");
						if (keyVal == null || keyVal.length != 2) {
							throw new IllegalArgumentException("数据字典值格式错误 " + bean.dataDict);
						}
						keyValue.key = keyVal[0];
						keyValue.value = Integer.valueOf(keyVal[1]);
						keyValues.add(keyValue);
					}
					if (keyValues.size() <= 0) {
						throw new IllegalArgumentException("数据字典值格式错误eg:进行中:1,未进行:2");
					}
				}
//				else {// 存在的数据字典
//
//				}
			}
		}

		@Transaction
		@Override
		public void moveDesignerColumn(String token, int id, int delta) {
			checkPermission(token,Permission.ID_代码设计器);
			moveDesignerColumn0(token, id, delta);
		}

		private synchronized void moveDesignerColumn0(String token, int id, int delta) {
			DesignerColumn bean = dao.getById(DesignerColumn.class, id);
			List<DesignerColumn> columns = dao.getAll(DesignerColumn.class,
					QueryWhere.create().where("table_name", bean.tableName).
					orderBy("`order` asc"));
			int index = 0;
			for (int i = 0; i < columns.size(); i++) {
				DesignerColumn c = columns.get(i);
				if (c.id == id) {
					if (delta > 0) {// 下移 order++
						if (index == columns.size() - 2) {
							return;
						}
						columns.add(index + 2, c);
						columns.remove(index);
						break;
					} else if (delta < 0) {// 上移 order--
						if (index < 1) {
							return;
						}
						columns.add(index - 1, c);
						columns.remove(index + 1);
						break;
					}
				}
				index++;
			}
			reloadDesignerColumnOrders(columns);
		}

		private void reloadDesignerColumnOrders(List<DesignerColumn> columns) {
			int order = 1;
			for (DesignerColumn e : columns) {
				e.order = order++;
				dao.update(e);
			}
		}

		@Transaction
		@Override
		public void resetDesignerColumnListOrder(String token,int designerDatabaseId,String tableName,List<Integer> sortedColumnIds) {
			checkPermission(token,Permission.ID_代码设计器);
			List<DesignerColumn> list=getDesignerColumns(designerDatabaseId, tableName);
			int order = 1;
			for (int id: sortedColumnIds) {
				for (DesignerColumn e : list) {
					if(e.id==id) {
						e.order=order++;
						dao.update(e);
						break;
					}
				}
			}
		}

		private List<DesignerColumn> getDesignerColumns(int designerDatabaseId, String tableName) {
			return dao.getAll(DesignerColumn.class,
					QueryWhere.create().where("designer_database_id", designerDatabaseId).where("table_name", tableName)
							.orderBy("`order` asc"));
		}

		private List<DesignerColumnInfo> getDesignerColumnInfos(int designerDatabaseId, String tableName) {
			return dao.getAll(DesignerColumnInfo.class,
					QueryWhere.create().where("designer_database_id", designerDatabaseId).where("table_name", tableName)
							.orderBy("`order` asc"));
		}

		@Transaction
		@Override
		public void addForeignColumn(String token, DesignerColumn bean) {
			addForeignColumn0(token, bean);
		}

		private synchronized void addForeignColumn0(String token, DesignerColumn bean) {
			//t_test表的createUserId 注释	创建人
			DesignerColumn relationColumn = dao.getById(DesignerColumn.class, bean.relationDesignerColumnId);// 外键字段(同一张表)
			if (relationColumn == null || relationColumn.fieldType != DesignerColumn.FIELD_TYPE_DB
					|| StringUtil.isEmpty(relationColumn.foreignTableName)) {
				throw new AppException("找不到外键字段");
			}
			//t_user表的name 注释 名称
			DesignerColumn foreignKeyColumn = dao.getDomain(DesignerColumn.class,
					QueryWhere.create().where("designer_database_id", relationColumn.designerDatabaseId)
							.where("table_name", relationColumn.foreignTableName)
							.where("column_name", bean.foreignColumnName));
			if (foreignKeyColumn == null) {
				throw new AppException(relationColumn.foreignTableName + "表未初始化");
			}
			if (StringUtil.isEmpty(bean.foreignColumnName)) {
				throw new AppException("关联表字段不能为空");
			}
			if (StringUtil.isEmpty(bean.domainName)) {
				throw new AppException("domain名称不能为空");
			}
			List<DesignerColumn> list = getDesignerColumns(relationColumn.designerDatabaseId, relationColumn.tableName);
			//
			bean.designerDatabaseId = relationColumn.designerDatabaseId;
			bean.tableName = relationColumn.tableName;
			bean.columnName = bean.domainName;
			bean.foreignTableName = relationColumn.foreignTableName;
			bean.fieldType = DesignerColumn.FIELD_TYPE_FOREIGN;
			bean.isQuery = true;
			bean.isShowInList = true;
			bean.isRangeQuery=false;
			bean.dbDef = foreignKeyColumn.dbDef;
			bean.dbIsAutoincrement = foreignKeyColumn.dbIsAutoincrement;
			bean.dbNullable = foreignKeyColumn.dbNullable;
			bean.dbSize = foreignKeyColumn.dbSize;
			bean.dbType = foreignKeyColumn.dbType.toUpperCase();
			bean.displayName = relationColumn.displayName+foreignKeyColumn.displayName;
			bean.comment = bean.displayName;
			bean.maxValue = getMaxSize(bean.dbType, bean.dbSize);
			bean.showType = getShowType(null, bean.foreignColumnName);
			add(token, bean,Permission.ID_代码设计器);
			//
			int index = 0;
			for (DesignerColumn e : list) {
				if (e.id == relationColumn.id) {
					list.add(index + 1, bean);
					break;
				}
				index++;
			}
			reloadDesignerColumnOrders(list);
		}


		@Transaction
		@Override
		public void addRelationColumn(String token, DesignerColumn bean) {
			addRelationColumn0(token, bean);
		}

		/**
		 * 举个例子店铺有多张图片(t_shop表t_shop_image表)
		 * 在t_shop表的id字段那里，增加关联t_shop_image表的shopId字段
		 * @param token
		 * @param bean
		 */
		private synchronized void addRelationColumn0(String token, DesignerColumn bean) {
			//t_shop的id
			DesignerColumn relationColumn = dao.getById(DesignerColumn.class, bean.relationDesignerColumnId);// 外键字段(同一张表)
			if (relationColumn == null || relationColumn.fieldType != DesignerColumn.FIELD_TYPE_DB) {
				throw new AppException("找不到关联字段");
			}
			DesignerDatabase database=dao.getById(DesignerDatabase.class,relationColumn.designerDatabaseId);
			if(database==null) {
				throw new AppException("参数错误");
			}
			//检查t_shop_image表是否存在
			TableInfo tableInfo=DataBaseCache.getTableInfo(database, bean.relationTableName);
			if(tableInfo==null) {
				throw new AppException("找不到关联表");
			}
			//检查t_shop_image表的shopId是否存在(这是t_shop_image表的外键)
			DesignerColumn foreignColumn = dao.getDomain(DesignerColumn.class,
					QueryWhere.create().where("designer_database_id", relationColumn.designerDatabaseId)
							.where("table_name", bean.relationTableName)
							.where("column_name", bean.relationColumnName));
			if (foreignColumn == null) {
				throw new AppException(bean.relationTableName + "表未初始化");
			}
			if (StringUtil.isEmpty(bean.relationColumnName)) {
				throw new AppException("关联表字段不能为空");
			}
			//查到t_shop表的所有字段
			List<DesignerColumn> list = getDesignerColumns(foreignColumn.designerDatabaseId, relationColumn.tableName);
			//
			bean.designerDatabaseId = relationColumn.designerDatabaseId;
			bean.tableName = relationColumn.tableName;
			bean.columnName = bean.domainName;
			if(!StringUtil.isEmpty(bean.targetTableName)&&!StringUtil.isEmpty(bean.targetColumnName)) {
				bean.fieldType = DesignerColumn.FIELD_TYPE_MULTIPE_RELATION;
				TableInfo tagetTableInfo=DataBaseCache.getTableInfo(database, bean.targetTableName);
				if(tagetTableInfo==null) {
					throw new AppException("找不到目标表");
				}
				bean.domainName="rel"+GenCodeUtil.getClassName(tableInfo.name)+GenCodeUtil.getClassName(tagetTableInfo.name)+"List";
			}else {
				bean.fieldType = DesignerColumn.FIELD_TYPE_RELATION;
				bean.domainName="rel"+GenCodeUtil.getClassName(tableInfo.name)+"List";
			}

			bean.columnName=bean.domainName;
			bean.isQuery = false;
			bean.isRangeQuery=false;
			bean.isShowInList = false;
			bean.isShowInDetailPage=true;
			bean.dbDef = foreignColumn.dbDef;
			bean.dbIsAutoincrement = foreignColumn.dbIsAutoincrement;
			bean.dbNullable = foreignColumn.dbNullable;
			bean.dbSize = foreignColumn.dbSize;
			bean.dbType = foreignColumn.dbType.toUpperCase();
			bean.displayName = foreignColumn.displayName;
			bean.comment = bean.displayName;
			bean.maxValue = getMaxSize(bean.dbType, bean.dbSize);
			bean.showType = getShowType(null, bean.relationColumnName);
			add(token, bean,Permission.ID_代码设计器);
			//
			int index = 0;
			for (DesignerColumn e : list) {
				if (e.id == relationColumn.id) {
					list.add(index + 1, bean);
					break;
				}
				index++;
			}
			reloadDesignerColumnOrders(list);
		}

		@Transaction
		@Override
		public void deleteDesignerColumn(String token, int id) {
			DesignerColumn old = dao.getByIdForUpdate(DesignerColumn.class, id);
			if (old.fieldType == DesignerColumn.FIELD_TYPE_DB) {
				throw new AppException("字段类型为DB的不能删除");
			}
			deleteById(token, id, "", DesignerColumn.class,Permission.ID_代码设计器);
		}

		@Override
		public void refreshDesignerDatabase(String token,int designerDatabaseId) {
			Account account=bizService.getExistedAccountByToken(token);
			bizService.checkCompanyPermission(account, Permission.ID_代码设计器);
			DesignerDatabase database = dao.getExistedById(DesignerDatabase.class,designerDatabaseId);
			DataBaseCache.reload(database);
			getTableInfos(token, designerDatabaseId);
		}

		@Override
		public List<GenerateCodeResp> generateCode(String token, GenerateCodeReq req)  {
			try {
				return generateCode0(token, req);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			return Collections.emptyList();
		}

		private synchronized List<GenerateCodeResp> generateCode0(String token, GenerateCodeReq req) throws IOException {
			Account account=checkPermission(token,Permission.ID_代码设计器);
			//
			List<GenerateCodeResp> result = new ArrayList<>();
			//
			DesignerDatabase database = dao.getExistedById(DesignerDatabase.class, req.designerDatabaseId);
			//table
			DesignerTable tableInfo = dao.getDomain(DesignerTable.class, QueryWhere.create().
					where("designer_database_id", req.designerDatabaseId).where("name", req.tableName));
			//唯一索引
			Map<String,List<IndexInfo>> uniqueIndexMap=DataBaseCache.getUniqueIndexMap(database,req.tableName);
			for (Map.Entry<String,List<IndexInfo>> e : uniqueIndexMap.entrySet()) {
				if(e!=null&&e.getKey()!=null&& "PRIMARY".equalsIgnoreCase(e.getKey())) {
					uniqueIndexMap.remove(e.getKey());
					break;
				}
			}
			List<UniqueIndex> uniqueIndexs=new ArrayList<>();
			uniqueIndexMap.forEach((k,v)->{
				UniqueIndex uniqueIndex=new UniqueIndex();
				uniqueIndex.name=k;
				for (IndexInfo e : v) {
					if(e.columnName==null) {
						continue;
					}
					uniqueIndex.domainNames.add(GenCodeUtil.getDomainField(e.columnName));
				}
				uniqueIndexs.add(uniqueIndex);
			});
			DesignerTableRender tableRender = new DesignerTableRender();
			tableRender.author = account.name;
			tableRender.createTime = DateUtil.formatDate(new Date());
			tableRender.tableName = req.tableName;
			tableRender.domainName = GenCodeUtil.getClassName(req.tableName);
			tableRender.packageName = database.packageName;
			tableRender.displayName = tableInfo.displayName;
			tableRender.isShowDetailPage = tableInfo.isShowDetailPage;
			tableRender.isCanUpdate = tableInfo.isCanUpdate;
			tableRender.isShowSelect=tableInfo.isShowSelect;
			tableRender.isFormInline=tableInfo.isFormInline;
			tableRender.isSortable=tableInfo.isSortable;
			if(!StringUtil.isEmpty(tableRender.displayName)) {
				if(tableRender.displayName.length()>12) {
					tableRender.displayName=tableRender.displayName.substring(0,12);
				}
			}
			tableRender.uniqueIndexs=uniqueIndexs;
			//column
			List<DesignerColumnRender> columnRenders = new ArrayList<>();
			List<DesignerColumnInfo> designerColumns = getDesignerColumnInfos(req.designerDatabaseId, req.tableName);
			for (DesignerColumn designerColumn : designerColumns) {
				DesignerColumnRender columnRender=createDesignerColumnRender(designerColumn, req.designerDatabaseId, tableRender);
				columnRenders.add(columnRender);
			}
			//
			List<DesignerColumnRender> innerJoinColumnRenders = new ArrayList<>();
			List<DesignerColumnInfo> targetDesignerColumns=dao.getAll(DesignerColumnInfo.class,
					QueryWhere.create().
					where("designer_database_id",req.designerDatabaseId).
					where("target_table_name", req.tableName).
					orderBy("`order` asc"));
			for (DesignerColumn designerColumn : targetDesignerColumns) {
				DesignerColumnRender columnRender=createDesignerColumnRender(designerColumn, req.designerDatabaseId, tableRender);
				innerJoinColumnRenders.add(columnRender);
			}

			//
			Velocity.addProperty("runtime.introspector.uberspect",
					"org.apache.velocity.util.introspection.UberspectPublicFields, "
							+ "org.apache.velocity.util.introspection.UberspectImpl");
			Velocity.init();
			VelocityContext context = new VelocityContext();
			context.put("runtime", new FieldMethodizer("org.apache.velocity.runtime.Runtime"));
			String fileSplit = "$$#$$";
			context.put("fileSplit", fileSplit);
			context.put("columnRenders", columnRenders);
			context.put("innerJoinColumnRenders", innerJoinColumnRenders);
			context.put("tableRender", tableRender);
			logger.info("columnRenders:{}",DumpUtil.dump(columnRenders));
			logger.info("tableRender:{}",DumpUtil.dump(tableRender));
			StringWriter w = new StringWriter();
			DesignerTemplate template = dao.getById(DesignerTemplate.class, req.designerTemplateId);
			Velocity.evaluate(context, w, "tag", template.content);

			StringReader sr = new StringReader(w.toString());
			BufferedReader br = new BufferedReader(sr);
			String line = null;
			GenerateCodeResp resp = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if (line.startsWith(fileSplit)) {
					if (resp != null) {
						resp.content = sb.toString().trim();
					}
					resp = new GenerateCodeResp();
					resp.fileName = line.substring(fileSplit.length()).trim();
					resp.language = template.language;
					result.add(resp);
					sb = new StringBuilder();
				} else {
					sb.append(line + "\n");
				}
			}
			if (resp == null) {
				throw new IllegalArgumentException("模板错误，缺少文件名");
			}
			if (resp.content == null) {
				resp.content = sb.toString().trim();
			}
			return result;
		}

		private DesignerColumnRender createDesignerColumnRender(DesignerColumn designerColumn,int designerDatabaseId,DesignerTableRender tableRender) {
			DesignerColumnRender columnRender = new DesignerColumnRender();
			columnRender.column = designerColumn;
			columnRender.javaType = GenCodeUtil.getJavaType(designerColumn);
			columnRender.csharpType = GenCodeUtil.getCSharpType(designerColumn);
			columnRender.javaObjectType=GenCodeUtil.getJavaObjectType(designerColumn);
			if (!StringUtil.isEmpty(designerColumn.foreignTableName)) {
				columnRender.foreignDomainName = GenCodeUtil.getClassName(designerColumn.foreignTableName);
				columnRender.foreignColumnDomainName = GenCodeUtil.getDomainField(designerColumn.foreignColumnName);
				DesignerTable tableInfo = dao.getDomain(DesignerTable.class, QueryWhere.create().
						where("designer_database_id", designerDatabaseId).
						where("name", designerColumn.foreignTableName));
				if (tableInfo == null) {
					throw new AppException("外键关联表不存在");
				}
				columnRender.foreignTableComment=tableInfo.displayName;
			}
			if (!StringUtil.isEmpty(designerColumn.relationTableName)) {
				columnRender.relationDomainName = GenCodeUtil.getClassName(designerColumn.relationTableName);
				columnRender.relationColumnDomainName = GenCodeUtil.getDomainField(designerColumn.relationColumnName);
				DesignerTable tableInfo = dao.getDomain(DesignerTable.class, QueryWhere.create().
						where("designer_database_id", designerDatabaseId).
						where("name", designerColumn.relationTableName));
				if (tableInfo == null) {
					throw new AppException("关联表不存在");
				}
				columnRender.relationTableComment=tableInfo.displayName;
				//
				if(!StringUtil.isEmpty(designerColumn.targetTableName)) {
					tableInfo = dao.getDomain(DesignerTable.class, QueryWhere.create().
							where("designer_database_id", designerDatabaseId).
							where("name", designerColumn.targetTableName));
					if (tableInfo == null) {
						throw new AppException("目标表不存在");
					}
					columnRender.relationDomainName = GenCodeUtil.getClassName(designerColumn.relationTableName);//CoinMarket
					columnRender.relationColumnDomainName = GenCodeUtil.getDomainField(designerColumn.relationColumnName);//marketId
					columnRender.relationColumnJavaName=GenCodeUtil.toUpperCaseFirstOne(columnRender.relationColumnDomainName);//MarketId
					columnRender.targetDomainName = GenCodeUtil.getClassName(designerColumn.targetTableName);
					columnRender.targetColumnDomainName = GenCodeUtil.getDomainField(designerColumn.targetColumnName);
					columnRender.relationRightDomainName = GenCodeUtil.getDomainField(designerColumn.relationRightColumnName);
					columnRender.targetTableComment=tableInfo.displayName;
				}
			}
			 if(designerColumn.fieldType==DesignerColumn.FIELD_TYPE_DB) {
				 //数据字典
				 if(!StringUtil.isEmpty(designerColumn.dataDict)) {
					 if(designerColumn.dataDict.indexOf(",")!=-1) {
						 columnRender.dataDictId=tableRender.domainName+"."+designerColumn.domainName;
						 String[] dataDictValueStr=designerColumn.dataDict.split(",");
						 int value=1;
						 List<DataDictValue> dataDictValues=new ArrayList<>();
						 for (String name : dataDictValueStr) {
							 DataDictValue dataDictValue=new DataDictValue();
							 dataDictValue.name=name;
							 dataDictValue.value=value++;
							 dataDictValues.add(dataDictValue);
						}
						 columnRender.dataDictValues=dataDictValues;
					 }else {
						 columnRender.dataDictId=designerColumn.dataDict;
					 }
				 }
			 }
			columnRender.displayWidth=getDisplayWidth(columnRender);
			return columnRender;
		}

		private int getDisplayWidth(DesignerColumnRender columnRender) {
			if(!StringUtil.isEmpty(columnRender.dataDictId)) {
				return 120;
			}
			if("id".equals(columnRender.column.columnName)) {
				return 60;
			}
			if("int".equals(columnRender.javaType)||
					"long".equals(columnRender.javaType)||
					"float".equals(columnRender.javaType)||
					"double".equals(columnRender.javaType)) {
				return 80;
			}
			if("Date".equals(columnRender.javaType)) {
				return 120;
			}
			if("String".equals(columnRender.javaType)) {
				int width=(int) (columnRender.column.maxValue*1.5);
				if(width>200) {
					width=200;
				}
				if(width<100) {
					width=100;
				}
				return width;
			}
			return 0;
		}
		//
		/**通过ID查询MYSQL代理*/
		@Override
		public DesignerMysqlProxyInstanceInfo getDesignerMysqlProxyInstanceById(String token, int id){
			checkPermission(token,Permission.ID_编辑MYSQL代理);
			return dao.getById(DesignerMysqlProxyInstanceInfo.class, id);
		}

		/**查询MYSQL代理列表和总数*/
		@Override
		public Map<String, Object> getDesignerMysqlProxyInstanceList(String token,DesignerMysqlProxyInstanceQuery query){
			checkPermission(token,Permission.ID_编辑MYSQL代理);
			return createResult(dao.getList(query), dao.getListCount(query));
		}

		/**新增MYSQL代理*/
		@Transaction
		@Override
		public int addDesignerMysqlProxyInstance(String token, DesignerMysqlProxyInstanceInfo bean) {
			Account account=checkPermission(token,Permission.ID_编辑MYSQL代理);
			bean.createAccountId=account.id;
			bean.companyId=account.companyId;
			BizUtil.checkValid(bean);
			BizUtil.checkUniqueKeysOnAdd(dao, bean,"代理端口已存在");
			bean.dbPassword=TripleDESUtil.encrypt(bean.dbPassword, ConstDefine.GLOBAL_KEY);
			dao.add(bean);
			mysqlProxyService.addRule(bean);
			return bean.id;
		}

		/**编辑MYSQL代理*/
		@Transaction
		@Override
		public void updateDesignerMysqlProxyInstance(String token, DesignerMysqlProxyInstanceInfo bean) {
			Account account=checkPermission(token,Permission.ID_编辑MYSQL代理);
			DesignerMysqlProxyInstance old=dao.getExistedByIdForUpdate(DesignerMysqlProxyInstance.class, bean.id);
			BizUtil.checkUniqueKeysOnUpdate(dao,bean,old,"代理端口已存在");
			int oldProxyPort=old.proxyPort;
			old.name=bean.name;
			old.proxyPort=bean.proxyPort;
			old.host=bean.host;
			old.port=bean.port;
			old.dbUser=bean.dbUser;
			if(bean.dbPassword!=null&&!bean.dbPassword.equals(old.dbPassword)) {
				old.dbPassword=TripleDESUtil.encrypt(bean.dbPassword, ConstDefine.GLOBAL_KEY);
			}
			old.roles=bean.roles;
			old.members=bean.members;
			old.remark=bean.remark;
			old.updateAccountId=account.id;
			BizUtil.checkValid(old);
			dao.update(old);
			//
			mysqlProxyService.removeRule(oldProxyPort);
			mysqlProxyService.addRule(old);
		}

		/**删除MYSQL代理*/
		@Transaction
		@Override
		public void deleteDesignerMysqlProxyInstance(String token, int id, String reason) {
			checkPermission(token,Permission.ID_编辑MYSQL代理);
			DesignerMysqlProxyInstance old=dao.getExistedById(DesignerMysqlProxyInstance.class, id);
		    dao.deleteById(DesignerMysqlProxyInstanceInfo.class, old.id);
		    //
		    mysqlProxyService.removeRule(old.proxyPort);
		}
		//
		@Override
		public DesignerDatabaseChangeLogInfo getDesignerTableChangeLogById(String token, int id) {
			checkPermission(token,Permission.ID_数据库配置);
			return dao.getById(DesignerDatabaseChangeLogInfo.class, id);
		}

		@Override
		public Map<String, Object> getDesignerTableChangeLogList(String token, DesignerTableChangeLogQuery query) {
			Account account=checkPermission(token,Permission.ID_数据库配置);
			bizService.setupQuery(account, query);
			return createResult(dao.getList(query,"createSql","ddl","dml"), dao.getListCount(query));
		}
	}
}
