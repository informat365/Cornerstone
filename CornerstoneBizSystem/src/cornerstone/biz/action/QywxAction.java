package cornerstone.biz.action;

import java.util.HashMap;
import java.util.Map;

import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.AccountToken;
import cornerstone.biz.domain.wx.WeixinMessage;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.srv.QywxService;
import cornerstone.biz.srv.QywxService.UserInfo;
import cornerstone.biz.srv.WeixinService;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.DumpUtil;

/**
 * 企业微信Action
 * @author cs
 *
 */
public interface QywxAction {

	/**
	 * 
	 * @param xml
	 * @return
	 * @throws AppException
	 */
	void receiveSuiteMessage(String xml) throws AppException;
	
	/**企业微信登录*/
	String qywxLogin(String code);
	
	/**绑定企业微信*/
	void bind(String token, String code);

	/**取消绑定企业微信*/
	void unbind(String token);
	
	/**是否绑定*/
	boolean isBind(String token);
	// --------------------------------------------------------------------------
	@RpcService
	public static class QywxActionImpl implements QywxAction {
		//
		@AutoWired
		QywxService qywxService;
		@AutoWired
		BizService bizService;
		@AutoWired
		BizDAO dao;
		//
		private static Logger logger = LoggerFactory.get(QywxActionImpl.class);
		//
		@Transaction
		@Override
		public void receiveSuiteMessage(String xml) throws AppException {
			WeixinMessage msg = WeixinService.parseWeixinMessage(xml);
			if (logger.isDebugEnabled()) {
				logger.debug("receive weixin message \n{} msg:{}", xml, DumpUtil.dump(msg));
			}
			// callback
			Map<String, Object> args = new HashMap<>();
			args.put("msg", msg);
//			Jazmin.notificationCenter.post(NotificationEvent.Notification_微信公众号收到微信消息, args);
			//
		}
		@Override
		public String qywxLogin(String code) {
			qywxService.refreshAccessToken();
			UserInfo userInfo=qywxService.getUserInfo(code);
			Account account=dao.getAccountByQywxUserId(userInfo.UserId);
			AccountToken userToken = dao.getAccountTokenByAccountId(account.id);
			return userToken.token;
		}
		
		@Override
		public void bind(String token, String code) {
			Account account=bizService.getExistedAccountByToken(token);
			if(!bizService.isPrivateDeploy(account)) {
				logger.error("isNotPrivateDeploy account:{}",account.id);
				throw new AppException("权限不足");
			}
			qywxService.refreshAccessToken();
			UserInfo userInfo=qywxService.getUserInfo(code);
			account.qywxUserId=userInfo.UserId;
			dao.updateSpecialFields(account, "qywxUserId");
			logger.info("bind success account:{} UserId:{}",account.id,userInfo.UserId);
		}
		
		@Override
		public void unbind(String token) {
			Account account=bizService.getExistedAccountByToken(token);
			account.qywxUserId=null;
			dao.updateSpecialFields(account, "qywxUserId");
			logger.info("unbind success account:{}",account.id);
		}
		
		@Override
		public boolean isBind(String token) {
			Account account=bizService.getExistedAccountByToken(token);
			return !StringUtil.isEmpty(account.qywxUserId);
		}
		
	}


}
