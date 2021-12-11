package cornerstone.web.controller;

import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.ResourceView;

/**
 * 
 * @author cs
 *
 */
public abstract class BaseController {
   
	/**
	 * 
	 * @param ctx
	 * @param errorMsg
	 */
	public void error(Context ctx,String errorMsg) {
		ctx.put("errorMsg", errorMsg);
		ctx.view(new ResourceView("/error.jsp"));
	}
}
