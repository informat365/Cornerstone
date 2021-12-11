/**
 *
 */
package cornerstone.web.controller;

import cornerstone.biz.CornerstoneBizSystem;
import cornerstone.biz.action.*;
import jazmin.core.Jazmin;
import jazmin.driver.rpc.JazminRpcDriver;
import jazmin.server.web.mvc.Controller;
import jazmin.server.web.mvc.ProxyController;

/**
 * 
 * @author cs
 *
 */
@Controller(id = "api")
public class APIController extends ProxyController {
	public APIController() {
		//
		JazminRpcDriver rpcServer = Jazmin.getDriver(JazminRpcDriver.class);
		registerProxyTarget(rpcServer.create(BizAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(DesignerAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(DataTableAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(SystemHookAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(WorkflowAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(WebApiAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(NoteAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(LarkAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(SurveysAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(QywxAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(DingtalkAction.class, CornerstoneBizSystem.class.getSimpleName()));
		registerProxyTarget(rpcServer.create(TaskAlterationAction.class, CornerstoneBizSystem.class.getSimpleName()));

	}
}