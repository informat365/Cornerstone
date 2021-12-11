package cornerstone.biz.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.Filter;
import cornerstone.biz.domain.FilterCondition;
import cornerstone.biz.domain.ProjectFieldDefine;
import cornerstone.biz.domain.QueryCondition;
import cornerstone.biz.domain.QueryConditionConvert;
import cornerstone.biz.domain.WhereSql;
import cornerstone.biz.domain.ProjectFieldDefine.ProjectFieldDefineQuery;
import cornerstone.biz.domain.Task.TaskQuery;
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
public class SqlUtils {
	//
	private static Logger logger=LoggerFactory.get(SqlUtils.class);
	//
	/**
	 * 
	 * @param define
	 * @param sqlKey	custom_fields->'$."field_11"'
	 * @param value
	 * @param operator	QueryCondition.operator
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static WhereSql createWhereSql(ProjectFieldDefine define,String sqlKey,Object value,int operator) {
		if(logger.isDebugEnabled()) {
			logger.debug("createWhereSql sqlKey:{} value:{} operator:{}",sqlKey,value,operator);
		}
		if(sqlKey==null||value==null) {
			return null;
		}
		String strOperation=QueryConditionWhere.getOperator(operator);
		StringBuilder sql=new StringBuilder();
		List<Object> values=new ArrayList<>();
		if(operator==FilterCondition.OPERATOR_IS_NULL||operator==FilterCondition.OPERATOR_IS_NOT_NULL) {
			sql.append(" "+sqlKey+" "+strOperation+" ");
		}else {
			if(define.type==ProjectFieldDefine.TYPE_单行文本框) {
				if(value.toString().trim().length()==0) {
					return null;
				}
				sql.append(" "+sqlKey+" "+strOperation+" concat('%',?,'%') ");
				values.add(value);
			}else if(define.type==ProjectFieldDefine.TYPE_日期||
					define.type==ProjectFieldDefine.TYPE_数值) {
				if(value.toString().trim().length()==0) {
					return null;
				}
				sql.append(" "+sqlKey+" "+strOperation+" ? ");
				values.add(value);
			}else if(define.type==ProjectFieldDefine.TYPE_单选框) {
				List<String> idjo=(List<String>) value;
				if(idjo==null||idjo.size()==0) {
					return null;
				}
				sql.append(" json_contains(JSON_ARRAY( ");
				for (int j=0;j<idjo.size();j++) {
			 		sql.append("?,");
			 		String inValue=(String)idjo.get(j);
			 		values.add(inValue);
			 	}
			 	sql.deleteCharAt(sql.length()-1);
			 	sql.append("),"+sqlKey+" ");
			 	sql.append(") ");
			 	if(operator==QueryCondition.OPERATOR_不等于) {
					sql.append("=0 ");
				}
			}else if(define.type==ProjectFieldDefine.TYPE_复选框) {
				List<String> idjo=(List<String>) value;
				if(idjo==null||idjo.size()==0) {
					return null;
				}
				sql.append(" (");
				for (int j=0;j<idjo.size();j++) {
					sql.append(" json_contains(").append(sqlKey).append(",JSON_ARRAY(?)").append(") ");
					if(operator==QueryCondition.OPERATOR_不等于) {
						sql.append("=0 ");
					}
					String arrValue=(String)idjo.get(j);
					values.add(arrValue);
					if(j!=(idjo.size()-1)) {
						if(operator==QueryCondition.OPERATOR_不等于) {
							sql.append(" and ");
						}else {
							sql.append(" or ");
						}
					}
				}
				sql.append(") ");
			}else if(define.type==ProjectFieldDefine.TYPE_团队成员选择) {//List<Integer>
				List<Integer> idjo=(List<Integer>) value;
				if(idjo==null||idjo.size()==0) {
					return null;
				}
				sql.append(" (");
				for (int j=0;j<idjo.size();j++) {
					sql.append(" json_contains(").append(sqlKey).append(",JSON_ARRAY(?)").append(") ");
					if(operator==QueryCondition.OPERATOR_不等于) {
						sql.append("=0 ");
					}
					int arrValue=(int)idjo.get(j);
					values.add(arrValue);
					if(j!=(idjo.size()-1)) {
						if(operator==QueryCondition.OPERATOR_不等于) {
							sql.append(" and ");
						}else {
							sql.append(" or ");
						}
					}
				}
				sql.append(") ");
			}else {
				throw new AppException("自定义字段类型错误"+define.type);
			}
		}
		//
		WhereSql whereSql=new WhereSql();
		whereSql.sql=sql.toString();
		whereSql.values=values;
		if(logger.isDebugEnabled()) {
			logger.debug("whereSql:{}",DumpUtil.dump(whereSql));
		}
		return whereSql;
	}
	//
	public static String makeJsonContainsSql(String dbField,int[] intValueList,int operator) {
		StringBuilder sql=new StringBuilder();
		int index=0;
		sql.append(" ( ");
		for (Integer value : intValueList) {
			index++;
			sql.append(" json_contains("+dbField+",'"+value+"') ");
			if(operator==FilterCondition.OPERATOR_不等于) {
				sql.append("=0 ");
			}
			if(index!=intValueList.length) {
				if(operator==FilterCondition.OPERATOR_不等于) {
					sql.append(" and ");
				}else {
					sql.append(" or ");
				}
			}
		}
		sql.append(" ) ");
		return sql.toString();
	}
	//
	public static String addFilterSql(BizDAO dao,Account account,TaskQuery query,List<Object> values) {
		if(query.filterId==null) {
			return "";
		}
		StringBuilder addWhereSql=new StringBuilder();
		if(query.filterId<0) {
			if(query.filterId==-1) {//我的任务
				addWhereSql.append(" and ( ");
				addWhereSql.append(SqlUtils.makeJsonContainsSql("a.owner_account_id_list",
						new int[] {account.id}, FilterCondition.OPERATOR_等于));
				addWhereSql.append(" ) \n");
			}
			if(query.filterId==-2) {//待认领任务
				addWhereSql.append(" and ( JSON_LENGTH(a.owner_account_id_list)=0 ) ");
			}
			if(query.filterId==-3) {//已完成任务
				addWhereSql.append(" and ( a.is_finish=1 ) ");
			}
			if(query.filterId==-4) {//未完成任务
				addWhereSql.append(" and ( a.is_finish=0 ) ");
			}
			if(query.filterId==-5) {//我参与的任务
				addWhereSql.append(" and ( a.id in("
						+" select distinct(task_id) from t_task_status_change_log  where json_contains(before_owner_id_list,'"+account.id+"') or json_contains(after_owner_id_list,'"+account.id+"')"
						+ ") ) ");
			}
			if(query.filterId==-6) {//逾期任务
				addWhereSql.append(" and (a.end_date is not null and a.end_date<curdate() and a.is_finish=false ) ");
			}
			if(query.filterId==-7) {//今天到期
				addWhereSql.append(" and (a.end_date is not null and a.end_date=curdate() and a.is_finish=false ) ");
			}
			if(query.filterId==-8) {//提前完成
				addWhereSql.append(" and (a.is_finish=true and (a.finish_time<a.end_date or a.finish_time<a.expect_end_date) ) ");
			}
		}else {
			Filter filter=dao.getById(Filter.class, query.filterId);
			if(filter!=null) {
				WhereStatment ws=getWhereStatment(dao,query.projectId,query.objectType,filter);
				if(ws!=null) {
					if(!BizUtil.isNullOrEmpty(ws.values)) {
						values.addAll(Arrays.asList(ws.values));
					}
					addWhereSql.append(ws.sql).append(" ");
				}
			}
		}
		return addWhereSql.toString();
	}
	//
	public static WhereStatment getWhereStatment(BizDAO dao,int projectId,int objectType,Filter filter) {
		QueryConditionWhere qc=new QueryConditionWhere();
		return qc.whereStatement(createQueryCondition(dao,projectId,objectType,filter));
	}
	
	public static QueryCondition createQueryCondition(BizDAO dao,int projectId,int objectType,Filter filter) {
		QueryConditionConvert qcc=new QueryConditionConvert();
		ProjectFieldDefineQuery fieldDefineQuery=new ProjectFieldDefineQuery();
		fieldDefineQuery.projectId=projectId;
		fieldDefineQuery.objectType=objectType;
		fieldDefineQuery.sortWeightSort=ProjectFieldDefineQuery.SORT_TYPE_ASC;
		fieldDefineQuery.pageSize=Integer.MAX_VALUE;
		List<ProjectFieldDefine> list=dao.getList(fieldDefineQuery);
		return qcc.convert(filter.condition,list);
	}
}
