package cornerstone.biz.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.util.SqlUtils;
import jazmin.core.app.AppException;
import jazmin.driver.jdbc.smartjdbc.Config;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;

/**
 * 
 * @author cs
 *
 */
public class QueryConditionConvert {
	//
	private static Logger logger=LoggerFactory.get(QueryConditionConvert.class);
	//
	private QueryCondition qc;
	private Map<Integer,ProjectFieldDefine> fieldDefineMap;
	//
	public QueryConditionConvert() {
		fieldDefineMap=new HashMap<>();
	}
	//
	public QueryCondition convert(FilterCondition fc,List<ProjectFieldDefine> fieldDefines) {
		if(fc==null) {
			return null;
		}
		for (ProjectFieldDefine e : fieldDefines) {
			fieldDefineMap.put(e.id, e);
			
		}
		qc=new QueryCondition();
		qc.type=fc.type;
		addItem(null,qc, fc);
		return qc;
	}
	
	private void addItem(QueryCondition parent,QueryCondition curr,FilterCondition item) {
		int conditionCount=getConditionCount(item);//20200130 如果查询条件下没有一个真正的查询条件则忽略掉
		if(conditionCount==0) {
			return;
		}
		if(item.type==FilterCondition.TYPE_查询条件) {
			ProjectFieldDefine define=fieldDefineMap.get(item.key);
			if(define==null) {
				return;
			}
			setupQueryCondition(curr,item,define);
		}
		curr.type=item.type;
		if(parent!=null) {
			if(!curr.ignore) {
				parent.children.add(curr);
			}
		}
		if(item.children!=null) {
			for (FilterCondition fcItem : item.children) {
				addItem(curr,new QueryCondition(),fcItem);
			}
		}
	}
	//获取下一级的查询条件的数量（只是children 不递归）如果没有查询条件则删除这个查询
	private int getConditionCount(FilterCondition condition) {
		if(condition==null) {
			return 0;
		}
		if(condition.type==FilterCondition.TYPE_查询条件) {
			return 1;
		}
		int conditionCount=0;
		if(condition.children!=null) {
			for (FilterCondition child : condition.children) {
				if(child.type==FilterCondition.TYPE_查询条件) {
					conditionCount++;
				}
			}
		}
		return conditionCount;
	}
	//
	private void setupQueryCondition(QueryCondition qc,FilterCondition item,ProjectFieldDefine fieldDefine) {
		int operator=item.operator;
		String key=fieldDefine.field;
		Object value=null;
		if(fieldDefine.isSystemField) {
			if("statusName".equals(key)) {
				makeInOrNullSql(qc, "status", item.intValueList, operator,"状态");
			}else if("priorityName".equals(fieldDefine.field)) {
				makeInOrNullSql(qc, "priority", item.intValueList, operator,"优先级");
			}else if("iterationName".equals(fieldDefine.field)) {
				makeInOrNullSql(qc, "iteration_id", item.intValueList, operator,"迭代");
			}else if("releaseName".equals(fieldDefine.field)) {
				makeInOrNullSql(qc, "release_id", item.intValueList, operator,"Release");
			}else if("subSystemName".equals(fieldDefine.field)) {
				makeInOrNullSql(qc, "sub_system_id", item.intValueList, operator,"子系统");
			}else if("createAccountName".equals(fieldDefine.field)) {
				makeInOrNullSql(qc, "create_account_id", item.intValueList, operator,"创建人");
			}else if("ownerAccountName".equals(fieldDefine.field)) {
				makeJsonContainsSql(qc, "a.owner_account_id_list", item.intValueList, operator,"责任人");
			}else if("categoryIdList".equals(fieldDefine.field)) {
				makeJsonContainsSql(qc, "a.category_id_list", item.intValueList, operator,"分类");
			}else {
				value=getValue(item, fieldDefine);
				qc.operator=operator;
				qc.key=Config.convertFieldName(key);
				qc.value=value;
			}
		}else {//自定义字段
			key=BizUtil.getCustomerSqlKey("a.custom_fields", BizUtil.getCustomerFieldKey(fieldDefine.id));
			qc.operator=operator;
			if(fieldDefine.type==ProjectFieldDefine.TYPE_团队成员选择) {
				value=item.intValueList;
			}else if(fieldDefine.type==ProjectFieldDefine.TYPE_单行文本框) {
				operator = QueryCondition.OPERATOR_LIKE;
				value=item.stringValue;
			}else if(fieldDefine.type==ProjectFieldDefine.TYPE_日期) {
				value=item.dateValue;
			}else if(fieldDefine.type==ProjectFieldDefine.TYPE_数值) {
				value=item.intValue;
			}else if(fieldDefine.type==ProjectFieldDefine.TYPE_单选框||
					fieldDefine.type==ProjectFieldDefine.TYPE_复选框) {
				value=item.stringValueList;
			}
			WhereSql whereSql=SqlUtils.createWhereSql(fieldDefine, key, value, operator);
			if(whereSql==null) {
				qc.ignore=true;
			}else {
				qc.whereSql=whereSql.sql;
				qc.value=whereSql.values;
			}
		}
		//
		logger.info("QueryCondition:{}",DumpUtil.dump(qc));
	}
	
	private void makeInOrNullSql(QueryCondition qc,String dbField,List<Integer> intValueList,int operator,String fieldName) {
		qc.key=dbField;
		qc.value=intValueList;
		if(operator==FilterCondition.OPERATOR_不等于) {
			qc.operator=QueryCondition.OPERATOR_NOT_IN;
		}else if(operator==FilterCondition.OPERATOR_等于) {
			qc.operator=QueryCondition.OPERATOR_IN;
		}else if(operator==FilterCondition.OPERATOR_IS_NULL) {
			qc.operator=QueryCondition.OPERATOR_IS_NULL;
		}else if(operator==FilterCondition.OPERATOR_IS_NOT_NULL) {
			qc.operator=QueryCondition.OPERATOR_IS_NOT_NULL;
		}else {
			logger.warn("dbField operator not support {}",operator);
			throw new AppException(fieldName+"不支持此操作符");
		}
	}
	
	//只针对json数组
	private void makeJsonContainsSql(QueryCondition qc,String dbField,
			List<Integer> intValueList,int operator,String fieldName) {
		if(intValueList==null||intValueList.isEmpty()) {
			qc.ignore=true;
			return;
		}
		StringBuilder sql=new StringBuilder();
		int index=0;
		sql.append(" ( ");
			if(operator==FilterCondition.OPERATOR_IS_NULL) {
				sql.append(dbField+" is null or JSON_LENGTH("+dbField+")=0");
			}else if(operator==FilterCondition.OPERATOR_IS_NOT_NULL) {
				sql.append("JSON_LENGTH("+dbField+")>0");
			}else {
				for (Integer value : intValueList) {
					index++;
					sql.append(" json_contains("+dbField+",'"+value+"') ");
					if(operator==FilterCondition.OPERATOR_不等于) {
						sql.append("=0 ");
					}
					if(index!=intValueList.size()) {
						if(operator==FilterCondition.OPERATOR_不等于) {
							sql.append(" and ");
						}else {
							sql.append(" or ");
						}
					}
				}
			}
		sql.append(" ) ");
		qc.whereSql=sql.toString();
		qc.operator=operator;
	}
	
	private Object getValue(FilterCondition item,ProjectFieldDefine fieldDefine) {
		if(fieldDefine.type==ProjectFieldDefine.TYPE_数值) {
			return item.intValue;
		}else if(fieldDefine.type==ProjectFieldDefine.TYPE_日期) {//只取年月日
			if(fieldDefine.showTimeField) {
				return item.dateValue;
			}else {
				return DateUtil.getBeginOfDay(item.dateValue);
			}
		}else if(fieldDefine.type==ProjectFieldDefine.TYPE_团队成员选择) {
			return item.intValueList;
		}else if(fieldDefine.type==ProjectFieldDefine.TYPE_复选框) {
			return item.intValueList;
		}else if(fieldDefine.type==ProjectFieldDefine.TYPE_单选框) {
			return item.intValueList;
		}else {
			return item.stringValue;
		}
	}
}
