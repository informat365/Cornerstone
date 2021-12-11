package cornerstone.biz.srv;

import javax.naming.directory.Attributes;

/**
 * 
 * @author cs
 *
 */
public interface AuthService {

	/**
	 * 认证
	 * @param userName
	 * @param password
	 * @return
	 */
	public Attributes auth(String userName,String password);
}
