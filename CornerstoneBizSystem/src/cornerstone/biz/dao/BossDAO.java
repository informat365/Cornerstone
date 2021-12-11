package cornerstone.biz.dao;

import java.util.Date;
import java.util.List;

import cornerstone.biz.domain.AccountChangeLogNum;
import cornerstone.biz.domain.AccountChangeLogNum.AccountChangeLogNumQuery;
import cornerstone.biz.domain.CompanyTaskNum;
import cornerstone.biz.domain.CompanyTaskNum.CompanyTaskNumQuery;
import cornerstone.biz.domain.DailyStatData;
import cornerstone.biz.domain.User;
import cornerstone.biz.domain.UserToken;
import cornerstone.biz.util.DateUtil;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;

/**
 * @author cs
 */
public class BossDAO extends ITFDAO {
	
	/**
	 * 
	 * @param userName
	 * @return
	 */
	public User getUserByUserName(String userName) {
		return getDomain(User.class,QueryWhere.create().where("user_name", userName));
	}
	
	/**
	 * 
	 * @param userName
	 * @return
	 */
	public User getUserByUserNameForUpdate(String userName) {
		return getDomain(User.class,
				QueryWhere.create().where("user_name", userName).
				forUpdate());
	}
	//
	/**
	 * 
	 * @param id
	 * @return
	 */
	public UserToken getUserTokenByUserId(int accountId) {
		return getDomain(UserToken.class,QueryWhere.create().where("user_id", accountId));
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public UserToken getUserTokenByToken(String token) {
		return getDomain(UserToken.class,QueryWhere.create().where("token", token));
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public UserToken getUserTokenByTokenForUpdate(String token) {
		return getDomain(UserToken.class,QueryWhere.create().where("token", token).forUpdate());
	}
	
	/**
	 * 
	 * @param accountId
	 * @return
	 */
	public UserToken getUserTokenByUserIdForUpdate(int accountId) {
		return getDomain(UserToken.class,QueryWhere.create().where("user_id", accountId).forUpdate());
	}

	/**
	 * 查询注册企业总数
	 * @return
	 */
	public int getTotalCompanyNum() {
		return queryCount("select count(1) from t_company");
	}

	/**
	 * 查询今日注册数量
	 * @return
	 */
	public int getTodayNewCompanyNum() {
		return queryCount("select count(1) from t_company where create_time>=?",
				DateUtil.getBeginOfDay(new Date()));
	}

	/**
	 * 查询最近days天的
	 * @param pageSize
	 * @return
	 */
	public List<DailyStatData> getCompanyCreateDayDataList(Date startDate,Date endDate) {
		String sql="select DATE_FORMAT(create_time, '%Y-%m-%d') as stat_date,count(1) as company_create_num from t_company "
				+ " where create_time>=? and create_time<?"
				+ " group by (stat_date) order by stat_date desc;";
		return queryList(DailyStatData.class, sql, startDate,endDate);
	}
	
	public List<DailyStatData> getInviteMemberDayDataList(Date startDate,Date endDate) {
		String sql="select DATE_FORMAT(create_time, '%Y-%m-%d') as stat_date,count(1) as invite_num from t_company_member_invite "
				+ " where create_time>=? and create_time<?"
				+ " group by (stat_date) order by stat_date desc;";
		return queryList(DailyStatData.class, sql, startDate,endDate);
	}
	
	public List<DailyStatData> getFileCreateDayDataList(Date startDate,Date endDate) {
		String sql="select DATE_FORMAT(create_time, '%Y-%m-%d') as stat_date,count(1) as file_create_num from t_file "
				+ " where create_time>=? and create_time<?"
				+ " group by (stat_date) order by stat_date desc;";
		return queryList(DailyStatData.class, sql, startDate,endDate);
	}
	
	public List<DailyStatData> getTaskCreateDayDataList(Date startDate,Date endDate) {
		String sql="select DATE_FORMAT(create_time, '%Y-%m-%d') as stat_date,count(1) as task_create_num from t_task "
				+ " where create_time>=? and create_time<? and name not like '【示例项目】%'"
				+ " group by (stat_date) order by stat_date desc;";
		return queryList(DailyStatData.class, sql, startDate,endDate);
	}
	
	public List<DailyStatData> getAccountCreateDayDataList(Date startDate,Date endDate) {
		String sql="select DATE_FORMAT(create_time, '%Y-%m-%d') as stat_date,count(1) as account_create_num from t_account "
				+ " where create_time>=? and create_time<? "
				+ " group by (stat_date) order by stat_date desc;";
		return queryList(DailyStatData.class, sql, startDate,endDate);
	}
	
	/**
	 * 查询企业最近7天的任务数量(排除示例项目)
	 * @param startDate
	 * @return
	 */
	public List<CompanyTaskNum> getCompanyTaskNumList(CompanyTaskNumQuery query) {
		String sql="select b.id as company_id,b.name as company_name,count(1) as num from t_task a "
				+ "inner join t_company b on a.company_id=b.id "
				+ "where a.create_time>=? and a.name not like '【示例项目】%' "
				+ "group by b.id,b.name "
				+ "order by num desc "
				+ "limit ?,?;\r\n" + 
				"\r\n" + 
				"";
		return queryList(CompanyTaskNum.class, sql, query.createTimeStart,
				(query.pageIndex-1)*query.pageSize,query.pageSize);
	}
	
	/**
	 * 查看企业员工数量
	 * @param inCompanys
	 * @return
	 */
	public List<CompanyTaskNum> getCompanyMemberNumList(String inCompanys) {
		String sql="select company_id,count(1) as member_num from t_company_member "
				+ "where company_id in("+inCompanys+") group by company_id;"; 
		return queryList(CompanyTaskNum.class, sql);
	}
	
	/**
	 * 查看企业项目数量
	 * @param inCompanys
	 * @return
	 */
	public List<CompanyTaskNum> getCompanyProjectNumList(String inCompanys) {
		String sql="select company_id,count(1) as project_num from t_project "
				+ "where company_id in("+inCompanys+") and name not like '%示例%' group by company_id;"; 
		return queryList(CompanyTaskNum.class, sql);
	}
	
	/**
	 * 查看企业文件数量
	 * @param inCompanys
	 * @return
	 */
	public List<CompanyTaskNum> getCompanyFileNumList(String inCompanys) {
		String sql="select company_id,count(1) as file_num from t_file "
				+ "where company_id in("+inCompanys+") group by company_id;"; 
		return queryList(CompanyTaskNum.class, sql);
	}
	
	/**
	 * 
	 * @param inCompanys
	 * @return
	 */
	public List<CompanyTaskNum> getCompanyWikiPageNumList(String inCompanys) {
		String sql="select company_id,count(1) as wiki_page_num from t_wiki_page "
				+ "where company_id in("+inCompanys+") group by company_id;"; 
		return queryList(CompanyTaskNum.class, sql);
	}
	/**
	 * 
	 * @param query
	 * @return
	 */
	public int getCompanyTaskNumListCount(CompanyTaskNumQuery query) {
		String sql="select count(distinct(b.id)) from t_task a "
				+ "inner join t_company b on a.company_id=b.id "
				+ "where a.create_time>=? and a.name not like '【示例项目】%';";
		return queryCount(sql, query.createTimeStart);
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public List<AccountChangeLogNum> getAccountChangeLogNumList(AccountChangeLogNumQuery query){
		String sql="select account_id,account_name,company_name,sum(num) as num from\r\n" + 
				"(\r\n" + 
				"select  a.create_account_id as account_id,i1.name as account_name,i2.name as company_name,count(1) as num from  t_change_log a\r\n" + 
				"inner join t_account i1 on i1.id=a.create_account_id \r\n" + 
				"inner join t_company i2 on i2.id=a.company_id \r\n" + 
				"inner join t_project i3 on i3.id=a.project_id\r\n" + 
				"where a.create_time>=? and a.create_account_id>0 and i3.name!='示例项目'\r\n" + 
				"group by a.create_account_id,i1.name,i2.name \r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  a.create_account_id as account_id,i1.name as account_name,i2.name as company_name,count(1) as num from  t_change_log a\r\n" + 
				"inner join t_account i1 on i1.id=a.create_account_id \r\n" + 
				"inner join t_company i2 on i2.id=a.company_id \r\n" + 
				"inner join t_task i4 on i4.id=a.task_id\r\n" + 
				"inner join t_project i3 on i3.id=i4.project_id\r\n" + 
				"where a.create_time>=? and a.create_account_id>0 and i3.name!='示例项目'\r\n" + 
				"group by a.create_account_id,i1.name,i2.name ) t\r\n" + 
				"\r\n" + 
				"group by t.account_id,t.account_name,t.company_name\r\n" + 
				"order by num desc limit ?,?;";
		return queryList(AccountChangeLogNum.class, sql, 
				query.createTimeStart,
				query.createTimeStart,
				(query.pageIndex-1)*query.pageSize,query.pageSize);
		
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public int getAccountChangeLogNumListCount(AccountChangeLogNumQuery query){
		String sql="select count(distinct(account_id)) from\r\n" + 
				"(\r\n" + 
				"select  a.create_account_id as account_id,i1.name as account_name,i2.name as company_name,count(1) as num from  t_change_log a\r\n" + 
				"inner join t_account i1 on i1.id=a.create_account_id \r\n" + 
				"inner join t_company i2 on i2.id=a.company_id \r\n" + 
				"inner join t_project i3 on i3.id=a.project_id\r\n" + 
				"where a.create_time>=? and a.create_account_id>0 and i3.name!='示例项目'\r\n" + 
				"group by a.create_account_id,i1.name,i2.name \r\n" + 
				"\r\n" + 
				"UNION ALL\r\n" + 
				"\r\n" + 
				"select  a.create_account_id as account_id,i1.name as account_name,i2.name as company_name,count(1) as num from  t_change_log a\r\n" + 
				"inner join t_account i1 on i1.id=a.create_account_id \r\n" + 
				"inner join t_company i2 on i2.id=a.company_id \r\n" + 
				"inner join t_task i4 on i4.id=a.task_id\r\n" + 
				"inner join t_project i3 on i3.id=i4.project_id\r\n" + 
				"where a.create_time>=? and a.create_account_id>0 and i3.name!='示例项目'\r\n" + 
				"group by a.create_account_id,i1.name,i2.name ) t;";
		return queryCount(sql, query.createTimeStart,query.createTimeStart);
		
	}

	public int getTodayAccountLoginNum() {
		return queryCount("select count(1) from t_account where last_login_time>=?",
				DateUtil.getBeginOfDay(new Date()));
	}
	
	public int getTodayCompanyLoginNum() {
		return queryCount("select count(1) from (select company_id from t_account where last_login_time>=? group by company_id)b;",
				DateUtil.getBeginOfDay(new Date()));
	}
	
	public int getTodayCreateAccountNum() {
		return queryCount("select count(1) from t_account where create_time>=?",
				DateUtil.getBeginOfDay(new Date()));
	}

	public int getTotalAccountNum() {
		return queryCount("select count(1) from t_account");
	}
}
