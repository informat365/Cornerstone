/**
 * 
 */
package cornerstone.web;

import javax.servlet.ServletException;

import jazmin.core.Jazmin;
import jazmin.core.Lifecycle;
import jazmin.core.LifecycleAdapter;
import jazmin.server.web.WebServer;

/**
 * @author yama
 * 30 Aug, 2015
 */
@SuppressWarnings("serial")
public class DispatchServlet extends jazmin.server.web.mvc.DispatchServlet{
	@Override
	public void init() throws ServletException {
		super.init();
		WebServer ws=Jazmin.getServer(WebServer.class);
		ws.setLifecycleListener(new LifecycleAdapter() {
			@Override
			public void afterStart(Lifecycle server) throws Exception {
				Jazmin.loadAndStartApplication(new CornerstoneWebSystem());
			}
		});
	}
}
