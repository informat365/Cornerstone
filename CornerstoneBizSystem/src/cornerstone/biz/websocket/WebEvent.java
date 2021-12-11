package cornerstone.biz.websocket;

/**
 * 
 * @author cs
 *
 */
public class WebEvent {
	//
	public static final int TYPE_创建任务=1;
	public static final int TYPE_编辑任务=2;
	public static final int TYPE_删除任务=3;
	public static final int TYPE_导入任务=4;
	//
	public static final int TYPE_Devops开始执行=10;
	public static final int TYPE_Devops执行结束=11;
	//
	public static final int TYPE_任务分类创建=21;
	public static final int TYPE_任务分类删除=22;
	public static final int TYPE_任务分类编辑=23;
	//
	public static final int TYPE_有新发表的讨论=31;
	//
	public int companyId;
	
	public int type;
	
	public Object data;
	
	public int optAccountId;
	//
	//
	public static WebEvent createWebEvent(int optAccountId,int companyId,int type,Integer objectType,
			Integer projectId,Integer taskId) {
		return createWebEvent(optAccountId, companyId, type, objectType, projectId, 
				taskId, null, null, null,null,null);
	}
	//
	public static WebEvent createWebEvent(int optAccountId,int companyId,int type,Integer objectType,
			Integer projectId,Integer taskId,Integer discussId,Integer discussMessageId,Integer categoryId,
			Integer pipelineId,Integer pipelineRunLogId) {
		WebEvent event=new WebEvent();
		event.optAccountId=optAccountId;
		event.companyId=companyId;
		event.type=type;
		WebEventData data=new WebEventData();
		data.objectType=objectType;
		data.projectId=projectId;
		data.taskId=taskId;
		data.discussId=discussId;
		data.discussMessageId=discussMessageId;
		data.categoryId=categoryId;
		data.pipelineId=pipelineId;
		data.pipelineRunLogId=pipelineRunLogId;
		event.data=data;
		return event;
	}
	//
	public static class WebEventData{
		public Integer taskId;
		public Integer objectType;
		public Integer projectId;
		public Integer discussId;
		public Integer discussMessageId;
		public Integer categoryId;
		public Integer pipelineId;
		public Integer pipelineRunLogId;
	}
}
