package cornerstone.biz.websocket;

import java.util.Date;

import org.java_websocket.WebSocket;

import cornerstone.biz.domain.Account;
import cornerstone.biz.util.DateUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * Session
 * @author cs
 *
 */
public class WebEventChannel2 {
	//
	private static Logger logger=LoggerFactory.get(WebEventChannel2.class);
	//
	Date createTime;
	Date lastAccessTime;
	WebEventServer2 server;
	Account account;
	WebSocket conn;
	String ip;
	String userAgent;
	//
	public WebEventChannel2(WebEventServer2 server,Account account,WebSocket conn,String ip,String userAgent) {
		this.server=server;
		this.account=account;
		this.conn=conn;
		this.ip=ip;
		this.userAgent=userAgent;
		createTime=new Date();
	}
	//
	public void onMessage(String message) {
		lastAccessTime=new Date();
		logger.info("onMessage account:{} message:{}",account.userName,message);
	}
	//
	public void close() {
		if(logger.isDebugEnabled()) {
			logger.debug("close account:{}",account.userName);
		}
	}
	
	public void send(String msg){
		if(conn!=null&&msg!=null) {
			conn.send(msg);
			if(logger.isDebugEnabled()) {
				logger.debug("send account:{} message: {}",account.userName,msg);
			}
		}
	}
	
	public String dump() {
		String accountName="";
		String strCreateTime="";
		String strLastAccessTime="";
		if(account!=null) {
			accountName=account.userName+"("+account.id+")";
		}
		if(createTime!=null) {
			strCreateTime=DateUtil.formatDate(createTime, "MM-dd HH:mm:ss");
		}
		if(lastAccessTime!=null) {
			strLastAccessTime=DateUtil.formatDate(lastAccessTime, "MM-dd HH:mm:ss");
		}
		String strUserAgent=userAgent;
		if(strUserAgent!=null&&strUserAgent.length()>35) {
			strUserAgent=strUserAgent.substring(0,35);
		}
		return String.format("%-20s%-20s%-25s%-25s%-35s", accountName,
				ip,strCreateTime,strLastAccessTime,strUserAgent);
	}
}
