package cornerstone.biz.ssh;
/**
 * 
 * @author yama
 *
 */
public interface PeerEndpoint {
	public void close();
	public void write(String msg);
}
