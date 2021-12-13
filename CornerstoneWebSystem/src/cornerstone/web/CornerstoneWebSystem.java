/**
 *
 */
package cornerstone.web;

import cornerstone.biz.BizData;
import cornerstone.biz.FileServiceManager;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.web.controller.*;
import cornerstone.web.taskjob.WebTaskJobs;
import jazmin.core.Jazmin;
import jazmin.core.app.Application;
import jazmin.core.app.AutoWired;
import jazmin.driver.rpc.JazminRpcDriver;
import jazmin.server.web.WebServer;

/**
 * @author yama 8 Sep, 2015
 */
public class CornerstoneWebSystem extends Application {

    public static final String CLUSTER_BIZ = "CornerstoneBizSystem";

    //
    @AutoWired
    APIController apiController;
    @AutoWired
    WebAPIController webAPIController;
    @AutoWired
    FileController fileController;
    @AutoWired
    MainController mainController;
    @AutoWired
    WeixinController weixinController;
    @AutoWired
    OpenAPIController openapiController;
    @AutoWired
    LarkController larkController;
    @AutoWired
    QywxController qywxController;
    @AutoWired
    OfficeController officeController;
    @AutoWired
    PageOfficeController pageOfficeController;
    @AutoWired
    DingtalkController dingtalkController;
    @AutoWired
    WebTaskJobs webTaskJobs;
    //
    @Override
    public void init() throws Exception {
        super.init();
        BizData.initBizActions(CLUSTER_BIZ);
        setAutoRegisterWired(true);
        GlobalConfig.setupConfig(BizData.taskJobAction.getConfigs());
        FileServiceManager.get().init();
        //
        JazminRpcDriver rpcDriver=Jazmin.getDriver(JazminRpcDriver.class);
		rpcDriver.setRequestTimeout(2*60*1000);//2分钟
    }

    //
    public static void main(String[] args) {
        //
        Jazmin.setServerName(CornerstoneWebSystem.class.getSimpleName() + System.getProperty("user.name"));
        Jazmin.environment.put("debug", "true");
        JazminRpcDriver rpcDriver = new JazminRpcDriver();
        rpcDriver.addRemoteServer(CLUSTER_BIZ, "app", "127.0.0.1", 9001);
        // rpcDriver.addRemoteServer(BizData.CLUSTER_BIZ, "app", "10.0.0.5", 9001);
        Jazmin.addDriver(rpcDriver);

        //
        WebServer ws = new WebServer();
        ws.addResource("/", "release/" + CornerstoneWebSystem.class.getSimpleName());
        ws.setPort(8888);
        Jazmin.addServer(ws);
        Jazmin.start();
    }

}
