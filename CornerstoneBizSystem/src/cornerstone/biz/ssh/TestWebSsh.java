package cornerstone.biz.ssh;

/***
 * 
 * @author cs
 *
 */
public class TestWebSsh {
	//
	private WebSshServer server;
	//
	public void start() throws InterruptedException {
		server=new WebSshServer();
		server.setPort(9201);
		server.init();
		server.start();
	}
}
