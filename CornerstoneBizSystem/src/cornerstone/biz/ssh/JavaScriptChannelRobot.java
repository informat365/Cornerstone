/**
 * 
 */
package cornerstone.biz.ssh;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.script.ScriptContext;
import javax.script.ScriptException;

import cornerstone.biz.BizData;
import cornerstone.biz.domain.CmdbRobot;
import cornerstone.biz.domain.JavaScriptEngine;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.IOUtil;

/**
 * @author yama
 *
 */
public class JavaScriptChannelRobot extends JavaScriptEngine implements ScriptChannelContext,ChannelListener{
	private static Logger logger=LoggerFactory.get(JavaScriptChannelRobot.class);
	static class ExpectHolder{
		String regex;
		boolean once;
		ExpectCallback callback;
	}
	//
	HookInputCallback hookInputCallback;
	TicketCallback ticketCallback;
	ActionCallback openCallback;
	ActionCallback closeCallback;
	Map<String,ExpectHolder>expectCallbacks;
	SshChannel webSshChannel;
	Map<Long,List<ActionCallback>>afterTicketCallbacks;
	//
	//
	public JavaScriptChannelRobot(String source)
			throws ScriptException {
		super(null);
		expectCallbacks=new ConcurrentHashMap<>();
		afterTicketCallbacks=new ConcurrentHashMap<>();
		bindings.put("robot", this);
		engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
		eval(source);
	}
	//----------------------------------------------------------------------
	@Override
	public String name() {
		if(webSshChannel!=null){
			return webSshChannel.connectionInfo.name+"";
		}
		return "undefined";
	}
	@Override
	public long setTimeout(int ticket, ActionCallback callback) {
		long targetTicket=webSshChannel.ticket+ticket;
		List<ActionCallback>list=afterTicketCallbacks.get(targetTicket);
		if(list==null){
			list=new LinkedList<>();
			afterTicketCallbacks.put(targetTicket, list);
		}
		list.add(callback);
		return targetTicket;
	}
	//
	@Override
	public void clearTimeout(long ticket) {
		afterTicketCallbacks.remove(ticket);
	}
	//
	@Override
	public boolean enableInput() {
		if(webSshChannel!=null){
			return webSshChannel.connectionInfo.enableInput;
		}
		return false;
	}
	@Override
	public void enableInput(boolean enable) {
		if(webSshChannel!=null){
			webSshChannel.connectionInfo.enableInput=enable;
		}	
	}
	//
	@Override
	public void sends(String msg) {
		if(webSshChannel!=null){
			webSshChannel.sendMessageToServer(msg);
		}else {
			logger.debug("webSshChannel is null");
		}
	}
	//
	@Override
	public void sendc(String msg) {
		if(webSshChannel!=null){
			webSshChannel.sendMessageToClient(msg);
		}else {
			logger.debug("webSshChannel is null");
		}
	}
	//
	@Override
	public void close() {
		if(webSshChannel!=null){
			webSshChannel.closeChannel();
		}
	}
	//
	@Override
	public void ticket(TicketCallback callback) {
		this.ticketCallback=callback;
	}
	//
	private void expect0(String regex,boolean once,ExpectCallback callback){
		synchronized (expectCallbacks) {
			ExpectHolder holder=new ExpectHolder();
			holder.callback=callback;
			holder.once=once;
			holder.regex=regex;
			expectCallbacks.put(regex, holder);
		}
	}
	//
	@Override
	public void expect(String regex, ExpectCallback callback) {
		expect0(regex,false,callback);
	}
	@Override
	public void expectOnce(String regex,ExpectCallback callback){
		expect0(regex,true,callback);
	}
	@Override
	public void expectClear() {
		synchronized (expectCallbacks) {
			expectCallbacks.clear();
		}
	}
	@Override
	public void hookin(HookInputCallback callback) {
		this.hookInputCallback=callback;
		enableInput(callback!=null);
	}
	//
	@Override
	public void open(ActionCallback callback) {
		logger.debug("open callback:{}",callback);
		this.openCallback=callback;;
	}
	//
	@Override
	public void close(ActionCallback callback) {
		logger.debug("close callback:{}",callback);
		this.closeCallback=callback;
	}
	//
	@Override
	public String host() {
		return webSshChannel==null?null:webSshChannel.connectionInfo.host;
	}
	//
	@Override
	public int port() {
		return webSshChannel==null?null:webSshChannel.connectionInfo.port;
	}
	//
	@Override
	public String user() {
		return webSshChannel==null?null:webSshChannel.connectionInfo.user;
	}
	//
	@Override
	public String password() {
		return webSshChannel==null?null:webSshChannel.connectionInfo.password;
	}
	//
	@Override
	public long ticket() {
		return webSshChannel==null?0:webSshChannel.ticket;
	}
	//
	@Override
	public void http(String url,HttpCallback callback) {
		try {
			String result=IOUtil.getContent(new URL(url).openStream());
			callback.invoke(result, null);
		} catch (Exception e) {
			callback.invoke(null, e.getMessage());
		} 
	}
	@Override
	public void log(String msg) {
		logger.info(msg);
	}
	//----------------------------------------------------------------------
	@Override
	public void onOpen(SshChannel channel) {
		logger.info("onOpen channel:{} openCallback:{}",channel,openCallback);
		this.webSshChannel=channel;
		if(this.openCallback!=null){
			this.openCallback.invoke();
		}
	}
	//
	@Override
	public void onInput(SshChannel channel, String message) {
		logger.info("onInput channel:{} message:{}",channel,message);
		if(hookInputCallback!=null){
			hookInputCallback.invoke(message);
		}
	}
	//
	@Override
	public boolean inputSendToServer() {
		return hookInputCallback==null?true:false;
	}
	//
	StringBuilder messageBuffer=new StringBuilder();
	//
	@Override
	public void onMessage(SshChannel channel, String message) {
		synchronized (messageBuffer) {
			for(char c:message.toCharArray()){
				messageBuffer.append(c);
				if(c=='\n'||c=='\r'){
					matchMessage(messageBuffer.toString().trim());
					messageBuffer.delete(0, messageBuffer.length());
				}
			}
		}
	}
	//
	private void matchMessage(String line){
		if(line.trim().isEmpty()){
			return;
		}
		List<String>removedCallbacks=new LinkedList<>();
		expectCallbacks.forEach((regex,holder)->{
			if(Pattern.matches(regex, line)){
				holder.callback.invoke(line);
				if(holder.once){
					removedCallbacks.add(holder.regex);
				}
			}
		});
		for(String s:removedCallbacks){
			expectCallbacks.remove(s);
		}
	}
	//
	@Override
	public void onTicket(SshChannel channel, long ticket) {
		/**
		 * last message is not end of \n 
		 * get them on next ticket
		 */
		synchronized (messageBuffer) {
			if(messageBuffer.length()>0){
				matchMessage(messageBuffer.toString().trim());
				messageBuffer.delete(0, messageBuffer.length());
			}
		}
		afterTicketCallbacks.forEach((target,list)->{
			if(target==ticket){
				list.forEach(action->action.invoke());
			}
		});
		afterTicketCallbacks.remove(ticket);
		if(this.ticketCallback!=null){
			this.ticketCallback.invoke(ticket);
		}
	}
	//
	@Override
	public void onClose(SshChannel channel) {
		if(this.closeCallback!=null){
			this.closeCallback.invoke();
		}
	}
	
	@Override
	public void include(String robotName) {
		logger.info("include:{}",robotName);
		CmdbRobot robot=BizData.sshAction.getCmdbRobot(robotName);
		if(robot==null) {
			sendc("robot not found with name "+robotName);
			return;
		}
		try {
			engine.eval(robot.code);
		} catch (ScriptException e) {
			logger.error(e.getMessage(),e);
			
		}
	}
}
