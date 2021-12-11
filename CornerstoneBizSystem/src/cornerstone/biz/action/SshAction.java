package cornerstone.biz.action;

import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.CmdbRobot;
import cornerstone.biz.domain.ConnectionDetailInfo;
import cornerstone.biz.srv.BizService;
import jazmin.core.app.AutoWired;
import jazmin.server.rpc.RpcService;

/**
 * SshAction
 *
 *
 */
@ApiDefine(value = "SSH 接口")
public interface SshAction {

	@ApiDefine(value = "webssh登录",params = {"登录Token"},resp = "WebSsh信息")
	ConnectionDetailInfo getConnectionDetailInfo(String token);

	@ApiDefine(value = "获取主机信息",params = {"主机名称"},resp = "主机信息")
	CmdbRobot getCmdbRobot(String robotName);
	//
	@RpcService
	class SshActionImpl implements SshAction {
		//
		@AutoWired
		BizDAO dao;
		@AutoWired
		BizService bizService;

		@Override
		public ConnectionDetailInfo getConnectionDetailInfo(String token) {
			return bizService.getConnectionDetailInfo0(token);
		}
		

		@Override
		public CmdbRobot getCmdbRobot(String robotName) {
			return dao.getCmdbRobot(robotName);
		}
	}

}
