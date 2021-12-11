package cornerstone.biz.websocket;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;

import cornerstone.biz.BizData;
import cornerstone.biz.domain.Account;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * Handles handshakes and messages
 */
public class WebEventSocketHandler extends SimpleChannelInboundHandler<Object> {
	//
	private static Logger logger = LoggerFactory
			.get(WebEventSocketHandler.class);
	//
	public static final AttributeKey<WebEventChannel> SESSION_KEY=
			AttributeKey.valueOf("s");
	//
	private static final int MAX_WEBSOCKET_FRAME_SIZE=10240;
	//
	private WebSocketServerHandshaker handshaker;
	private WebEventServer server;
	private boolean isWss;
	//
	//
	public WebEventSocketHandler(WebEventServer server,boolean isWss) {
		this.server=server;
		this.isWss=isWss;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object msg) throws IOException {
		if (msg instanceof FullHttpRequest) {
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	private void handleHttpRequest(
			ChannelHandlerContext ctx,
			FullHttpRequest req) {
		// Handle a bad request.
		if (!req.decoderResult().isSuccess()) {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1,
					BAD_REQUEST));
			return;
		}
		// Allow only GET methods.
		if (req.method() != GET) {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1,
					FORBIDDEN));
			return;
		}
		if ("/favicon.ico".equals(req.uri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, 
            		HttpResponseStatus.NOT_FOUND);
            sendHttpResponse(ctx, req, res);
            return;
        }
		String uri=req.uri();
		logger.debug("handleHttpRequest uri:{}",uri);
		if(uri==null||!uri.startsWith("/event/")) {
			logger.warn("uri error"+uri);
			return;
		}
		String token=uri.substring(7);
		if(token==null){
			logger.error("auth failed with token {}",token);
			ctx.close();
			return;
		}
		try {
			Account account=BizData.webSocketAction.auth(token);
			if(account.companyId==0) {
				logger.error("account.companyId==0 {} account:{}",token,account.id);
				ctx.close();
				return;
			}
			WebEventChannel channel=ctx.channel().attr(SESSION_KEY).get();
			if(channel==null) {
				logger.error("channel==null {} account:{}",token,account.id);
				ctx.close();
				return;
			}
			String xForwardedFor=null;
			String userAgent=null;
			if(req.headers()!=null) {
				CharSequence cs=req.headers().get("X-Forwarded-For");
				if(cs!=null) {
					xForwardedFor=cs.toString();
				}
				cs=req.headers().get("userAgent");
				logger.info("userAgent:{}",userAgent);
				if(cs!=null) {
					userAgent=cs.toString();
				}
			}
			channel.onOpen(account,xForwardedFor);
			server.addChannel(channel);
			//
			// Handshake
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
					getWebSocketLocation(req), "event", true,MAX_WEBSOCKET_FRAME_SIZE);
			
			handshaker = wsFactory.newHandshaker(req);
			if (handshaker == null) {
				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			} else {
				handshaker.handshake(ctx.channel(), req);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("auth failed with token {}",token);
			ctx.close();
			return;
		}
	}
	
	private  String getWebSocketLocation(FullHttpRequest req) {
		String location = (String) req.headers().get("Host");
		return (isWss?"wss":"ws")+"://" + location;
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame) 
			throws IOException {
		WebEventChannel c=ctx.channel().attr(SESSION_KEY).get();
		
		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(),(CloseWebSocketFrame) frame.retain());
			return;
		}
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format(
					"%s frame types not supported", frame.getClass().getName()));
		}
		// Send the uppercase string back.
		String content = ((TextWebSocketFrame) frame).text();
		c.onMessage(content);
		//回复OK
//		ctx.channel().write(new TextWebSocketFrame("OK"));
	}
	//
	private static void sendHttpResponse(ChannelHandlerContext ctx,
			FullHttpRequest req, FullHttpResponse res) {
		// Generate an error page if response getStatus code is not OK (200).
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
					CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
			HttpUtil.setContentLength(res, res.content().readableBytes());
		}
		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("exception on channal:" + ctx.channel(), cause);
		ctx.close();
	}

	//
	@Override
    public void channelInactive(ChannelHandlerContext ctx) {
		WebEventChannel c=ctx.channel().attr(SESSION_KEY).get();
		if(c!=null){
			c.close();
			if(logger.isDebugEnabled()){
				logger.debug("channelInactive:"+ctx.channel());	
			}	
		}
	}
	//
	@Override
	public void channelActive(ChannelHandlerContext ctx) 
			 {
		WebEventChannel channel=new WebEventChannel(server);
		channel.remoteAddr=ctx.channel().remoteAddress();
		channel.setEndpoint(new WebEventEndpoint(ctx.channel()));
		ctx.channel().attr(SESSION_KEY).set(channel);
		if(logger.isDebugEnabled()){
			logger.debug("channelActive:"+ctx.channel());	
		}
	}
	//
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			 {
		if(logger.isDebugEnabled()){
			logger.debug("userEventTriggered:"+ctx.channel()+"/"+evt);				
		}
	}
}