package cornerstone.biz.action;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.annotations.DomainRelation;
import cornerstone.biz.dao.ITFDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.DomainUniqueKey;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.taskjob.BizTaskJobs;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.smartjdbc.Config;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.driver.jdbc.smartjdbc.SmartJdbcException;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.JSONUtil;

/**
 *
 * @author cs
 *
 */

public interface CommAction {
	//
	@RpcService
	public static class CommActionImpl implements CommAction {
		//
		private static Logger logger = LoggerFactory.get(CommActionImpl.class);
		//
		@AutoWired
		public ITFDAO dao;

		@AutoWired
		BizService bizService;
		//
		private Map<String,Class<?>> domainClassMap;
		//
		public CommActionImpl() {
			domainClassMap=new ConcurrentHashMap<>();
		}
		//
		protected void checkValid(Object bean) {
			BizUtil.checkValid(bean);
		}
		//
		protected void checkDataDictValueValid(String categoryCode,int value,String errorMsg) {
			BizTaskJobs.checkDataDictValueValid(categoryCode, value, errorMsg);
		}
		/**
		 *
		 * @param account
		 * @param query
		 */
		protected void setupQuery(Account account,Query query) {
			BizUtil.setFieldValue(query, "isDelete", false);
			BizUtil.setFieldValue(query, "companyId", account.companyId);
		}
		/**
		 * 返回
		 * @param list
		 * @param count
		 * @return
		 */
		protected Map<String, Object> createResult(List<?> list, int count) {
			Map<String, Object> result = new HashMap<>();
			result.put("list", list);
			result.put("count", count);
			return result;
		}
		//
		protected Map<String, Object> createEmptyResult() {
			Map<String, Object> result = new HashMap<>();
			result.put("list", new ArrayList<>());
			result.put("count", 0);
			return result;
		}
		//
		// 增加前
		protected void beforeAdd(String token, Object bean,String permissionId) {
			if(bean==null) {
				throw new AppException("数据不能为空");
			}
			checkPermission(token,permissionId);
		}

		// 增加后
		protected void afterAdd(String token, Object bean) {

		}

		// 更新前
		protected void beforeUpdate(String token, Object old, Object bean,String permissionId) {
			if(bean==null) {
				throw new AppException("数据不能为空");
			}
			checkPermission(token,permissionId);
		}

		// 更新前
		protected void afterUpdate(String token, Object old, Object bean) {

		}

		// 删除前
		protected void beforeDelete(String token, Object old, int id, String reason,String permissionId) {
			if(old==null) {
				throw new AppException("数据不存在");
			}
			checkPermission(token,permissionId);
		}

		// 删除后
		protected void afterDelete(String token, Object old, int id, String reason) {

		}

		protected Account checkPermission(String token,String permissionId) {
			Account  account=bizService.getExistedAccountByToken(token);
			if(!StringUtil.isEmpty(permissionId)) {
				bizService.checkCompanyPermission(account, permissionId);
			}
			return account;
		}

		public void checkAccountPermission(Account account,int accountId) {
			if(account.id!=accountId) {
				throw new AppException("权限不足");
			}
		}

		// 所有查询前
		protected Account beforeQuery(String token,Class<?> domainClass,Query query,String permissionId) {
			return checkPermission(token, permissionId);
		}
		// 所有查询前
		protected Account beforeQuery(String token,Class<?> domainClass,String permissionId) {
			return beforeQuery(token, domainClass, null,permissionId);
		}

		// 列表查询前
		protected Account beforeGetList(String token,Class<?> domainClass, Query query,String permissionId) {
			return beforeQuery(token, domainClass,permissionId);
		}

		// 列表查询后
		protected <T> void afterGetList(String token,Class<?> domainClass, Query query, List<T> list) {
		}

		// 列表总数查询前
		protected Account beforeGetListCount(String token,Class<?> domainClass, Query query,String permissionId) {
			return beforeQuery(token, domainClass,query,permissionId);
		}

		// 列表总数查询后
		protected void afterGetListCount(String token,Class<?> domainClass, Query query, int count) {

		}

		// 列表和总数查询前
		protected Account beforeGetListAndCount(String token,Class<?> domainClass, Query query,String permissionId) {
			return beforeQuery(token, domainClass,query,permissionId);
		}

		// 列表和总数查询后
		protected void afterGetListAndCount(String token,Class<?> domainClass, Query query, Map<String, Object> result) {

		}

		// 查询所有前
		protected Account beforeGetAll(String token,Class<?> domainClass,String permissionId) {
			return beforeQuery(token, domainClass,permissionId);
		}

		// 查询所有后
		protected void afterGetAll(String token,Class<?> domainClass) {

		}

		//
		protected Account beforGetById(String token,Class<?> domainClass, int id,String permissionId) {
			return beforeQuery(token, domainClass,permissionId);
		}

		//
		protected void afterGetById(String token, Class<?> domainClass, int id, Object bean) {

		}
		// 启用前
		protected void beforeEnable(String token, Class<?> domainClass, Object old, int id) {

		}

		// 启用后
		protected void afterEnable(String token, Class<?> domainClass, Object old, int id) {

		}

		// 禁用前
		protected void beforeDisable(String token, Class<?> domainClass, Object old, int id) {

		}

		// 禁用后
		protected void afterDisable(String token, Class<?> domainClass, Object old, int id) {

		}
		//
		protected Class<?> getDomainClass(String domainClassName) {
			if(StringUtil.isEmpty(domainClassName)) {
				throw new AppException("IllegalArgument Error domainClassName cannot be null");
			}
			Class<?> clazz=domainClassMap.get(domainClassName);
			if(clazz!=null) {
				return clazz;
			}
			try {
				clazz=Class.forName(domainClassName);
				domainClassMap.put(domainClassName, clazz);
				return clazz;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new AppException(e.getMessage());
			}
		}

		protected int add(String token, Object bean,String domainClassName,String permissionId) {
			Class<?> domainClass = getDomainClass(domainClassName);
			bean = JSONUtil.fromJson(JSONUtil.toJson(bean), domainClass);
			return add(token, bean,permissionId);
		}

		protected int add(String token, Object bean,String permissionId) {
			beforeAdd(token, bean,permissionId);
			int id = add(bean);
			addOrUpdateDomainRelations(false,bean);
			afterAdd(token, bean);
			return id;
		}

		private void addOrUpdateDomainRelations(boolean isUpdate,Object bean) {
			Field[] fields=bean.getClass().getFields();
			for (Field field : fields) {
				DomainRelation domainRelation=field.getAnnotation(DomainRelation.class);
				if(domainRelation==null) {
					continue;
				}
				if(!domainRelation.targetDomainClass().equals(void.class)) {
					continue;
				}
				int beanId=(int) getFieldValue(bean, "id");
				if(isUpdate) {
					dao.delete(domainRelation.domainClass(), QueryWhere.create().where(
							Config.convertFieldName(domainRelation.fieldName()),beanId));
				}
				try {
					List<?> relDomains=(List<?>) field.get(bean);
					if(relDomains==null) {
						return;
					}
					for (Object e : relDomains) {
						setFieldValue(e, domainRelation.fieldName(), beanId);
						add(e);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					throw new AppException(e.getMessage());
				}
			}
		}

		protected int add(Object bean) {
			BizUtil.checkValid(bean);// 检查数据合法性
			checkUniqueKeysOnAdd(dao, bean);// 检查唯一索引
			return dao.add(bean);
		}

		// 检查唯一索引(新增时)
		protected void checkUniqueKeysOnAdd(ITFDAO dao,Object bean) {
			DomainDefineValid domainValid = bean.getClass().getAnnotation(DomainDefineValid.class);
			if (domainValid == null) {
				return;
			}
			if (domainValid.uniqueKeys() != null) {
				for (UniqueKey uk : domainValid.uniqueKeys()) {
					DomainUniqueKey key = new DomainUniqueKey();
					key.fields = uk.fields();
					key.values = new Object[key.fields.length];
					for (int i = 0; i < key.fields.length; i++) {
						key.values[i] = getFieldValue(bean, key.fields[i]);
					}
					Object old = dao.getByUniqueKey(bean.getClass(), key);
					if (old != null) {
						throw new IllegalArgumentException("数据已存在，不能重复添加");
					}
				}
			}
		}

		// 检查唯一索引(编辑时)
		protected void checkUniqueKeysOnUpdate(ITFDAO dao, Object bean,Object old) {
			DomainDefineValid domainDefine = bean.getClass().getAnnotation(DomainDefineValid.class);
			if (domainDefine == null) {
				return;
			}
			if (domainDefine.uniqueKeys() != null) {
				for (UniqueKey uk : domainDefine.uniqueKeys()) {
					DomainUniqueKey key = new DomainUniqueKey();
					key.fields = uk.fields();
					key.values = new Object[key.fields.length];
					boolean isChange=false;//是否有修改唯一索引相关字段
					for (int i = 0; i < key.fields.length; i++) {
						key.values[i] = getFieldValue(bean, key.fields[i]);
						Object oldValue= getFieldValue(old, key.fields[i]);
						if(!oldValue.equals(key.values[i])) {
							isChange=true;
						}
					}
					if(isChange) {
						Object oldObj = dao.getByUniqueKey(bean.getClass(), key);
						if (oldObj != null) {
							throw new IllegalArgumentException("数据已存在，不能重复添加");
						}
					}
				}
			}
		}

		protected void update(String token, Object bean,String domainClassName,String permissionId) {
			Class<?> domainClass = getDomainClass(domainClassName);
			bean = JSONUtil.fromJson(JSONUtil.toJson(bean), domainClass);
			update(token, bean,permissionId);
		}

		protected void update(String token, Object bean,String permissionId) {
			int id = (int) getFieldValue(bean, "id");
			Object old = dao.getExistedByIdForUpdate(bean.getClass(), id);
			beforeUpdate(token, old, bean,permissionId);
			update(old, bean);
			addOrUpdateDomainRelations(true, bean);
			afterUpdate(token, old, bean);
		}

		private void update(Object old,Object bean) {
			setupOldBean(old, bean);
			BizUtil.checkValid(bean);
			checkUniqueKeysOnUpdate(dao, bean,old);
			dao.update(old);
		}

		private void setupOldBean(Object old, Object bean) {
			Field[] fields = bean.getClass().getFields();
			for (Field field : fields) {
				try {
					if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
						continue;
					}
					DomainFieldValid define = field.getAnnotation(DomainFieldValid.class);
					if (define == null) {
						continue;
					}
					if (define.canUpdate()) {
						setFieldValue(old, field.getName(), getFieldValue(bean, field.getName()));
					}
				} catch (Exception e) {
					logger.catching(e);
					throw new IllegalArgumentException(e.getMessage());
				}
			}
		}

		public <T> T getById(String token,int id,String domainClassName,String permissionId) {
			Class<?> domainClass = getDomainClass(domainClassName);
			return getById(token, id, domainClass,permissionId);
		}

		protected <T> T getById(String token,int id,Class<?> domainClass,String permissionId) {
			return getById(token, id,domainClass,false,permissionId);
		}

		@SuppressWarnings({ "unchecked" })
		protected <T> T getById(String token,int id,Class<?> domainClass,boolean ingoreDomainRelations,String permissionId) {
			beforGetById(token, domainClass, id,permissionId);
			T bean = (T) dao.getById(domainClass, id);
			Field[] fields=domainClass.getFields();
			if(!ingoreDomainRelations) {
				for (Field field : fields) {
					setRelationDomainList(bean, field, id);
				}
			}
			afterGetById(token, domainClass, id, bean);
			return bean;
		}

		private void setRelationDomainList(Object bean,Field field,int id) {
			DomainRelation domainRelation=field.getAnnotation(DomainRelation.class);
			if(domainRelation==null) {
				return;
			}
			try {
				List<?> list=null;
				if(domainRelation.targetDomainClass().equals(void.class)) {//一对多
					list=dao.getAll(domainRelation.domainClass(),QueryWhere.create().
							where(Config.convertFieldName(domainRelation.fieldName()),id));
				}else {//多对多
					Class<?> queryClass=domainRelation.targetQueryDomainClass();
					if(!queryClass.equals(void.class)) {
						String fieldName="rel"+domainRelation.domainClass().getSimpleName()+BizUtil.toUpperCaseFirstOne(domainRelation.fieldName());
						Field filed=queryClass.getField(fieldName);
						if(filed==null) {
							logger.warn(fieldName+" not found in "+queryClass.getName());
							return;
						}
						Query query=(Query) queryClass.newInstance();
						filed.set(query, id);
						list=dao.getAll(query);
					}else {
						String sql="select * from "+Config.getTableName(domainRelation.targetDomainClass())+" a "
								+ "inner join "+Config.getTableName(domainRelation.domainClass())+" i1 on i1."+Config.convertFieldName(domainRelation.relationRightFieldName())+"=a."+domainRelation.targetFieldName()+" "
								+ "where i1."+Config.convertFieldName(domainRelation.fieldName())+"=?";
						list=dao.queryList(domainRelation.targetDomainClass(), sql, id);
					}
				}
				field.set(bean, list);
			} catch (Exception e) {
				logger.error(e);
			}
		}

		@SuppressWarnings("unchecked")
		protected <T> Class<T> getQueryDomainClass(Query query) {
			QueryDefine queryDefine=query.getClass().getAnnotation(QueryDefine.class);
			if(queryDefine==null) {
				throw new SmartJdbcException("no domainClass found in QueryClass["+query.getClass().getName()+"]");
			}
			return (Class<T>) queryDefine.domainClass();
		}

		public <T> List<T> getList(String token,Object q,String queryClassName,String permissionId) {
			Class<?> queryClass = getDomainClass(queryClassName);
			Query query = JSONUtil.fromJson(JSONUtil.toJson(q), queryClass);
			return getList(token, query,permissionId);
		}

		protected <T> List<T> getList(String token,Query query,String permissionId) {
			Class<T> domainClass=getQueryDomainClass(query);
			beforeGetList(token,domainClass, query,permissionId);
			List<T> list = dao.getList(query);
			afterGetList(token,domainClass, query, list);
			return list;
		}

		public int getListCount(String token, Object q,String queryClassName,String permissionId) {
			Class<?> queryClass = getDomainClass(queryClassName);
			Query query = JSONUtil.fromJson(JSONUtil.toJson(q), queryClass);
			return getListCount(token, query,permissionId);
		}

		protected int getListCount(String token, Query query,String permissionId) {
			Class<?> domainClass=getQueryDomainClass(query);
			beforeGetListCount(token,domainClass, query,permissionId);
			int count = dao.getListCount(query);
			afterGetListCount(token,domainClass, query, count);
			return count;
		}

		public <T> List<T> getAll(String token, String domainClassName,String permissionId) {
			@SuppressWarnings("unchecked")
			Class<T> domainClass = (Class<T>) getDomainClass(domainClassName);
			return getAll(token, domainClass,permissionId);
		}

		protected <T> List<T> getAll(String token, Class<T> domainClass,String permissionId) {
			beforeGetAll(token, domainClass,permissionId);
			List<T> list = dao.getAll(domainClass);
			afterGetAll(token, domainClass);
			return list;
		}

		public Map<String, Object> getListAndCount(String token, Object q,String queryClassName,String permissionId) {
			Class<?> queryClass = getDomainClass(queryClassName);
			Query query = JSONUtil.fromJson(JSONUtil.toJson(q), queryClass);
			return getListAndCount(token, query,permissionId);
		}

		protected Map<String, Object> getListAndCount(String token, Query query,String permissionId) {
			Class<?> domainClass=getQueryDomainClass(query);
			beforeGetListAndCount(token,domainClass,query,permissionId);
			Map<String, Object> result = createResult(dao.getList(query), dao.getListCount(query));
			afterGetListAndCount(token,domainClass,query, result);
			return result;
		}

		protected int deleteById(String token,int id, String reason, String domainClassName,String permissionId) {
			Class<?> domainClass = getDomainClass(domainClassName);
			deleteById(token, id, reason, domainClass,permissionId);
			return id;
		}

		protected int deleteById(String token,int id, String reason, Class<?> domainClass,String permissionId) {
			Object old = dao.getByIdForUpdate(domainClass, id);
			if (old == null) {
				throw new IllegalArgumentException("数据不存在");
			}
			beforeDelete(token, old, id, reason,permissionId);
			dao.deleteById(domainClass, id);
			afterDelete(token, old, id, reason);
			return id;
		}
		//

		protected  void setFieldValue(Object bean, String fieldName, Object value) {
			setFieldValue(bean, fieldName, value, true);
		}

		protected  void setFieldValue(Object bean, String fieldName, Object value,boolean ingoreException) {
			try {
				Field field = bean.getClass().getField(fieldName);
				if (field != null) {
					field.set(bean, value);
				}
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
				if(!ingoreException) {
					throw new AppException(e.getMessage());
				}
			}
		}

		protected  Object getFieldValue(Object bean, String fieldName) {
			return getFieldValue(bean, fieldName, true);
		}

		protected  Object getFieldValue(Object bean, String fieldName,boolean ingoreException) {
			try {
				Field field = bean.getClass().getField(fieldName);
				if (field != null) {
					return field.get(bean);
				}
			} catch (Exception e) {
				if(!ingoreException) {
					logger.error(e.getMessage(), e);
					throw new AppException(e.getMessage());
				}
			}
			return null;
		}

		/**
		 *
		 * @param bean
		 * @param name
		 */
		protected void checkNull(Object bean,String name) {
			if(bean==null) {
				logger.error("checkNull bean is null name:{}",name);
				throw new AppException(name+"不存在");
			}
		}

		/**
		 *
		 * @param domainClass
		 * @return
		 */
		protected String getDomainComment(Class<?> domainClass) {
			DomainDefineValid domainDefine = domainClass.getAnnotation(DomainDefineValid.class);
			if (domainDefine == null || StringUtil.isEmpty(domainDefine.comment())) {
				throw new AppException("no comment found in " + domainClass.getName());
			}
			return domainDefine.comment();
		}
	}
}
