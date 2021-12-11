package cornerstone.biz.websocket;

import java.net.SocketAddress;
import java.util.Date;

import cornerstone.biz.domain.Account;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.util.DumpUtil;
import cornerstone.biz.util.StringUtil;
import io.netty.channel.Channel;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * Session
 * @author cs
 *
 */
public class WebEventChannel {
	//
	private static Logger logger=LoggerFactory.get(WebEventChannel.class);
	//
	String id;
	Date createTime;
	WebEventEndpoint endpoint;
	WebEventServer server;
	Account account;
	SocketAddress remoteAddr;
	String xForwardedFor;
	Date lastAccessTime;
	//
	public WebEventChannel(WebEventServer server) {
		this.server=server;
		createTime=new Date();
	}
	//
	public void onOpen(Account account,String xForwardedFor) {
		logger.info("onOpen accountId:{} xForwardedFor:{}",account.id,xForwardedFor);
		this.account=account;
		this.xForwardedFor=xForwardedFor;
	}
	//
	public void onMessage(String content) {
		lastAccessTime=new Date();
		logger.info("WebEventServer onMessage {} {}",endpoint.getChannel(),content);
	}
	//
	public void close() {
		if(logger.isDebugEnabled()) {
			logger.debug("close id:{} account:{}",id,DumpUtil.dump(account));
		}
		if(server!=null) {
			server.removeChannel(this);
		}
	}
	
	public void sendMessageToClient(String msg){
		if(endpoint!=null) {
			endpoint.write(msg);
		}
	}
	
	private String getIp() {
		if(!StringUtil.isEmptyWithTrim(xForwardedFor)) {
			return xForwardedFor;
		}
		if(remoteAddr!=null) {
			return remoteAddr.toString();
		}
		return "<未知>";
	}
	
	/**
	 * @return the endpoint
	 */
	public WebEventEndpoint getEndpoint() {
		return endpoint;
	}
	/**
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(WebEventEndpoint endpoint) {
		this.endpoint = endpoint;
	}
	//
	public String getChannelId() {
		if(endpoint!=null&&endpoint.getChannel()!=null) {
			Channel c=endpoint.getChannel();
			if(c!=null&&c.id()!=null) {
				return c.id().toString();
			}
		}
		return "<NULL>";
	}
	//
	public String dump() {
		String accountName="";
		String channelId="";
		String ip=getIp();
		String strCreateTime="";
		String strLastAccessTime="";
		if(account!=null) {
			accountName=account.userName+"("+account.id+")";
		}
		channelId=getChannelId();
		if(createTime!=null) {
			strCreateTime=DateUtil.formatDate(createTime, "MM-dd HH:mm:ss");
		}
		if(lastAccessTime!=null) {
			strLastAccessTime=DateUtil.formatDate(lastAccessTime, "MM-dd HH:mm:ss");
		}
		return String.format("%-20s%-15s%-20s%-25s%-25s", accountName,channelId,
				ip,strCreateTime,strLastAccessTime);
	}
}
