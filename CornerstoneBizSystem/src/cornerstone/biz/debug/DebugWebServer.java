package cornerstone.biz.debug;

import java.util.List;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.webapp.WebAppContext;

import cornerstone.biz.BizData;
import cornerstone.biz.action.BizAction;
import cornerstone.biz.action.BizAction.BizActionImpl;
import cornerstone.web.controller.FileController;
import cornerstone.web.controller.MainController;
import cornerstone.web.controller.WebAPIController;
import jazmin.core.Jazmin;
import jazmin.core.app.AppException;
import jazmin.core.app.Application;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.WebServer;
import jazmin.server.web.mvc.Controller;
import jazmin.server.web.mvc.DispatchServlet;
import jazmin.server.web.mvc.ProxyController;

/**
 * 
 * @author cs
 *
 */
public class DebugWebServer {
	//
	private static Logger logger=LoggerFactory.get(WebServer.class);
	//
	public DebugWebServer() {
	}
	//
	public void init(int port) {
		WebServer ws = new WebServer();
        ws.setPort(port);
        ws.addApplication("/", ".");
		WebAppContext context=ws.getWebAppContext();
		ServletHolder defaultServlet=new ServletHolder(DispatchServlet.class);
		context.addServlet(defaultServlet, "/p/*");
		FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
        filterHolder.setInitParameter("allowedOrigins", "*");
        filterHolder.setInitParameter("allowedMethods", "GET, POST");
        ws.getWebAppContext().addFilter(filterHolder, "/*", null);
		defaultServlet.getRegistration().setMultipartConfig(new MultipartConfigElement("", 52428800, 52428800, 0));
        Jazmin.addServer(ws);
	}
	//
	public void start(Application applicaton,List<Object> actions) {
		try {
			BizData.bizAction = Jazmin.dispatcher.create(BizAction.class,applicaton.createWired(BizActionImpl.class));
			DispatchServlet.dispatcher.registerController(new APIController(actions));
			DispatchServlet.dispatcher.registerController(new MainController());
			DispatchServlet.dispatcher.registerController(new FileController());
			DispatchServlet.dispatcher.registerController(new WebAPIController());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
	}
	//
	@Controller(id = "api")
	public  class APIController extends ProxyController {
		public APIController(List<Object> actions) {
			try {
				for (Object action : actions) {
					registerProxyTarget(action);
				}
				registerProxyTarget(BizData.bizAction);
			} catch (Exception e) {
				logger.error("APIController ERR",e);
			}
		}
	}
}
