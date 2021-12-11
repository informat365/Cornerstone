/**
 * 
 */
package cornerstone.biz.ssh;

/**
 * @author yama
 *
 */
public class SendCommandChannelRobot extends ChannelRobot{
	String cmd;
	public SendCommandChannelRobot(String cmd) {
		this.cmd=cmd;
	}
	@Override
	public void onOpen(SshChannel channel) {
		channel.sendMessageToServer(cmd+"\r\n");
	}
}
