package cornerstone.biz;


import cornerstone.biz.action.*;
import jazmin.core.Jazmin;
import jazmin.driver.rpc.JazminRpcDriver;

/**
 * @author cs
 */
public class BizData {
	//
	public static BizAction bizAction;
	public static TaskJobAction taskJobAction;
	public static SshAction sshAction;
	public static PipelineAction pipelineAction;
	public static WeixinAction weixinAction;
	public static DesignerAction designerAction;
	public static WebApiAction webApiAction;
	public static DataTableAction dataTableAction;
	public static SystemHookAction systemHookAction;
	public static WorkflowAction workflowAction;
	public static NoteAction noteAction;
	public static LarkAction larkAction;
	public static WebSocketAction webSocketAction;
	public static SurveysAction surveysAction;
	public static QywxAction qywxAction;
	public static DingtalkAction dingtalkAction;
	public static TaskAlterationAction taskAlterationAction;
    //
    public static void initBizActions(String cluster) {
        JazminRpcDriver rpcServer = Jazmin.getDriver(JazminRpcDriver.class);
        bizAction = rpcServer.create(BizAction.class, cluster);
        taskJobAction = rpcServer.create(TaskJobAction.class, cluster);
        sshAction = rpcServer.create(SshAction.class, cluster);
        pipelineAction= rpcServer.create(PipelineAction.class, cluster);
        weixinAction= rpcServer.create(WeixinAction.class, cluster);
        designerAction= rpcServer.create(DesignerAction.class, cluster);
        webApiAction= rpcServer.create(WebApiAction.class, cluster);
        dataTableAction= rpcServer.create(DataTableAction.class, cluster);
        systemHookAction= rpcServer.create(SystemHookAction.class, cluster);
        workflowAction= rpcServer.create(WorkflowAction.class, cluster);
        noteAction= rpcServer.create(NoteAction.class, cluster);
        larkAction= rpcServer.create(LarkAction.class, cluster);
        webSocketAction= rpcServer.create(WebSocketAction.class, cluster);
        surveysAction= rpcServer.create(SurveysAction.class, cluster);
        qywxAction= rpcServer.create(QywxAction.class, cluster);
        dingtalkAction= rpcServer.create(DingtalkAction.class, cluster);
        taskAlterationAction = rpcServer.create(TaskAlterationAction.class, cluster);
    }
}
