package cornerstone.biz.ssh;

import java.util.List;

import com.mysql.cj.log.LogFactory;
import jazmin.core.Jazmin;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.console.ascii.TablePrinter;
import jazmin.server.console.builtin.ConsoleCommand;

/**
 * 
 * @author yama 26 Dec, 2014
 */
public class WebSshServerCommand extends ConsoleCommand {
	private WebSshServer server;
	private Logger logger = LoggerFactory.get(WebSshServerCommand.class);

	public WebSshServerCommand() {
		super(true);
		id = "webssh";
		desc = "webssh server ctrl command";
		addOption("i", false, "show server information.", this::showServerInfo);
		addOption("c", false, "show server channels.", this::showChannels);
		//
		server = Jazmin.getServer(WebSshServer.class);
	}
	//
	@Override
	public void run()  {
		if (server == null) {
			out.println("can not find WebSshServer.");

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
				.length(15,40,10,15,10,70,20)
				.headers("ID",
						"SSHINFO",
		    			"SENTCNT",
		    			"RECECNT",
		    			"CREATETIME",
		    			"CONNECTION",
		    			"ENDPOINT");
    	List<SshChannel>channels=server.getChannels();
    	for(SshChannel s:channels){
    		tp.print(
        			s.getId(),
        			s.connectionInfo.user+"@"+s.connectionInfo.host+":"+s.connectionInfo.port,
        			s.messageSentCount,
        			s.messageReceivedCount,
        			formatDate(s.createTime),
        			s.connectionInfo,
        			s.endpoint);
    	}
    }
}
