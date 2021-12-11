/**
 * 
 */
package cornerstone.biz.ssh;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;

import cornerstone.biz.sftp.SftpChannel;
import cornerstone.biz.util.SshUtil;
import cornerstone.biz.util.StringUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;


/**
 * @author yama
 *
 */
public class SshChannel {
	private static Logger logger=LoggerFactory.get(SshChannel.class);
	//
	public static Map<String,SshChannel> sshChannelMap=new ConcurrentHashMap<>();
	//
	String id;
	String token;
	Date createTime;
	long messageReceivedCount=0;
	long messageSentCount=0;
	public PeerEndpoint endpoint;
	private ChannelShell shell;
	private OutputStream shellOutputStream;
	private InputStream shellInputStream;
	public ConnectionInfo connectionInfo;
	long ticket;
	WebSshServer webSshServer;
	StringBuilder inputOutputMessage;
	SftpChannel sftpChannel;
	//
	public SshChannel(WebSshServer webSshServer) {
		this.webSshServer=webSshServer;
		createTime=new Date();
		ticket=0;
		inputOutputMessage=new StringBuilder();
	}
	//
	
	//
	private static final char RECEIVE_KEY='0';
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	private static final char RECEIVE_WINDOWRESIZE='1';
	//
	
	//
	public void startShell(){
		logger.info("startShell connection to {}", DumpUtil.dump(connectionInfo));
		try {
			shell = SshUtil.shell(connectionInfo.host, connectionInfo.port,
					connectionInfo.user, connectionInfo.password,connectionInfo.privateKey,
					15000);
			shellInputStream = shell.getInputStream();
			shellOutputStream = shell.getOutputStream();
			startInputReader();
			if(!StringUtil.isEmpty(connectionInfo.cmd)) {
				sendMessageToServer(connectionInfo.cmd+"\n");
			}
			if (connectionInfo.channelListener != null) {
				connectionInfo.channelListener.onOpen(this);
			}
		} catch (Exception e) {
			logger.catching(e);
			sendError(e.getMessage());
		}
	}
	//
	public synchronized SftpChannel startSftp() {
		if(sftpChannel!=null) {
			return sftpChannel;
		}
		sftpChannel=SftpChannel.create(connectionInfo);
		logger.info("SftpChannel startSftp uuid:{} {}",sftpChannel.uuid,DumpUtil.dump(sftpChannel));
		return sftpChannel;
	}
	/**
	 * @return the connectionInfo
	 */
	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}
	/**
	 * @param connectionInfo the connectionInfo to set
	 */
	public void setConnectionInfo(ConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}
	//
	void updateTicket(){
		ticket++;
		try{
			if(connectionInfo!=null&&connectionInfo.channelListener!=null){
				connectionInfo.channelListener.onTicket(this, ticket);
			}
		}catch (Exception e) {
			sendError(e.getMessage());
			logger.catching(e);
		}
	}
	//
	private void startInputReader(){
		Thread inputReaderThread=new Thread(() -> {
			while(shell!=null&&!shell.isClosed()){
				try {
					int n = 0;
					byte[] buffer = new byte[4096];
					while (-1 != (n = shellInputStream.read(buffer))) {
						String s=new String(buffer,0,n);
						receiveServerMessage(s);
					}
				} catch (Exception e) {
					sendError(e.getMessage());
					logger.catching(e);
				}
			}
			logger.info("ssh connection :"+shell+" stopped");
			if(webSshServer!=null) {
				webSshServer.removeChannel(id);
			}
			if(endpoint!=null) {
				endpoint.close();
			}

		},"WebSSHInputReader-"+connectionInfo.user+"@"+connectionInfo.host
				+":"+connectionInfo.port);
		inputReaderThread.start();
	}
	//
	public void sendError(String error){
		//show red alert info to client
		String rsp=
				"\033[31m\n\r**************************************************\n\r"+
				"\n\rerror:"+error+"\n\r"+
				"\n\r**************************************************\n\r\033[0m";
		sendMessageToClient(rsp);
	}
	//
	private void receiveServerMessage(String s){
		if(connectionInfo.channelListener!=null){
    		connectionInfo.channelListener.onMessage(SshChannel.this,s);
    	}
    	sendMessageToClient(s);	
	}
	//
	StringBuilder inputBuffer=new StringBuilder();
	//
	public void receiveMessage(String msg){
		//logger.info("receiveMessage cmd:{}",msg);
		messageReceivedCount++;
		inputOutputMessage.append(msg);
		try{
			receiveMessage0(msg);
		}catch (Exception e) {
			logger.catching(e);
			sendError(e.getMessage());
		}
	}
	private void receiveMessage0(String msg){
		char command=msg.charAt(0);
		if(command==RECEIVE_KEY&&connectionInfo.enableInput){
			boolean sendToServer=true;
			if(connectionInfo.channelListener!=null){
				sendToServer=connectionInfo.channelListener.inputSendToServer();
			}
			if(sendToServer){
				for(int i=1;i<msg.length();i++){
					sendMessageToServer(msg.charAt(i)+"");
				}
			}else{
				//hook input mode
				//ECHO 
				for(int i=1;i<msg.length();i++){
					char c=msg.charAt(i);
					sendMessageToClient(c+"");
					switch (c) {
					case 127://del
						if(inputBuffer.length()>0){
							sendMessageToClient(((char)0x08)+"\033[J");//BS
							inputBuffer.deleteCharAt(inputBuffer.length()-1);		
						}
						break;
					case '\r':
						sendMessageToClient("\n");
						if(connectionInfo.channelListener!=null){
							connectionInfo.channelListener.onInput(this, inputBuffer.toString().trim());
						}
						inputBuffer.delete(0,inputBuffer.length());
					default:
						inputBuffer.append(c);
						break;
					}
				}	
			}
			
			return;
		}
		//
		if(command==RECEIVE_WINDOWRESIZE){
			String t=msg.substring(1);
			String ss[]=t.split(",");
			if(shell instanceof com.jcraft.jsch.ChannelShell){
				shell.setPtySize(Integer.valueOf(ss[0]),Integer.valueOf(ss[1]),0,0);	
			}
			return;
		}
	}
	//
	public void sendMessageToServer(String cmd){
		try{
			inputOutputMessage.append(cmd);
			shellOutputStream.write((cmd).getBytes());
			shellOutputStream.flush();
		}catch (Exception e) {
			logger.catching(e);
		}
	}
	//
	public void sendMessageToClient(String msg){
		messageSentCount++;
		if(endpoint!=null) {
			endpoint.write(msg);
		}
	}
	//
	public void closeChannel(){
		if(connectionInfo!=null&&connectionInfo.channelListener!=null){
			connectionInfo.channelListener.onClose(this);
		}
		if(shell!=null&&shell.isConnected()){
			shell.disconnect();
		}
		try {
			if(shell!=null&&shell.getSession().isConnected()){
				shell.getSession().disconnect();
			}
		} catch (JSchException e) {
			logger.catching(e);
		}
//		BizData.sshAction.saveResult();
		if(webSshServer!=null) {
			webSshServer.removeChannel(id);
		}
		shell=null;
		try {
			if(token!=null) {
				SshChannel.sshChannelMap.remove(token);
				if(logger.isDebugEnabled()) {
					logger.debug("sshChannelMap remove {}",token);
				}
			}
			if(sftpChannel!=null) {
				sftpChannel.exit();
			}
		} catch (Exception e) {
			logger.error("sshChannelMap.remove {}",token);
			logger.error(e.getMessage(),e);
		}
	}
	//
	public String getOutputMessage() {
		return inputOutputMessage.toString();
	}
}
