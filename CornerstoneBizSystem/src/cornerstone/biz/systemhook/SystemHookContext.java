package cornerstone.biz.systemhook;

import java.util.HashMap;
import java.util.Map;

import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.Task;

/**
 * 
 * @author cs
 *
 */
public class SystemHookContext {

	public Account account;
	
	public Task task;
	
	public Map<String,Object> env;
	//
	public SystemHookContext() {
		env=new HashMap<>();
	}
}
