package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class WorkflowInstanceNotify {
	public String workflowDefineName;
	public String beforeNodeName;//", instance.beforeNodeName);
	public String currNodeName;//", instance.currNodeName);
	public String content;//", instance.currNodeName);
	public String title;//", instance.title);
	public String uuid;//", instance.uuid);
	public String serialNo;//", instance.serialNo);
	public boolean enableNotifyWechat;//", define.enableNotifyWechat);
	public boolean enableNotifyEmail;//", define.enableNotifyEmail);
}
