package cornerstone.biz.domain;


import cornerstone.biz.ConstDefine;
import cornerstone.biz.taskjob.TaskJobs;
import cornerstone.biz.util.TripleDESUtil;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.mysqlproxy.Database;
import jazmin.server.mysqlproxy.ProxyRuleAuthProvider;
import jazmin.server.mysqlproxy.ProxySession;
import jazmin.util.MD5Util;

/**
 * 
 * @author cs
 *
 */
public class ProxyRuleAuthProviderImpl implements ProxyRuleAuthProvider{
	//
	private static Logger logger=LoggerFactory.get(ProxyRuleAuthProviderImpl.class);
	//
	DesignerMysqlProxyInstance instance;
	
	/**
	 * 
	 * @param e
	 * @param rule
	 */
	public ProxyRuleAuthProviderImpl(DesignerMysqlProxyInstance instance) {
		this.instance=instance;
	}

	@Override
	public Database auth(ProxySession session) {
		try {
			logger.info("auth session:{}",session);
			if(session.user==null) {
				logger.warn("session.user==null");
				return null;
			}
			Account account=TaskJobs.taskJobAction.getAccountByUserName(session.user);
			if(account==null||account.status==Account.STATUS_无效) {
				logger.warn("account ==null 或者无效 {}",session.user);
				return null;
			}
			if(account.encryptPassword==null&&account.accessToken==null) {
				logger.warn("account.encryptPassword==null&&account.accessToken==null {}",session.user);
				return null;
			}
			checkPassword(session, account);
			TaskJobs.taskJobAction.checkAccountPermission(account.id,instance);
			return new Database(instance.dbUser,TripleDESUtil.decrypt(instance.dbPassword,ConstDefine.GLOBAL_KEY));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	//
	private void checkPassword(ProxySession session,Account account) {
		if(account.encryptPassword!=null) {
			String password=TripleDESUtil.decrypt(account.encryptPassword, ConstDefine.GLOBAL_KEY);
			byte[] shaPassword=MD5Util.encodeSHA1Bytes(password.getBytes());
			if(session.comparePassword(shaPassword)){
				return;
			}
		}
		if(account.accessToken!=null) {
			byte[] shaPassword=MD5Util.encodeSHA1Bytes(account.accessToken.getBytes());
			if(session.comparePassword(shaPassword)){
				return;
			}
		}
		logger.error("accountUserName:{} encryptPassword:{} accessToken:{}",
				account.userName,account.encryptPassword,account.accessToken);
		throw new AppException("password error");
	}
}
