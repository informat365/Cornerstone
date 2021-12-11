/**
 * 
 */
package cornerstone.biz.systemhook;

import java.util.ArrayList;
import java.util.List;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.JavaScriptEngine;
import cornerstone.biz.domain.Task.TaskDetailInfo;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;
import jdk.nashorn.api.scripting.JSObject;

/**
 * 
 * @author cs
 *
 */
public class JavaScriptSystemHook extends JavaScriptEngine{
	//
	private static Logger logger = LoggerFactory.get(JavaScriptSystemHook.class);
	//
	private SystemHookDefine systemHookDefine;
	private JSObject jsSystemHook;
	public List<String> messages;
	public Account account;
	//
	public JavaScriptSystemHook(String source) {
		super(source);
		systemHookDefine = createSystemHookDefine();
		messages=new ArrayList<>();
	}
	/**
	 * 
	 * @return
	 */
	private SystemHookDefine createSystemHookDefine() {
		jsSystemHook = (JSObject) engine.get("systemHook");
		if (jsSystemHook == null) {
			throw new AppException("脚本格式错误 找不到systemHook");
		}
		systemHookDefine=new SystemHookDefine();
		systemHookDefine.functions=getFunctionNamesForSet(jsSystemHook);
		if (logger.isInfoEnabled()) {
			logger.info("systemHookDefine:{}", DumpUtil.dump(systemHookDefine));
		}
		//
		return systemHookDefine;
	}
	
	//
	public void run(String key,String functionName,SystemHookContext ctx) {
		if (key == null || !key.equals(ConstDefine.GLOBAL_KEY)) {
			logger.error("run failed.key is not match:{}", key);
			return;
		}
		try {
			bindings.put("ctx",ctx);
			engine.eval("systemHook."+functionName+"()");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException("执行失败:"+e.getMessage());
		}
	}
	//
	public String toJson(Object obj) {
		return JSONUtil.toJson(obj);
	}
	//
	public void info(String message) {
		if(logger.isDebugEnabled()) {
			logger.debug("info:"+message);
		}
		messages.add(message);
	}
	//
	public void abort(String msg) {
		logger.warn("abort {}",msg);
		throw new AppException(msg);
	}
	/**
	 * @return the pipelineDefine
	 */
	public SystemHookDefine getSystemHookDefine() {
		return systemHookDefine;
	}

	
}
