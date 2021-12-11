package cornerstone.biz.srv;

import java.util.List;

import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.DesignerMysqlProxyInstance;
import cornerstone.biz.domain.DesignerMysqlProxyInstance.DesignerMysqlProxyInstanceInfo;
import cornerstone.biz.domain.ProxyRuleAuthProviderImpl;
import jazmin.core.Jazmin;
import jazmin.core.app.AutoWired;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.mysqlproxy.MySQLProxyServer;
import jazmin.server.mysqlproxy.ProxyRule;
import jazmin.util.DumpUtil;

/**
 * MYSQL代理服务
 * @author cs
 *
 */
public class MysqlProxyService{
	//
	private static Logger logger=LoggerFactory.get(MysqlProxyService.class);
	//
	@AutoWired
	BizDAO dao;
	
	@AutoWired
	BizService bizService;
	//
	public void init() {
		List<DesignerMysqlProxyInstanceInfo> infos=dao.getAll(DesignerMysqlProxyInstanceInfo.class);
		for (DesignerMysqlProxyInstanceInfo e : infos) {
			addRule(e);
		}
	}
	//
	public void addRule(DesignerMysqlProxyInstance e) {
		try {
			MySQLProxyServer server=Jazmin.getServer(MySQLProxyServer.class);
			ProxyRule rule=new ProxyRule();
			rule.localPort=e.proxyPort;
			rule.remoteHost=e.host;
			rule.remotePort=e.port;
			ProxyRuleAuthProviderImpl impl=new ProxyRuleAuthProviderImpl(e);
			rule.authProvider=impl;
			server.addRule(rule);
			if(logger.isInfoEnabled()) {
				logger.info("MysqlProxyService addRule {}",DumpUtil.dump(e));
			}
		} catch (Exception e2) {
			logger.error(e2.getMessage(),e2);
		}
		
	}
	//
	public void removeRule(int port) {
		try {
			MySQLProxyServer server=Jazmin.getServer(MySQLProxyServer.class);
			server.removeRule(port);
			if(logger.isInfoEnabled()) {
				logger.info("removeRule {}",port);
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(),e);
		}
		
	}
}
