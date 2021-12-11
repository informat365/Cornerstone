package cornerstone.biz.dao;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.domain.AccountToken;
import cornerstone.biz.domain.DomainUniqueKey;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.ConnectionUtil;
import jazmin.core.app.AppException;
import jazmin.driver.jdbc.ConnectionException;
import jazmin.driver.jdbc.ResultSetHandler;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.driver.jdbc.smartjdbc.SmartDAO;
import jazmin.driver.jdbc.smartjdbc.SqlBean;
import jazmin.driver.jdbc.smartjdbc.provider.SelectProvider;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.StringUtil;

/**
 * 公共DAO
 * @author cs
 *
 */
public class ITFDAO extends SmartDAO {

	//
	private static final Logger logger = LoggerFactory.get(ITFDAO.class);
	//
	/**
	 * 
	 * @param bean
	 * @return
	 */
	public int add(Object bean) {
		int id=add(bean, true, "id");
		setFieldValue(bean, "id", id);
		return id;
	}
	
	/**
	 * 
	 * @param bean
	 */
	public void addNotWithGenerateKey(Object bean) {
		add(bean, false);
	}
	
	/**
	 * 
	 * @param bean
	 * @param withGenerateKey
	 * @param excludeFields
	 * @return
	 */
	public int add(Object bean,boolean withGenerateKey,String... excludeFields) {
		setFieldValue(bean, "createTime", new Date());
		setFieldValue(bean, "updateTime", new Date());
		return insert(bean, withGenerateKey, excludeFields);
	}

	/**
	 * 
	 * @param bean
	 */
	public void update(Object bean) {
		setFieldValue(bean, "updateTime", new Date());
		update(bean, "createTime", "id");
	}
	
	/**
	 * 
	 * @param bean
	 */
	public void updateExcludeFields(Object bean,String... excludeFields) {
		setFieldValue(bean, "updateTime", new Date());
		update(bean, excludeFields);
	}
	
	/**
	 * 
	 * @param bean
	 * @param includeFields 指定字段更新 isFileDelete
	 */
	public void updateSpecialFields(Object bean,String ... includeFields) {
		setFieldValue(bean, "updateTime", new Date());
		Set<String> includeFieldSet=null;
		if(!BizUtil.isNullOrEmpty(includeFields)) {
			includeFieldSet = new LinkedHashSet<>(Arrays.asList(includeFields));
			includeFieldSet.add("updateTime");
		}
		update(bean, includeFieldSet);
	}

	/**
	 * 
	 * @param id
	 */
	public void deleteById(Class<?> domainClass, int id) {
		delete(domainClass, QueryWhere.create().where("id", id));
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T getById(Class<T> domainClass, int id) {
		return getDomain(domainClass, QueryWhere.create().where("id", id));
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T getByUuid(Class<T> domainClass, String uuid) {
		return getDomain(domainClass, QueryWhere.create().where("uuid", uuid));
	}
	
	/**
	 * 
	 * @param <T>
	 * @param domainClass
	 * @param uuid
	 * @return
	 */
	public <T> T getByUuidForUpdate(Class<T> domainClass, String uuid) {
		return getDomain(domainClass, QueryWhere.create().where("uuid", uuid).forUpdate());
	}
	
	/**
	 * 
	 * @param <T>
	 * @param domainClass
	 * @param id
	 * @param includeFields
	 * @return
	 */
	public <T> T getById(Class<T> domainClass, int id, Set<String> includeFields) {
		return getDomain(domainClass, QueryWhere.create().where("id", id),includeFields);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T getExistedById(Class<T> domainClass, int id) {
		T bean = getById(domainClass, id);
		if (bean == null) {
			throw new IllegalArgumentException(getDomainClassComment(domainClass)+"不存在");
		}
		return bean;
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param uuid
	 * @return
	 */
	public <T> T getExistedByUuid(Class<T> domainClass, String uuid) {
		T bean = getDomain(domainClass, QueryWhere.create().where("uuid", uuid));
		if (bean == null) {
			throw new IllegalArgumentException(getDomainClassComment(domainClass)+"不存在");
		}
		return bean;
	}
	
	/**
	 * 
	 * @param <T>
	 * @param domainClass
	 * @param uuid
	 * @param includeFields
	 * @return
	 */
	public <T> T getExistedByUuid(Class<T> domainClass, String uuid, Set<String> includeFields) {
		T bean = getDomain(domainClass, QueryWhere.create().where("uuid", uuid),includeFields);
		if (bean == null) {
			throw new IllegalArgumentException(getDomainClassComment(domainClass)+"不存在");
		}
		return bean;
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param uuid
	 * @return
	 */
	public <T> T getExistedByUuidForUpdate(Class<T> domainClass, String uuid) {
		T bean = getDomain(domainClass, QueryWhere.create().where("uuid", uuid).forUpdate());
		if (bean == null) {
			throw new IllegalArgumentException(getDomainClassComment(domainClass)+"不存在");
		}
		return bean;
	}
	
	private String getDomainClassComment(Class<?> domainClass) {
		DomainDefineValid valid=domainClass.getAnnotation(DomainDefineValid.class);
		if(valid==null) {
			return "数据";
		}
		String comment=valid.comment();
		if(StringUtil.isEmpty(comment)) {
			return "数据";
		}
		return comment;
	}

	/**
	 * 
	 * @param domainClass
	 * @param key
	 * @return
	 */
	public <T> T getByUniqueKey(Class<T> domainClass,DomainUniqueKey key){
		if(key==null||key.fields==null||key.fields.length==0) {
			throw new IllegalArgumentException("唯一索引数据错误");
		}
		QueryWhere qw=QueryWhere.create();
		for(int i=0;i<key.fields.length;i++) {
			qw.where(convertFieldName(key.fields[i]),key.values[i]);
		}
		return getDomain(domainClass,qw);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T getByIdForUpdate(Class<T> domainClass, int id) {
		return getDomain(domainClass,QueryWhere.create().where("id", id).forUpdate());
	}

	/**
	 * 
	 * @param domainClass
	 * @param id
	 * @return
	 */
	public <T> T getExistedByIdForUpdate(Class<T> domainClass, int id) {
		T bean = getDomain(domainClass,QueryWhere.create().where("id", id).forUpdate());
		if (bean == null) {
			throw new IllegalArgumentException(getDomainClassComment(domainClass)+"不存在");
		}
		return bean;
	}
	
	public <T> T getExistedByIdForUpdate(Class<T> domainClass, String id) {
		T bean = getDomain(domainClass,QueryWhere.create().where("id", id).forUpdate());
		if (bean == null) {
			throw new IllegalArgumentException(getDomainClassComment(domainClass)+"不存在");
		}
		return bean;
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param sqlBean
	 * @return
	 */
	public <T> List<T> getList(Class<T> domainClass,SqlBean sqlBean){
		return queryList(domainClass,sqlBean.toSql(),sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param sqlBean
	 * @return
	 */
	public int getListCount(SqlBean sqlBean){
		return queryCount(sqlBean.toSql(),sqlBean.parameters);
	}
	//
	/**
	 * 
	 * @param bean
	 * @param fieldName
	 * @param value
	 */
	protected void setFieldValue(Object bean, String fieldName, Object value) {
		try {
			Field field = bean.getClass().getField(fieldName);
			if (field != null) {
				field.set(bean, value);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param bean
	 * @param fieldName
	 * @return
	 */
	protected Object getFieldValue(Object bean, String fieldName) {
		try {
			Field field = bean.getClass().getField(fieldName);
			if (field != null) {
				return field.get(bean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public AccountToken getAccountTokenByToken(String token) {
		return getByField(AccountToken.class,"token",token);
	}
	
	/**
	 * 
	 * @param clazz
	 * @param javaField	eg:userName
	 * @param value
	 * @return
	 */
	public <T> T getByField(Class<T> clazz,String javaField,Object value) {
		return getDomain(clazz,QueryWhere.create().where(convertFieldName(javaField),value));
	}
	
	/**
	 * 
	 * @param clazz
	 * @param javaField eg:userName
	 * @param value
	 * @return
	 */
	public <T> T getByFieldForUpdate(Class<T> clazz,String javaField,Object value) {
		return getDomain(clazz,QueryWhere.create().where(convertFieldName(javaField),value).forUpdate());
	}
	
	/**
	 * 
	 * @param clazz
	 * @param javaFields eg:["userName","email"]
	 * @param values
	 * @return
	 */
	public <T> T getByFields(Class<T> clazz,List<String> javaFields,List<Object> values) {
		QueryWhere qw=QueryWhere.create();
		for(int i=0;i<javaFields.size();i++) {
			qw.where(convertFieldName(javaFields.get(i)),values.get(i));
		}
		return getDomain(clazz,qw);
	}
	
	/**
	 * 
	 * @param clazz
	 * @param javaFields eg:["userName","email"]
	 * @param values
	 * @return
	 */
	public <T> T getByFieldsForUpdate(Class<T> clazz,List<String> javaFields,List<Object> values) {
		QueryWhere qw=QueryWhere.create();
		for(int i=0;i<javaFields.size();i++) {
			qw.where(convertFieldName(javaFields.get(i)),values.get(i));
		}
		return getDomain(clazz,qw.forUpdate());
	}

	/**
	 * 
	 * @param domainClass
	 * @param query
	 * @param excludeFields
	 * @return
	 */
	@Override
	public <T> List<T> getAll(Class<T> domainClass, QueryWhere query, String ... excludeFields){
		SqlBean sqlBean=new SelectProvider(domainClass).query(query).excludeFields(excludeFields).build();
		return queryList(domainClass,sqlBean.sql,sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param domainClass
	 * @param query
	 * @return
	 */
	public <T> List<Integer> getIdList(Class<T> domainClass,Query query){
		SqlBean sqlBean=new SelectProvider(domainClass).query(query).build();
		sqlBean.selectSql="select a.id ";
		return queryForIntegers(sqlBean.toSql(), sqlBean.parameters);
	}
	
	//
	protected final <T> List<T> call(
			String sql,
			ResultSetHandler<T> rowHandler, 
			Object... parameters) {
		Connection conn = connectionDriver.getConnection();
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			cs = conn.prepareCall(sql);
			try {
				ConnectionUtil.set(cs, parameters);
			} catch (SQLException e) {
				logger.error("ConnectionUtil.set failed.\nsql:{} \nparameters:{}",
						sql,DumpUtil.dump(parameters));
				throw new AppException("ERR");
			}
			cs.execute();
			rs=cs.getResultSet();
			List<T> result = new ArrayList<T>();
			if(rs!=null) {
				while (rs.next()) {
					result.add(rowHandler.handleRow(rs));
				}
			}
			return result;
		} catch (Exception e) {
			throw new ConnectionException(e);
		} finally {
			ConnectionUtil.closeResultSet(rs);
			ConnectionUtil.closeStatement(cs);
			ConnectionUtil.closeConnection(conn);
		}
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public <T> List<T> call(
			Class<T> domainClass,
			String sql,
			Object... parameters) {
		SqlBean sqlBean=parseSql(sql, parameters);
		return call(sqlBean.sql, new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws IllegalAccessException, InstantiationException {
				T o=domainClass.newInstance();
				try {
					convertBean(o,row);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				return o;
			}}, sqlBean.parameters);
	}
}
