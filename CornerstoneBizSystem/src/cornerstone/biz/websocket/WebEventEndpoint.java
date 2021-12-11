/**
 * 
 */
package cornerstone.biz.websocket;

import cornerstone.biz.ssh.PeerEndpoint;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * @author yama
 *
 */
public class WebEventEndpoint implements PeerEndpoint{
	private static Logger logger=LoggerFactory.get(WebEventEndpoint.class);
	//
	Channel channel;
	public WebEventEndpoint(Channel channel) {
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
	//
	/**
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}
	/**
	 * @param channel the channel to set
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
}
