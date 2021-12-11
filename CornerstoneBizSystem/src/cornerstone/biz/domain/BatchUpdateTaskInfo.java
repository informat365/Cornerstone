package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author cs
 *
 */
public class BatchUpdateTaskInfo extends BaseDomain {

	public Integer projectId;

	public Integer repositoryVersionId;

	public Integer repositoryId;

	public Integer iterationId;

	public Integer stageId;

	public Integer objectType;

	public String name;

	public String uuid;

	public String serialNo;

	public Integer ownerAccountId;

	public Integer createAccountId;

	public Integer updateAccountId;

	public Integer status;

	public Integer parentId;

	public Integer priority;

	public Date startDate;

	public Date finishTime;

	public Date endDate;

	public Integer expectWorkTime;

	public Integer workTime;

	public Boolean isDelete;

	public Integer releaseId;

	public Double workLoad;

	public Integer subSystemId;
	
	public Integer progress;

	public Integer taskDescriptionId;

	public Integer subTaskCount;

	public Integer finishSubTaskCount;

	public Map<String, Object> customFields;

	public List<Integer> categoryIdList;
	
    public String content;
    
    public List<Integer> ownerAccountIdList;//责任人列表(用于新增或编辑任务)
    
}
