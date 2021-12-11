package cornerstone.biz.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cornerstone.biz.domain.SurveysDefine;
import cornerstone.biz.domain.SurveysDefine.SurveysDefineInfo;
import cornerstone.biz.domain.SurveysDefine.SurveysDefineQuery;
import cornerstone.biz.domain.SurveysFormDefine.SurveysFormDefineInfo;
import cornerstone.biz.domain.SurveysInstance;
import jazmin.core.app.AppException;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;

/**
 * 
 * @author cs
 *
 */
public class SurveysDAO extends ITFDAO{

	/**
	 * 
	 * @return
	 */
	public List<SurveysDefineInfo> getSurveysNoPermissions(SurveysDefineQuery query){
		return getList(query,"accountList","companyRoleList","projectRoleList","departmentList");
	}
	
	/**
	 * 
	 * @param surveysDefineId
	 * @return
	 */
	public SurveysDefine getSurveysDefinePermission(int surveysDefineId){
		Set<String> includeFields=new HashSet<>();
		includeFields.add("id");
		includeFields.add("accountList");
		includeFields.add("companyRoleList");
		includeFields.add("projectRoleList");
		includeFields.add("departmentList");
		return getById(SurveysDefine.class, surveysDefineId, includeFields);
	}
	
	/**
	 * 
	 * @param surveysDefineId
	 * @return
	 */
	public SurveysFormDefineInfo getExistedSurveysFormDefine(int surveysDefineId) {
		SurveysFormDefineInfo bean=getDomain(SurveysFormDefineInfo.class, QueryWhere.create().where("surveys_define_id", surveysDefineId));
		if(bean==null) {
			throw new AppException("表单不存在");
		}
		return bean;
	}

	/**
	 * 查出我填过的问卷调查列表
	 * @param accountId
	 * @return
	 */
	public List<Integer> getMyFilledSurveysDefineIds(String accountUuid,List<Integer> surveysDefineIds){
		if(surveysDefineIds==null||surveysDefineIds.isEmpty()) {
			return new ArrayList<>();
		}
		StringBuilder sql=new StringBuilder();
		sql.append("select distinct(a.id) from t_surveys_define a "
				+ "\ninner join t_surveys_instance i1 on a.id=i1.`surveys_define_id` and i1.`account_uuid`='"+accountUuid+"' ");
		sql.append("\nwhere a.id in(");
		for (Integer surveysDefineId : surveysDefineIds) {
			sql.append(surveysDefineId).append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		sql.append(" and i1.status="+SurveysInstance.STATUS_已填写);
		return queryForIntegers(sql.toString());
	}

	public SurveysInstance getSurveysInstance(String accountUuid, int surveysDefineId) {
		return getDomain(SurveysInstance.class, QueryWhere.create().
				where("account_uuid", accountUuid).where("surveys_define_id",surveysDefineId));
	}
	
	public SurveysInstance getSurveysInstanceForUpdate(String accountUuid, int surveysDefineId) {
		return getDomain(SurveysInstance.class, QueryWhere.create().
				where("account_uuid", accountUuid).
				where("surveys_define_id",surveysDefineId).
				forUpdate());
	}

	public int getSurveysDefineSubmitCount(int surveysDefineId) {
		return queryCount("select count(1) from t_surveys_instance "
				+ "where surveys_define_id=? and status=? ", surveysDefineId,SurveysInstance.STATUS_已填写);
	}

	public void resetSurveysInstances(int surveysDefineId) {
		executeUpdate("delete from t_surveys_instance "
				+ "where surveys_define_id=? ", surveysDefineId);
	}
}
