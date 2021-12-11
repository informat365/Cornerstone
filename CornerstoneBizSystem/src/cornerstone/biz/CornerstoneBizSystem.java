package cornerstone.biz;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import cornerstone.biz.action.BizAction;
import cornerstone.biz.action.BizAction.BizActionImpl;
import cornerstone.biz.action.BossAction;
import cornerstone.biz.action.BossAction.BossActionImpl;
import cornerstone.biz.action.DataTableAction;
import cornerstone.biz.action.DataTableAction.DataTableActionImpl;
import cornerstone.biz.action.DesignerAction;
import cornerstone.biz.action.DesignerAction.DesignerActionImpl;
import cornerstone.biz.action.DingtalkAction.DingtalkActionImpl;
import cornerstone.biz.action.LarkAction;
import cornerstone.biz.action.LarkAction.LarkActionImpl;
import cornerstone.biz.action.NoteAction;
import cornerstone.biz.action.NoteAction.NoteActionImpl;
import cornerstone.biz.action.PipelineAction;
import cornerstone.biz.action.PipelineAction.PipelineActionImpl;
import cornerstone.biz.action.QywxAction.QywxActionImpl;
import cornerstone.biz.action.SshAction;
import cornerstone.biz.action.SshAction.SshActionImpl;
import cornerstone.biz.action.SurveysAction;
import cornerstone.biz.action.SurveysAction.SurveysActionImpl;
import cornerstone.biz.action.SystemHookAction;
import cornerstone.biz.action.SystemHookAction.SystemHookActionImpl;
import cornerstone.biz.action.TaskAlterationAction;
import cornerstone.biz.action.TaskAlterationAction.TaskAlterationActionImpl;
import cornerstone.biz.action.TaskJobAction;
import cornerstone.biz.action.TaskJobAction.TaskJobActionImpl;
import cornerstone.biz.action.WebApiAction;
import cornerstone.biz.action.WebApiAction.WebApiActionImpl;
import cornerstone.biz.action.WebSocketAction;
import cornerstone.biz.action.WebSocketAction.WebSocketActionImpl;
import cornerstone.biz.action.WeixinAction.WeixinActionImpl;
import cornerstone.biz.action.WorkflowAction;
import cornerstone.biz.action.WorkflowAction.WorkflowActionImpl;
import cornerstone.biz.debug.DebugWebServer;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.query.BizQuery;
import cornerstone.biz.lucene.IKAnalyzer4Lucene7;
import cornerstone.biz.srv.LarkService;
import cornerstone.biz.ssh.WebSshServer;
import cornerstone.biz.taskjob.BizTaskJobs;
import cornerstone.biz.taskjob.TaskJobs;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.websocket.WebEventServer;
import cornerstone.biz.websockify.WebsockifyServer;
import jazmin.core.Jazmin;
import jazmin.core.app.Application;
import jazmin.core.app.AutoWired;
import jazmin.driver.http.HttpClientDriver;
import jazmin.driver.jdbc.DruidConnectionDriver;
import jazmin.driver.jdbc.smartjdbc.Config;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.driver.jdbc.smartjdbc.provider.SelectProvider;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.mysqlproxy.MySQLProxyServer;
import jazmin.server.rpc.RpcServer;

/**
 * @author cs
 */
public class CornerstoneBizSystem extends Application {
    //
    private static Logger logger = LoggerFactory.get(CornerstoneBizSystem.class);
    //
    @AutoWired
    BizActionImpl bizAction;
    @AutoWired
    TaskJobActionImpl taskJobAction;
    @AutoWired
    SshActionImpl sshAction;
    @AutoWired
    PipelineActionImpl pipelineAction;
    @AutoWired
    DataTableActionImpl dataTableAction;
    @AutoWired
    SystemHookActionImpl systemHookAction;
    @AutoWired
    WorkflowActionImpl workflowAction;
    @AutoWired
    NoteActionImpl noteAction;
    @AutoWired
    WeixinActionImpl weixinAction;
    @AutoWired
    DesignerActionImpl designerAction;
    @AutoWired
    WebApiActionImpl webApiAction;
    @AutoWired
    BossActionImpl bossAction;
    @AutoWired
    LarkActionImpl larkAction;
    @AutoWired
    WebSocketActionImpl webSocketAction;
    @AutoWired
    SurveysActionImpl surveysAction;
    @AutoWired
    QywxActionImpl qywxAction;
    @AutoWired
    DingtalkActionImpl dingtalkAction;
    @AutoWired
    TaskAlterationActionImpl taskAlterationAction;
    @AutoWired
    LarkService larkService;
    //
    @AutoWired
    public TaskJobs taskJobs;
    @AutoWired
    public BizTaskJobs bizTaskJobs;
    /***/
    public static WebSshServer webSshServer;
    public static WebsockifyServer websockifyServer;
    public static WebEventServer webEventServer;
    //
    DebugWebServer debugWebServer;
    /**
     * testCase模式
     */
    boolean isTestCaseMode;
    public static boolean isDebug;

    //
    public static CornerstoneBizSystem get() {
        return (CornerstoneBizSystem) Jazmin.getApplication();
    }

    //
    @Override
    public void init() {
        try {
            super.init();
        } catch (Exception e) {
            logger.error("init ERROR ", e);
        }
        setAutoRegisterWired(true);
        Config.setDefaultOrderBy(this::addOrderBy);
        Query.defaultOrderType = BizQuery.ORDER_TYPE_CREATE_TIME_DESC;
        Query.defaultPageSize = 30;
        //
        if (!isTestCaseMode) {
            MySQLProxyServer server = new MySQLProxyServer();
            Jazmin.addServer(server);
        }
    }

    @Override
    public void start() {
        try {
            super.start();
            TaskJobs.taskJobAction = Jazmin.dispatcher.create(TaskJobAction.class, createWired(TaskJobActionImpl.class));
            GlobalConfig.setupConfig(TaskJobs.taskJobAction.getConfigs());
            BizData.bizAction = Jazmin.dispatcher.create(BizAction.class, Jazmin.getApplication().createWired(BizActionImpl.class));
            BizData.sshAction = Jazmin.dispatcher.create(SshAction.class, createWired(SshActionImpl.class));
            BizData.pipelineAction = Jazmin.dispatcher.create(PipelineAction.class, createWired(PipelineActionImpl.class));
            BizData.webApiAction = Jazmin.dispatcher.create(WebApiAction.class, createWired(WebApiActionImpl.class));
            BizData.webSocketAction = Jazmin.dispatcher.create(WebSocketAction.class, createWired(WebSocketActionImpl.class));
            if (debugWebServer != null) {
                BizAction bizAction = Jazmin.dispatcher.create(BizAction.class, createWired(BizActionImpl.class));
                BossAction bossAction = Jazmin.dispatcher.create(BossAction.class, createWired(BossActionImpl.class));
                DesignerAction designerAction = Jazmin.dispatcher.create(DesignerAction.class, createWired(DesignerActionImpl.class));
                DataTableAction dataTableAction = Jazmin.dispatcher.create(DataTableAction.class, createWired(DataTableActionImpl.class));
                SystemHookAction systemHookAction = Jazmin.dispatcher.create(SystemHookAction.class, createWired(SystemHookActionImpl.class));
                WorkflowAction workflowAction = Jazmin.dispatcher.create(WorkflowAction.class, createWired(WorkflowActionImpl.class));
                NoteAction noteAction = Jazmin.dispatcher.create(NoteAction.class, createWired(NoteActionImpl.class));
                WebApiAction webApiAction = Jazmin.dispatcher.create(WebApiAction.class, createWired(WebApiActionImpl.class));
                LarkAction larkAction = Jazmin.dispatcher.create(LarkAction.class, createWired(LarkActionImpl.class));
                SurveysAction surveysAction = Jazmin.dispatcher.create(SurveysAction.class, createWired(SurveysActionImpl.class));
			TaskAlterationAction taskAlterationAction = Jazmin.dispatcher.create(TaskAlterationAction.class,createWired(TaskAlterationActionImpl.class));
                debugWebServer.start(this, Arrays.asList(bizAction, bossAction, designerAction,
                        dataTableAction, systemHookAction, workflowAction, webApiAction, noteAction, larkAction, surveysAction,taskAlterationAction));
            }
            if (!isTestCaseMode) {
                int webSshPort = GlobalConfig.getIntValue("websocket.port");
                if (webSshPort > 0) {
                    webSshServer = new WebSshServer();
                    webSshServer.setPort(webSshPort);
                    webSshServer.init();
                    webSshServer.start();
                    logger.info("webSshServer start {}", webSshPort);
                }
                TaskJobs.taskJobAction.initLdapService();
                //
                int websockifyPort = GlobalConfig.getIntValue("websockify.port");
                if (websockifyPort > 0) {
                    websockifyServer = new WebsockifyServer();
                    websockifyServer.setPort(websockifyPort);
                    websockifyServer.init();
                    websockifyServer.start();
                    logger.info("websockifyServer start {}", websockifyPort);
                }
                //
                TaskJobs.taskJobAction.initMysqlProxy();
                //
                int webEventPort = GlobalConfig.getIntValue("webevent.port");
                if (webEventPort > 0) {
                    webEventServer = new WebEventServer(webEventPort);
                    webEventServer.init();
                    webEventServer.start();
                    logger.info(webEventServer.getClass().getSimpleName() + " start port:{}", webEventPort);
                }
            }
            FileServiceManager.get().init();
            IKAnalyzer4Lucene7.initDictCallback = this::initIkAnalyzerDicts;
            //lark
//            if (!StringUtil.isEmptyWithTrim(GlobalConfig.larkAppId) && GlobalConfig.larkAppId.length() > 6) {
//                larkService.resendAppTicket();
//            }
        } catch (Exception e) {
            logger.error("start ERROR ", e);
        }
    }

    @Override
    public void stop()  {
        try {
            super.stop();
        } catch (Exception e) {
            logger.error("stop ERROR",e);
        }
        if (webEventServer != null) {
            try {
                webEventServer.stop();
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            logger.info("webEventServer stop");
        }
    }

    public Boolean initIkAnalyzerDicts() {//初始化IK字典库
        try {
            Jazmin.execute(() -> {//一定要启动一个线程 否则call begin transcation first
                TaskJobs.taskJobAction.initIkAnalyzerDicts();
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return true;
    }

    //Query默认排序
    protected void addOrderBy(SelectProvider sp, Query query) {
        if (query == null) {
            return;
        }
        if (query.orderType != null) {
            if (query.orderType == BizQuery.ORDER_TYPE_CREATE_TIME_ASC) {
                sp.orderBy(" create_time asc");
            }
            if (query.orderType == BizQuery.ORDER_TYPE_CREATE_TIME_DESC) {
                sp.orderBy(" create_time desc");
            }
            if (query.orderType == BizQuery.ORDER_TYPE_UPDATE_TIME_ASC) {
                sp.orderBy(" update_time asc");
            }
            if (query.orderType == BizQuery.ORDER_TYPE_UPDATE_TIME_DESC) {
                sp.orderBy(" update_time desc");
            }
            if (query.orderType == BizQuery.ORDER_TYPE_ID_ASC) {
                sp.orderBy(" id asc");
            }
            if (query.orderType == BizQuery.ORDER_TYPE_ID_DESC) {
                sp.orderBy(" id desc");
            }
        }
    }

    // --------------------------------------------------------------------------
    //
    public static void start(CornerstoneBizSystem app, int port, int webPort, boolean isTestCaseMode) throws SQLException, IOException {
        LoggerFactory.setLevel("ALL");
        LoggerFactory.setFile("/tmp/" + CornerstoneBizSystem.class.getSimpleName() + ".log", true);
        isDebug = true;//本地测试
        app.isTestCaseMode = isTestCaseMode;
//		boolean useMysql8=false;
        String dbUser = null;
        String dbPassword = null;
        String dbUrl = null;
        String dbDriverClass = null;
        DruidConnectionDriver driver = new DruidConnectionDriver();
        try (InputStream input = new FileInputStream("local.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            dbUser = BizUtil.null2String(prop.getProperty("dbUser"));
            dbPassword = BizUtil.null2String(prop.getProperty("dbPassword"));
            dbUrl = BizUtil.null2String(prop.getProperty("dbUrl"));
            dbDriverClass = BizUtil.null2String(prop.getProperty("dbDriverClass"));
        }
        driver.setDriverClass(dbDriverClass);
        driver.setUrl(dbUrl);
        driver.setUser(dbUser);
        driver.setPassword(dbPassword);
        Jazmin.addDriver(driver);
        //
        HttpClientDriver httpClientDriver = new HttpClientDriver();
        Jazmin.addDriver(httpClientDriver);
        //
        if (webPort > 0) {//启动debugWebServer
            app.debugWebServer = new DebugWebServer();
            app.debugWebServer.init(webPort);
        }
        RpcServer rpcServer = new RpcServer();
        rpcServer.setPort(port);
        Jazmin.addServer(rpcServer);
        Jazmin.loadApplication(app);
        Jazmin.start();
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        start(new CornerstoneBizSystem(), 9001, 8888,false);
    }

}
