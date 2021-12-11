/**
 * 
 */
package cornerstone.biz.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.JavaScriptEngine;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.srv.DataTableService;
import jazmin.core.Jazmin;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.JSObject;

/**
 * 
 * @author cs
 *
 */
public class JavaScriptWebApi extends JavaScriptEngine{
	//
	private static Logger logger = LoggerFactory.get(JavaScriptWebApi.class);
	//
	private WebApiDefine webApiDefine;
	private JSObject jsWebApi;
	public Account account;
	private StringBuilder buff;
	public BizService bizService;
	public WebApiReq req;
	public static boolean isDebug;
	private static String[] ARGS=new String[] {"--language=es6","--no-syntax-extensions"};
	//
	//
	public static class NoFilter implements ClassFilter{
        @Override
        public boolean exposeToScripts(String className) {
            return true;
        }
    }
	//
	public JavaScriptWebApi(String source) {
		super(source,ARGS,new NoFilter());
		webApiDefine = createWebApiDefine();
		buff=new StringBuilder();
		bizService=getBizService();
	}
	//
	private BizService getBizService() {
		if(!isDebug) {
			return Jazmin.getApplication().getWired(BizService.class);
		}else {
			return new BizService();
		}
	}
	/**
	 * 
	 * @return
	 */
	private WebApiDefine createWebApiDefine() {
		jsWebApi = (JSObject) engine.get("webApi");
		if (jsWebApi == null) {
			throw new AppException("脚本格式错误 找不到webApi");
		}
		webApiDefine=new WebApiDefine();
		Object execute = jsWebApi.getMember("execute");
		JSObject jsExecute=null;
		if(execute instanceof JSObject) {
			jsExecute=(JSObject)execute;
			if(!jsExecute.isFunction()) {
				throw new AppException("脚本格式错误 execute函数找不到");
			}
		}
		if(jsExecute==null) {
			throw new AppException("脚本格式错误 execute函数找不到");
		}
		if (logger.isInfoEnabled()) {
			logger.info("createWebApiDefine:{}", DumpUtil.dump(webApiDefine));
		}
		//
		return webApiDefine;
	}
	//
	public Account getAcccountByUserName(String userName) {
		return bizService.getExistedAccountByUserName(userName);
	}
	//
	public String run(String key) {
		if (key == null || !key.equals(ConstDefine.GLOBAL_KEY)) {
			logger.error("run failed.key is not match:{}", key);
			return buff.toString();
		}
		try {
			engine.eval("webApi.execute()");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException("执行失败:"+e.getMessage());
		}
		return buff.toString();
	}
	//
	public String toJson(Object obj) {
		return JSONUtil.toJson(obj);
	}
	//
	public List<Map<String,Object>> queryList(String sql) {
		return DataTableService.get().queryList(sql,new ArrayList<>());
	}
	//
	public String queryForString(String sql) {
		return DataTableService.get().queryForString(sql,new ArrayList<>());
	}
	//
	public List<Map<String,Object>> queryList(String sql,List<Object> parameters) {
		return DataTableService.get().queryList(sql,parameters);
	}
	//
	public int queryCount(String sql,List<Object> parameters) {
		return DataTableService.get().queryCount(sql, parameters);
	}
	//
	public void error(String msg) {
		throw new AppException(msg);
	}
	//
	public void print(String message) {
		buff.append(message);
	}
	//
	public void println(String message) {
		buff.append(message).append("\n");
	}
	//
	public Map<String,String> getParameterMap() {
		return req.parameterMap;
	}
	
	public String getParameterBody() {
		return req.body;
	}
	//
	/**
	 * @return the pipelineDefine
	 */
	public WebApiDefine getWebApiDefine() {
		return webApiDefine;
	}

	//

}
