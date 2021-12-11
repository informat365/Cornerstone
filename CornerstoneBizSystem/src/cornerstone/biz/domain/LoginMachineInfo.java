package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class LoginMachineInfo {

	public String machineName;
	
	public String token;
	
	public List<AccountSimpleInfo> accountList;
	
	public String websocketHost;
	
	public int websocketPort;
	
	public String createAccountName;

	public Date createTime;
}
