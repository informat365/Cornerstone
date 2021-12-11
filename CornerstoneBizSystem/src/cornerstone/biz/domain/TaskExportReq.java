package cornerstone.biz.domain;

import java.util.List;

import cornerstone.biz.domain.Task.TaskQuery;

/**
 * 
 * @author cs
 *
 */
public class TaskExportReq {

	public String token;
	
	public TaskQuery query;
	
	public List<Integer> fields;
}
