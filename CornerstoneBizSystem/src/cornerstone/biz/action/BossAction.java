package cornerstone.biz.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cornerstone.biz.BizExceptionCode;
import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.dao.BossDAO;
import cornerstone.biz.domain.Account.AccountQuery;
import cornerstone.biz.domain.AccountChangeLogNum.AccountChangeLogNumQuery;
import cornerstone.biz.domain.BossIndexInfo;
import cornerstone.biz.domain.Company.CompanyQuery;
import cornerstone.biz.domain.CompanyTaskNum;
import cornerstone.biz.domain.CompanyTaskNum.CompanyTaskNumQuery;
import cornerstone.biz.domain.Config;
import cornerstone.biz.domain.Config.ConfigQuery;
import cornerstone.biz.domain.DailyStatData;
import cornerstone.biz.domain.User;
import cornerstone.biz.domain.User.UserInfo;
import cornerstone.biz.domain.UserToken;
import cornerstone.biz.srv.BossService;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.util.StatUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;

/**
 * 
 * @author cs
 *
 */
@ApiDefine("Boss后台调用接口")
public interface BossAction{

	/**登录*/
	String login(String userName,String password);
	
	/**登出*/
	void logout(String token);
	
	/**获取首页数据*/
	BossIndexInfo getIndexInfo(String token);
	
	/***/
	List<Config> getConfigList(String token,ConfigQuery query);

	/**查询企业列表*/
	Map<String,Object> getCompanyList(String token,CompanyQuery query);
	
	/***/
	List<DailyStatData> getDailyStatDataList(String token);
	
	/**查询账号列表*/
	Map<String,Object> getAccountList(String token,AccountQuery query);
	
	/**查询企业新增任务数量*/
	Map<String,Object> getCompanyTaskNumList(String token, CompanyTaskNumQuery query);
	
	/**查询账号变更记录数量*/
	Map<String,Object> getAccountChangeLogNumList(String token, AccountChangeLogNumQuery query);
	//
	//END
	@RpcService
	class BossActionImpl extends CommActionImpl implements BossAction {
		//
		private static Logger logger=LoggerFactory.get(BossActionImpl.class);
		//
		@AutoWired
		public BossDAO dao;
		@AutoWired
		public BossService service;
		//
		private void checkLogin(String token) {
			getExistedUserInfoByToken(token);
		}
		//
		private UserInfo getExistedUserInfoByToken(String token) {
			UserToken at = dao.getUserTokenByToken(token);
			if (at == null) {
				throw new AppException(BizExceptionCode.CODE_TOKEN过期, "TOKEN已过期");
			}
			return dao.getExistedById(UserInfo.class, at.userId);
		}
		//
		private UserInfo getUserInfoByToken(String token) {
			UserToken at = dao.getUserTokenByToken(token);
			if (at == null) {
				return null;
			}
			return dao.getById(UserInfo.class, at.userId);
		}
		//
		@Override
		public List<Config> getConfigList(String token, ConfigQuery query) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Transaction
		@Override
		public String login(String userName, String password) {
			if(userName==null||password==null) {
				throw new AppException("用户名或密码错误");
			}
			User user=dao.getUserByUserNameForUpdate(userName);
			if(user==null) {
				throw new AppException("用户名或密码错误");
			}
			if(user.status!=User.STATUS_有效) {
				throw new AppException("用户已无效");
			}
			if(!BizUtil.encryptPassword(password).equals(user.password)) {
				throw new AppException("用户名或密码错误");
			}
			UserToken userToken=loginSuccess(user);
			return userToken.token;
		}
		
		private UserToken loginSuccess(User user) {
			UserToken userToken = dao.getUserTokenByUserId(user.id);
			if (userToken == null) {
				userToken = new UserToken();
				userToken.userId = user.id;
				userToken.token = BizUtil.randomUUID();
				userToken.createTime = new Date();
				userToken.updateTime = new Date();
				dao.add(userToken);
			}
//			else {
////				userToken.token = BizUtil.randomUUID();
////				dao.update(userToken);
//			}
			user.lastLoginTime = new Date();
			dao.update(user);
			return userToken;
		}
		
		@Override
		public void logout(String token) {
			UserToken userToken = dao.getUserTokenByTokenForUpdate(token);
			if(userToken==null) {
				return;
			}
			userToken.token = BizUtil.randomUUID();
			dao.update(userToken);
		}
		
		@Override
		public BossIndexInfo getIndexInfo(String token) {
			checkLogin(token);
			BossIndexInfo info=new BossIndexInfo();
			info.totalCompanyNum=dao.getTotalCompanyNum();
			info.todayNewCompanyNum=dao.getTodayNewCompanyNum();
			info.todayAccountLoginNum=dao.getTodayAccountLoginNum();
			info.todayCompanyLoginNum=dao.getTodayCompanyLoginNum();
			info.todayCreateAccountNum=dao.getTodayCreateAccountNum();
			info.totalAccountNum=dao.getTotalAccountNum();
			return info;
		}
		
		@Override
		public Map<String,Object> getCompanyList(String token, CompanyQuery query) {
			checkLogin(token);
			return createResult(dao.getList(query), dao.getListCount(query));
		}
		
		@Override
		public List<DailyStatData> getDailyStatDataList(String token) {
			checkLogin(token);
			Date startDate = DateUtil.getBeginOfDay(DateUtil.getNextDay(-30));
			Date endDate = DateUtil.getToday();
			List<DailyStatData> result = dao.getCompanyCreateDayDataList(startDate,endDate);
			List<DailyStatData> inviteList = dao.getInviteMemberDayDataList(startDate,endDate);
//			List<DailyStatData> fileCreateList = dao.getFileCreateDayDataList(startDate,endDate);
//			List<DailyStatData> taskCreateList = dao.getTaskCreateDayDataList(startDate,endDate);
			List<DailyStatData> accountCreateList = dao.getAccountCreateDayDataList(startDate,endDate);
			Map<Date,Integer> inviteNumMap=new HashMap<>();
			for (DailyStatData e : inviteList) {
				inviteNumMap.put(e.statDate, e.inviteNum);
			}
//			Map<Date,Integer> fileCreateNumMap=new HashMap<>();
//			for (DailyStatData e : fileCreateList) {
//				fileCreateNumMap.put(e.statDate, e.fileCreateNum);
//			}
//			Map<Date,Integer> taskCreateNumMap=new HashMap<>();
//			for (DailyStatData e : taskCreateList) {
//				taskCreateNumMap.put(e.statDate, e.taskCreateNum);
//			}
			//
			Map<Date,Integer> accountCreateNumMap=new HashMap<>();
			for (DailyStatData e : accountCreateList) {
				accountCreateNumMap.put(e.statDate, e.accountCreateNum);
			}
			//
			for (DailyStatData e : result) {
				if(inviteNumMap.containsKey(e.statDate)) {
					e.inviteNum=inviteNumMap.get(e.statDate);
				}
				if(accountCreateNumMap.containsKey(e.statDate)) {
					e.accountCreateNum=accountCreateNumMap.get(e.statDate);
				}
//				if(fileCreateNumMap.containsKey(e.statDate)) {
//					e.fileCreateNum=fileCreateNumMap.get(e.statDate);
//				}
//				if(taskCreateNumMap.containsKey(e.statDate)) {
//					e.taskCreateNum=taskCreateNumMap.get(e.statDate);
//				}
			}
			return StatUtil.buDays(DailyStatData.class, startDate, 
					DateUtil.getNextDay(endDate,-1), result, "statDate", true,null);
		}
		
		@Override
		public Map<String, Object> getAccountList(String token, AccountQuery query) {
			checkLogin(token);
			return createResult(dao.getList(query), dao.getListCount(query));
		}
		
		@Override
		public Map<String,Object> getCompanyTaskNumList(String token, CompanyTaskNumQuery query) {
			checkLogin(token);
			if(query.createTimeStart==null) {
				query.createTimeStart=DateUtil.getBeginOfDay(DateUtil.getNextDay(-7));
			}
			List<CompanyTaskNum> list=dao.getCompanyTaskNumList(query);
			if(list.isEmpty()) {
				return createResult(list, 0);
			}
			StringBuilder inCompanyIds=new StringBuilder();
			for (CompanyTaskNum e : list) {
				inCompanyIds.append(e.companyId).append(",");
			}
			inCompanyIds.deleteCharAt(inCompanyIds.length()-1);
			//查询企业员工数量
			List<CompanyTaskNum> memberList=dao.getCompanyMemberNumList(inCompanyIds.toString());
			//查询企业项目数量
			List<CompanyTaskNum> projectList=dao.getCompanyProjectNumList(inCompanyIds.toString());
			//查询企业文件数量
			List<CompanyTaskNum> fileList=dao.getCompanyFileNumList(inCompanyIds.toString());
			//查询企业wikiPage数量
			List<CompanyTaskNum> wikiPageList=dao.getCompanyWikiPageNumList(inCompanyIds.toString());
			//
			for (CompanyTaskNum num : memberList) {
				CompanyTaskNum taskNum=BizUtil.getByTarget(list, "companyId", num.companyId);
				taskNum.memberNum=num.memberNum;
			}
			for (CompanyTaskNum num : projectList) {
				CompanyTaskNum taskNum=BizUtil.getByTarget(list, "companyId", num.companyId);
				taskNum.projectNum=num.projectNum;
			}
			for (CompanyTaskNum num : fileList) {
				CompanyTaskNum taskNum=BizUtil.getByTarget(list, "companyId", num.companyId);
				taskNum.fileNum=num.fileNum;
			}
			for (CompanyTaskNum num : wikiPageList) {
				CompanyTaskNum taskNum=BizUtil.getByTarget(list, "companyId", num.companyId);
				taskNum.wikiPageNum=num.wikiPageNum;
			}
			//
			return createResult(list,dao.getCompanyTaskNumListCount(query));
		}
		
		@Override
		public Map<String, Object> getAccountChangeLogNumList(String token, AccountChangeLogNumQuery query) {
			checkLogin(token);
			if(query.createTimeStart==null) {
				query.createTimeStart=DateUtil.getBeginOfDay(DateUtil.getNextDay(-7));
			}
			return createResult(dao.getAccountChangeLogNumList(query),
					dao.getAccountChangeLogNumListCount(query));
		}
	}
}
