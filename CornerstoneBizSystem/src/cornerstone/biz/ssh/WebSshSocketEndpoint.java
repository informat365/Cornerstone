/**
 * 
 */
package cornerstone.biz.ssh;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * @author yama
 *
 */
public class WebSshSocketEndpoint implements PeerEndpoint{
	private static Logger logger=LoggerFactory.get(WebSshSocketEndpoint.class);
	//
	Channel channel;
	public WebSshSocketEndpoint(Channel channel) {
		this.channel=channel;
	}
	//
	@Override
	public void close() {
		try {
			channel.close().sync();
		} catch (InterruptedException e) {
			logger.catching(e);
		}
	}

	@Override
	public void write(String msg) {
		TextWebSocketFrame frame=new TextWebSocketFrame(msg);
		channel.writeAndFlush(frame);
	}
	//
	@Override
	public String toString() {
		return "WS:"+channel;
	}
}
