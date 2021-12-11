package cornerstone.biz.domain;

import cornerstone.biz.domain.Task.TaskInfo;

/**
 * 
 * @author cs
 *
 */
public class TaskSimpleInfo {
	public int id;
	public int objectType;
	public String objectTypeName;
	public String uuid;
	public String serialNo;
	public String name;
	public String statusName;
	public int projectId;
	public String projectName;
	public boolean isDelete;
    //
    public static TaskSimpleInfo createTaskSimpleinfo(TaskInfo bean) {
    		TaskSimpleInfo info=new TaskSimpleInfo();
    		info.id=bean.id;
    		info.uuid=bean.uuid;
    		info.name=bean.name;
    		info.serialNo=bean.serialNo;
    		info.statusName=bean.statusName;
    		info.projectId=bean.projectId;
    		info.projectName=bean.projectName;
    		info.objectType=bean.objectType;
    		info.objectTypeName=bean.objectTypeName;
    		info.isDelete=bean.isDelete;
    		return info;
    }
}
