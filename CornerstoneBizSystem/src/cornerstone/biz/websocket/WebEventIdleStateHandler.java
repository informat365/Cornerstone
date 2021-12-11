package cornerstone.biz.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateHandler;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * 
 * @author cs
 *
 */
public class WebEventIdleStateHandler extends IdleStateHandler{
	//
	private static Logger logger=LoggerFactory.get(WebEventIdleStateHandler.class);
	//
	public WebEventIdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
		super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
	}
	//
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			 {
		if(logger.isDebugEnabled()){
			logger.debug("WebEventIdleStateHandler userEventTriggered:"+ctx.channel()+"/"+evt);				
		}
	}

}
