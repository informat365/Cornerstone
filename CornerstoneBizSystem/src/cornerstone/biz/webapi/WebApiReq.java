package cornerstone.biz.webapi;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author cs
 *
 */
public class WebApiReq {
	
	public String method;
	
	public String ip;

	public Map<String,String> parameterMap;
	
	public String body;
	
	public WebApiReq() {
		parameterMap=new HashMap<>();
	}
}
