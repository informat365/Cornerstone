package cornerstone.biz.websockify;

import java.net.InetSocketAddress;
import java.util.List;

import jazmin.core.Jazmin;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.console.ascii.TablePrinter;
import jazmin.server.console.builtin.ConsoleCommand;
import jazmin.server.websockify.WebsockifyChannel;
import jazmin.server.websockify.WebsockifyServer;

/**
 * 
 * @author yama 26 Dec, 2014
 */
public class WebsockifyCommand extends ConsoleCommand {
	private WebsockifyServer server;
	private Logger logger  = LoggerFactory.get(WebsockifyCommand.class);

	public WebsockifyCommand() {
		super(true);
		id = "websockify";
		desc = "websockify server ctrl command";
		addOption("i", false, "show server information.", this::showServerInfo);
		addOption("c", false, "show server channels.", this::showChannels);
		//
		server = Jazmin.getServer(WebsockifyServer.class);
	}
	//
	@Override
	public void run()  {
		if (server == null) {
			out.println("can not find WebsockifyServer.");
		return;
		}
		try {
			super.run();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	//
	private void showServerInfo(String args) {
		out.println(server.info());
	}
	//
	private void showChannels(String args){
		TablePrinter tp=TablePrinter.create(out)
				.length(10,30,30,10,10,15)
				.headers("ID",
						"INBOUND",
		    			"OUTBOUND",
		    			"SENTCNT",
		    			"RECECNT",
		    			"CREATETIME");
    	List<WebsockifyChannel>channels=server.getChannels();
    	for(WebsockifyChannel s:channels){
    		String in="";
    		if(s.inBoundChannel!=null){
    			InetSocketAddress is=(InetSocketAddress) s.inBoundChannel.remoteAddress();
    			in=is.getAddress().getHostAddress()+":"+is.getPort();
    		}
    		tp.print(
        			s.id,
        			in,
        			s.remoteAddress+":"+s.remotePort,
        			s.messageSentCount,
        			s.messageReceivedCount,
        			formatDate(s.createTime));
    	}
    }
}
