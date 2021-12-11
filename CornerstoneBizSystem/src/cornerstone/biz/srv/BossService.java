package cornerstone.biz.srv;

import cornerstone.biz.dao.BossDAO;
import cornerstone.biz.domain.User;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;

/**
 * @author cs
 */
public class BossService{
	//
	@AutoWired
	BossDAO dao;
	//
	public User getExistedUserByUserName(String userName) {
		User user=dao.getUserByUserName(userName);
		if(user==null) {
			throw new AppException("用户不存在"+userName);
		}
		return user;
	}
}
