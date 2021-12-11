package cornerstone.biz.ssh;

/**
 * 
 * @author cs
 *
 */
public class ConnectionInfo {
	//
	public static interface Callback {
		void onReceiveMessage(String message);
	}
	//
	public String name;
	public String host;
	public int port;
	public String user;
	public String password;
	public String privateKey;
	public boolean enableInput;
	public String cmd;
	public Callback callback;
	public ChannelListener channelListener;

	//
	@Override
	public String toString() {
		return user + "@" + host + ":" + port + "/" + "input:" + enableInput + "/" + channelListener;
	}
}