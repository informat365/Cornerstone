/**
 *
 */
package cornerstone.boss.controller;

import cornerstone.biz.CornerstoneBizSystem;
import cornerstone.biz.action.BossAction;
import jazmin.core.Jazmin;
import jazmin.driver.rpc.JazminRpcDriver;
import jazmin.server.web.mvc.Controller;
import jazmin.server.web.mvc.ProxyController;

/**
 * 
 * @author cs
 *
 */
@Controller(id = "boss")
public class BossController extends ProxyController {
	public BossController() {
		//
		JazminRpcDriver rpcServer = Jazmin.getDriver(JazminRpcDriver.class);
		registerProxyTarget(rpcServer.create(BossAction.class, CornerstoneBizSystem.class.getSimpleName()));
	}
}