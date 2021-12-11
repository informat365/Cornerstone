package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jazmin.core.Jazmin;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * 
 * @author cs
 *
 */
public abstract class JavaScriptEngine {
	//
	private static Logger logger=LoggerFactory.get(JavaScriptEngine.class);
	//
	protected ScriptEngine engine;
	
	protected Bindings bindings;
	//
	private static String[] DEFUALT_ARGS=new String[] {"--language=es6", "--no-java", "--no-syntax-extensions"};
	//
	//
	public static interface JsDateWrapper {
	    long getTime();
	    int getTimezoneOffset();
	}
	//
	public JavaScriptEngine(String source) {
		this(source,DEFUALT_ARGS,null);
	}
	//
	public JavaScriptEngine(String source,String[] args) {
		this(source,args,null);
	}
	//
	public JavaScriptEngine(String source,String[] args,ClassFilter classFilter) {
		logger.info("JavaScriptEngine source:{} args:{}", source,args);
		NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		if(classFilter!=null) {
			engine = factory.getScriptEngine(args,Jazmin.getAppClassLoader(),classFilter);
		}else {
			engine = factory.getScriptEngine(args);
		}
		try {
			bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.remove("print");
			bindings.remove("echo");
			bindings.remove("load");
			bindings.remove("loadWithNewGlobal");
			bindings.remove("exit");
			bindings.remove("quit");
			bindings.put("$", this);
			engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
			if(source!=null) {
				engine.eval(source);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AppException(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param source
	 * @throws ScriptException
	 */
	protected void eval(String source)throws ScriptException{
		if(source==null) {
			return;
		}
		engine.eval(source); 
		if(logger.isDebugEnabled()) {
			logger.debug("eval source:{}",source);
		}
	}
	
	/**
	 * 获取函数名列表
	 * @param jsObject
	 * @return
	 */
	protected Set<String> getFunctionNamesForSet(JSObject jsObject){
		Set<String> result=new LinkedHashSet<>();
		List<String> list=getFunctionNames(jsObject);
		for (String func: list) {
			result.add(func);
		}
		return result;
	}
	
	/**
	 * 获取函数名列表
	 * @param jsObject
	 * @return
	 */
	protected List<String> getFunctionNames(JSObject jsObject){
		List<String> list=new ArrayList<>();
		if(jsObject==null) {
			return list;
		}
		Set<String> keys=jsObject.keySet();
		for (String key : keys) {
			Object member=jsObject.getMember(key);
			if(member instanceof JSObject) {
				JSObject jsMember=(JSObject)member;
				if(jsMember.isFunction()) {
					list.add(key);
				}
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @param jsObject
	 * @return
	 */
	protected List<JSObject> getArray(JSObject jsObject) {
		List<JSObject> list = new ArrayList<>();
		Object len = jsObject.getMember("length");
		if (len instanceof Number) {
			int n = ((Number) len).intValue();
			for (int i = 0; i < n; ++i) {
				JSObject item = (JSObject) jsObject.getSlot(i);
				list.add(item);
			}
		} else {
			throw new AppException("is not array");
		}
		return list;
	}

	/**
	 * 
	 * @param jsObject
	 * @param name
	 * @return
	 */
	protected String getStringValue(JSObject jsObject, String name) {
		Object member = jsObject.getMember(name);
		if (member == null || "undefined".equals(member.toString())) {
			return null;
		}
		if (member instanceof String) {
			return (String) member;
		}
		throw new AppException(name + " must be string type");
	}
	
	protected Long getDateValue(JSObject jsObject, String name) {
		Object member = jsObject.getMember(name);
		if (member == null || "undefined".equals(member.toString())) {
			return null;
		}
		if (member instanceof Double) {
			return ((Double)member).longValue();
		}
		if (member instanceof Long) {
			return (Long)member;
		}
		if(member instanceof ScriptObjectMirror) {
			JsDateWrapper jsDate = ((Invocable) engine).getInterface(member, JsDateWrapper.class);
			return jsDate.getTime();
		}
		throw new AppException(name + " must be date type");
	}
	
	protected List<String> getStringListValue(JSObject jsObject,String name) {
		Object member=jsObject.getMember(name);
		if(member==null|| "undefined".equals(member.toString())) {
			return null;
		}
		List<String> list=new ArrayList<>();
		if(member instanceof JSObject) {
			JSObject obj=(JSObject)member;
			Set<String> keys=obj.keySet();
			for (String key : keys) {
				String value=getStringValue(obj, key);
				if(value!=null) {
					list.add(value);
				}
			}
			return list;
		}
		throw new AppException(name+" must be List type");
	}
	//
	protected Map<String,String> getMapValue(JSObject jsObject,String name) {
		Object member=jsObject.getMember(name);
		if(member==null|| "undefined".equals(member.toString())) {
			return null;
		}
		if(member instanceof JSObject) {
			Map<String,String> result=new LinkedHashMap<>();
			JSObject object=(JSObject)member;
			Set<String> keys=object.keySet();
			keys.forEach(key->{
				String value=getStringValue(object,key);
				result.put(key, value);
			});
			return result;
		}
		throw new AppException(name+" must be map type");
	}
	
	//
	protected JSObject getJSObjectMember(Object object) {
		if(object==null) {
			return null;
		}
		if(!(object instanceof JSObject)) {
			return null;
		}
		return (JSObject)object;
	}
	
	//
	protected JSObject getJSObjectMember(JSObject jsObject,String name) {
		Object member=jsObject.getMember(name);
		if(member==null|| "undefined".equals(member.toString())) {
			return null;
		}
		if(!(member instanceof JSObject)) {
			return null;
		}
		return (JSObject)member;
	}
}
