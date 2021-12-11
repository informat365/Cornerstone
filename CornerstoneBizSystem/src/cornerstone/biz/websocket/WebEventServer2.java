package cornerstone.biz.websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import cornerstone.biz.BizData;
import cornerstone.biz.domain.Account;
import cornerstone.biz.util.DumpUtil;
import cornerstone.biz.util.StringUtil;
import jazmin.core.Jazmin;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.misc.InfoBuilder;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 *
 */
public class WebEventServer2 extends WebSocketServer {
	//
	private static Logger logger = LoggerFactory.get(WebEventServer2.class);
	//
	public static Map<Integer, List<WebEventChannel2>> channelMap;
	public int port;

	//
	public WebEventServer2(int port) {
		super(new InetSocketAddress(port));
		channelMap = new HashMap<>();
		this.port = port;
	}

	//
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		String uri = handshake.getResourceDescriptor();
		if (StringUtil.isEmptyWithTrim(uri)) {
			logger.warn(getClass().getSimpleName()+" onOpen failed.uri:{}", uri);
			return;
		}
		uri = uri.trim();
		logger.debug(getClass().getSimpleName()+" onOpen uri:{}", uri);
		if (uri == null || !uri.startsWith("/event/")) {
			logger.warn("uri error" + uri);
			return;
		}
		String token = uri.substring(7);
		//
		if (token == null) {
			logger.error(getClass().getSimpleName()+" auth failed with token {}", token);
			conn.close();
			return;
		}
		try {
			Account account = BizData.webSocketAction.auth(token);
			if (account.companyId == 0) {
				logger.error(getClass().getSimpleName()+" account.companyId==0 {} account:{}", token, account.id);
				conn.close();
				return;
			}
			String ip=handshake.getFieldValue("X-Forwarded-For");
			String userAgent=handshake.getFieldValue("User-Agent");
			if(StringUtil.isEmptyWithTrim(ip)) {
				if(conn.getRemoteSocketAddress()!=null) {
					ip=conn.getRemoteSocketAddress().toString();
				}
			}
			if(logger.isDebugEnabled()) {
				logger.debug(getClass().getSimpleName()+" onOpen accountId:{} ip:{} userAgent:{}",
						account.id,ip,userAgent);
			}
			WebEventChannel2 channel = new WebEventChannel2(this, account, conn,ip,userAgent);
			conn.setAttachment(channel);
			addChannel(channel);
		} catch (Exception e) {
			logger.error(getClass().getSimpleName()+" error " + e.getMessage());
			logger.error(e.getMessage(), e);
		}
	}

	public List<WebEventChannel2> getChannels(int companyId) {
		if (!channelMap.containsKey(companyId)) {
			return new ArrayList<>();
		}
		return channelMap.get(companyId);
	}

	public void addChannel(WebEventChannel2 channel) {
		Account account = channel.account;
		if (account == null) {
			return;
		}
		List<WebEventChannel2> channels = channelMap.get(account.companyId);
		if (channels == null) {
			channels = new ArrayList<>();
			channelMap.put(account.companyId, channels);
		}
		channels.add(channel);
		logger.info(getClass().getSimpleName()+" addChannel{} accountId:{} companyId:{}", channel, account.id, account.companyId);
	}

	//
	public void removeChannel(WebEventChannel2 channel) {
		if (channel == null) {
			logger.error(getClass().getSimpleName()+" channel==null");
			return;
		}
		Account account = channel.account;
		if (account == null) {
			logger.error(getClass().getSimpleName()+" account == null");
			return;
		}
		List<WebEventChannel2> channels = channelMap.get(account.companyId);
		if (channels == null) {
			logger.error(getClass().getSimpleName()+" channels==null,account.companyId:{}", account.companyId);
			return;
		}
		boolean removeChannel = channels.remove(channel);
		if (logger.isInfoEnabled()) {
			logger.info(getClass().getSimpleName()+" removeChannel{} success:{} accountId:{}", channel, removeChannel, account.id);
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		WebEventChannel2 channel = conn.getAttachment();
		removeChannel(channel);
		if (channel != null) {
			channel.close();
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		WebEventChannel2 channel = conn.getAttachment();
		logger.info(getClass().getSimpleName()+" onMessage channel:{} message:{}", channel, message);
		if (channel != null) {
			channel.onMessage(message);
		}
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		if (logger.isDebugEnabled()) {
			logger.debug(getClass().getSimpleName()+" onMessage conn:{} ByteBufferMsg:{}", conn, message);
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		logger.error(getClass().getSimpleName()+" onError Event");
		logger.error(ex.getMessage(), ex);
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific
			// websocket
		}
	}

	public void init() {
		setReuseAddr(true);
	}

	@Override
	public void onStart() {
		logger.info(getClass().getSimpleName()+" started");
		setConnectionLostTimeout(0);
		setConnectionLostTimeout(100);
		//
		Thread t = new Thread(this::dumpChannel);
		t.setName("WebEventServerTicket");
		t.start();
		//
		logger.info(info());
	}

	//
	public void boardcastAsync(WebEvent event) {
		Jazmin.execute(() -> {
			boardcastData(event);
		});
	}

	// 广播
	public void boardcastData(WebEvent event) {
		long startTime = System.currentTimeMillis();
		broadcast(JSONUtil.toJson(event));
		logger.info(getClass().getSimpleName()+" boardcastWebEvent using:{} channels:{} event:{} ",
				System.currentTimeMillis() - startTime, getConnections().size(), DumpUtil.dump(event));
	}

	void dumpChannel() {
		while (true) {
			if (logger.isInfoEnabled()) {
				logger.info(dumpChannels());
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				logger.catching(e);
			}
		}
	}

	private String dumpChannels() {
		StringBuilder channels = new StringBuilder();
		channels.append("\n==========================================\n");
		channels.append(getClass().getSimpleName()+" webevent sessions\n");
		int total = 0;
		for (Map.Entry<Integer, List<WebEventChannel2>> entry : channelMap.entrySet()) {
			int companyId = entry.getKey();
			List<WebEventChannel2> channel = entry.getValue();
			channels.append("company[").append(companyId).append("]\n");
			channels.append(String.format("%-20s%-20s%-25s%-25s%-35s", "account",
					"ip","createTime","lastAccessTime","userAgent")).append("\n");
			for (WebEventChannel2 e : channel) {
				if (e.account == null) {
					continue;
				}
				channels.append(e.dump()).append("\n");
				total++;
			}
			channels.deleteCharAt(channels.length() - 1);
			channels.append("\n");
		}
		channels.append("\ntotal:").append(total).append("/").append(getConnections().size()).append("\n");
		channels.append("==========================================\n");
		return channels.toString();
	}

	//
	public String info() {
		InfoBuilder ib = InfoBuilder.create();
		ib.section("info").format("%-30s:%-30s\n").print("port", getPort());
		return ib.toString();
	}


}