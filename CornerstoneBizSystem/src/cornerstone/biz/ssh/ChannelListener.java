/**
 * 
 */
package cornerstone.biz.ssh;

/**
 * @author yama
 *
 */
public interface ChannelListener {
	void onOpen(SshChannel channel);
	void onClose(SshChannel channel);
	void onMessage(SshChannel channel,String message);
	void onInput(SshChannel channel,String message);
	boolean inputSendToServer();
	void onTicket(SshChannel channel,long ticket);
}
