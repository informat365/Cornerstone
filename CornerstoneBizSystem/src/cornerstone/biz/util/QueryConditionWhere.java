package cornerstone.biz.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cornerstone.biz.domain.QueryCondition;
import jazmin.core.app.AppException;
import jazmin.driver.jdbc.smartjdbc.QueryWhere.WhereStatment;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;

/**
 * 
 * @author cs
 *
 */
public class QueryConditionWhere {
	//
	private static Logger logger=LoggerFactory.get(QueryConditionWhere.class);
	//
	private StringBuilder sql=new StringBuilder();
	private List<Object> valueList=new LinkedList<Object>();
	private int totalCondionCount=0;
	//
	public QueryConditionWhere() {
		
	}
	//
	public WhereStatment whereStatement(QueryCondition qc) {
		if(qc==null) {
			return null;
		}
		WhereStatment ws=new WhereStatment();
		makeWhereSql(0,qc.type,qc);
		if(totalCondionCount==0) {//没有一个查询条件
			return null;
		}
		ws.sql=" and ("+sql.toString()+") ";
		ws.values=valueList.toArray();
		logger.info("whereStatement: qc:{} \n{}",DumpUtil.dump(qc),DumpUtil.dump(ws));
		return ws;
	}	
	//
	public void makeWhereSql(int index,int type,QueryCondition bean) {
		if(index>1) {
			if(type==QueryCondition.TYPE_满足所有条件) {
				sql.append(" and ");
			}else {
				sql.append(" or ");
			}
		}
		if(bean.type==QueryCondition.TYPE_查询条件) {
			totalCondionCount++;
			appendWhere(bean);
		}else {
			List<QueryCondition>conditionItems=getConditionItems(bean.children);
			if(conditionItems.size()==0) {
				return;
			}
			sql.append(" (");
			int i=1;
			for (QueryCondition child : bean.children) {
				makeWhereSql(i,bean.type,child);
				i++;
			}
			sql.append(") ");
		}
	}
	//
	private List<QueryCondition> getConditionItems(List<QueryCondition> items) {
		List<QueryCondition> conditionItems=new ArrayList<>();
		for(QueryCondition qc :items) {
			if(qc.type==QueryCondition.TYPE_查询条件) {
				conditionItems.add(qc);
			}
		}
		return conditionItems;
	}
	//
	public static String getOperator(int operator) {
		if(operator==QueryCondition.OPERATOR_等于) {
			return "=";
		}else if(operator==QueryCondition.OPERATOR_不等于) {
			return "!=";
		}else if(operator==QueryCondition.OPERATOR_大于) {
			return ">";
		}else if(operator==QueryCondition.OPERATOR_小于) {
			return "<";
		}else if(operator==QueryCondition.OPERATOR_大于等于) {
			return ">=";
		}else if(operator==QueryCondition.OPERATOR_小于等于) {
			return "<=";
		}else if(operator==QueryCondition.OPERATOR_LIKE) {
			return "like";
		}else if(operator==QueryCondition.OPERATOR_IN) {
			return "in";
		}else if(operator==QueryCondition.OPERATOR_NOT_IN) {
			return "not in";
		}else if(operator==QueryCondition.OPERATOR_IS_NULL) {
			return "is null";
		}else if(operator==QueryCondition.OPERATOR_IS_NOT_NULL) {
			return "is not null";
		}
		throw new AppException("operator not support."+operator);
	}
	//
	@SuppressWarnings("unchecked")
	private void appendWhere(QueryCondition w) {
		if(!StringUtil.isEmpty(w.whereSql)) {
			sql.append(" ").append(w.whereSql).append(" \n");
			if(w.value!=null) {//List<Object>
				if(List.class.isAssignableFrom(w.value.getClass())) { 
					List<Object> list=(List<Object>)w.value;
					for (Object v : list) {
						valueList.add(v);
					}
				}else {
					valueList.add(w.value);
				}
			}
			return;
		}
		if(w.alias!=null) {
			sql.append(w.alias).append(".");
		}
		sql.append("`").append(w.key).append("` ");
		sql.append(getOperator(w.operator)).append(" ");
		if(w.operator==QueryCondition.OPERATOR_LIKE){
			sql.append(" concat('%',?,'%') ");
			valueList.add(w.value);
		}else if(w.operator==QueryCondition.OPERATOR_IN) {
			List<Object> values=(List<Object>)w.value;
			if(values!=null&&values.size()>0) {
				sql.append(" ( ");
				for (int i = 0; i < values.size(); i++) {
					sql.append(" ?,");
					valueList.add(values.get(i));
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(" ) ");
			}
		}else if(w.operator==QueryCondition.OPERATOR_NOT_IN) {
			List<Object> values=(List<Object>)w.value;
			if(values!=null&&values.size()>0) {
				sql.append(" ( ");
				for (int i = 0; i < values.size(); i++) {
					sql.append(" ?,");
					valueList.add(values.get(i));
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(" ) ");
			}
		}else if(w.operator==QueryCondition.OPERATOR_IS_NULL) {
			
		}else if(w.operator==QueryCondition.OPERATOR_IS_NOT_NULL) {
			
		}else{
			sql.append("  ? ");
			valueList.add(w.value);
		}
	}
}
