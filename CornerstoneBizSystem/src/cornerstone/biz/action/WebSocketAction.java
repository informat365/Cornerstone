package cornerstone.biz.action;

import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.srv.BizService;
import jazmin.core.app.AutoWired;
import jazmin.server.rpc.RpcService;

/**
 * WebSocketAction
 *
 *
 */
@ApiDefine(value = "WebSocket接口")
public interface WebSocketAction {

	/***/
	Account auth(String token);
	//
	@RpcService
	class WebSocketActionImpl implements WebSocketAction {
		//
		@AutoWired
		BizDAO dao;
		@AutoWired
		BizService bizService;
		//
		@Override
		public Account auth(String token) {
			Account account=bizService.getExistedAccountByToken(token);
			return account;
		}

		
	}

}
