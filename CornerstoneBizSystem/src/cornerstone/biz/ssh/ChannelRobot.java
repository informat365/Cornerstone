/**
 * 
 */
package cornerstone.biz.ssh;

/**
 * @author yama
 *
 */
public class ChannelRobot implements ChannelListener{
	@Override
	public void onOpen(SshChannel channel) {
		
	}
	//
	@Override
	public void onClose(SshChannel channel) {
		
	}
	//
	@Override
	public void onMessage(SshChannel channel, String message) {
		
	}
	//
	@Override
	public void onTicket(SshChannel channel, long ticket) {
		
	}
	//
	@Override
	public void onInput(SshChannel channel, String message) {
		
	}
	//
	@Override
	public boolean inputSendToServer() {
		return true;
	}
}
