/**
 * 
 */
package cornerstone.web.controller;

import jazmin.server.web.mvc.Context;

/**
 * @author yama
 * 11 Dec, 2015
 */
public interface WeixinControllerCallback {
	void afterWeixinLogin(Context ctx);
}
