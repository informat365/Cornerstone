/**
 * 
 */
package cornerstone.biz.datatable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.datatable.DataTableDefine.DataTableDefineQuery;
import cornerstone.biz.datatable.DataTableDefine.QueryItem;
import cornerstone.biz.datatable.DataTableResult.Chart;
import cornerstone.biz.datatable.DataTableResult.Series;
import cornerstone.biz.datatable.DataTableResult.XCell;
import cornerstone.biz.datatable.DataTableResult.XCol;
import cornerstone.biz.datatable.DataTableResult.XRow;
import cornerstone.biz.datatable.DataTableResult.XSheet;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.JavaScriptEngine;
import cornerstone.biz.srv.DataTableService;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DateUtil;
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
public class JavaScriptDataTable extends JavaScriptEngine{
	//
	private static Logger logger = LoggerFactory.get(JavaScriptDataTable.class);
	//
	private DataTableDefine dataTableDefine;
	private JSObject jsDataTable;
	public List<String> messages;
	public DataTableResult table;
	public DataTableDefineQuery query;
	public Account account;
	//
	public JavaScriptDataTable(String source) {
		super(source,new String[] {"--language=es6","--no-syntax-extensions"});
		dataTableDefine = createDataTableDefine();
		messages=new ArrayList<>();
		table=new DataTableResult();
	}
	/**
	 * 
	 * @return
	 */
	private DataTableDefine createDataTableDefine() {
		jsDataTable = (JSObject) engine.get("dataTable");
		if (jsDataTable == null) {
			throw new AppException("脚本格式错误 找不到dataTable");
		}
		dataTableDefine=new DataTableDefine();
		List<QueryItem> queryList=new ArrayList<>();
		Object query = jsDataTable.getMember("query");
		if(query instanceof JSObject) {
			List<JSObject> queryItems=getArray((JSObject)query);
			for (JSObject item : queryItems) {
				QueryItem queryItem=new QueryItem();
				queryItem.name = getStringValue(item, "name");
				queryItem.type = getStringValue(item, "type");
				if(!QueryItem.TYPES.contains(queryItem.type)) {
					throw new AppException("query.type类型错误");
				}
				if(queryItem.type.equals(QueryItem.TYPE_DATE)) {
					queryItem.value = getDateValue(item, "value");
				}else {
					queryItem.value = getStringValue(item, "value");
				}
				if(queryItem.type.equals(QueryItem.TYPE_SELECT)){
					queryItem.options = getStringListValue(item,"options");
				}
				queryList.add(queryItem);	
			}
			dataTableDefine.query=queryList;
		}
		Object execute = jsDataTable.getMember("execute");
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
			logger.info("getDataTableDefine:{}", DumpUtil.dump(dataTableDefine));
		}
		//
		return dataTableDefine;
	}
	//
	public void run(String key) {
		if (key == null || !key.equals(ConstDefine.GLOBAL_KEY)) {
			logger.error("run failed.key is not match:{}", key);
			return;
		}
		try {
			engine.eval("dataTable.execute()");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException("执行失败:"+e.getMessage());
		}
	}
	//
	public XRow mapToRow(Map<String,Object> map,List<String> keys,Map<String,String>format) {
		XRow row=new XRow();
		for (String key : keys) {
			String fmt=null;
			if(format!=null) {
				fmt=format.get(key);
			}
			Object value=map.get(key);
			XCell cell=new XCell();
			if(value!=null) {
				cell.value=value;
				if(Number.class.isAssignableFrom(value.getClass())) {//判断是否是数字
					cell.dataType=XCell.DATA_TYPE_数字;
				}
				if(fmt!=null) {
					if("fmtBool".equals(fmt)) {
						cell.value=fmtBool((Integer) value);
					}
					if("fmtDate".equals(fmt)) {
						cell.value=fmtDate((Date) value);
					}
					if("fmtDateTime".equals(fmt)) {
						cell.value=fmtDateTime((Date) value);
					}
				}
			}
			row.cells.add(cell);
		}
		return row;
	}
	//
	public XRow arrayToRow(List<String> names) {
		if(names==null) {
			throw new AppException("内容不能为空");
		}
		XRow row=new XRow();
		for (String name : names) {
			XCell cell=new XCell();
			cell.value=name;
			row.cells.add(cell);
		}
		return row;
	}

	public void log(String msg){
		logger.info("JavaScriptDataTable log:{}",msg);
	}
	//
	public XRow arrayToRow(List<String> names,int maxRowNum) {
		XRow row=new XRow();
		if(names==null) {
			throw new AppException("内容不能为空");
		}
		if(names.size()>maxRowNum) {
			throw new AppException("maxRowNum不能少于"+names.size());
		}
		for (int i=0;i<maxRowNum;i++) {
			XCell cell=new XCell();
			if(i<names.size()) {
				cell.value=names.get(i);
			}
			row.cells.add(cell);
		}
		return row;
	}
	//
	public String fmtBool(Integer value) {
		if(value==null) {
			return "";
		}
		return value==1?"是":"否";
	}
	//
	public String fmtDate(Date value) {
		if(value==null) {
			return "";
		}
		return DateUtil.formatDate(value,"yyyy-MM-dd");
	}
	//
	public String fmtDateTime(Date value) {
		if(value==null) {
			return "";
		}
		return DateUtil.formatDate(value,"yyyy-MM-dd HH:mm:ss");
	}
	//
	public String queryForString(String sql) {
		return DataTableService.get().queryForString(sql,new ArrayList<>());
	}
	//
	public String queryForString(String sql,List<Object> parameters) {
		return DataTableService.get().queryForString(sql,parameters);
	}
	//
	public List<String> queryForStrings(String sql) {
		return DataTableService.get().queryForStrings(sql,new ArrayList<>());
	}
	//
	public List<String> queryForStrings(String sql,List<Object> parameters) {
		return DataTableService.get().queryForStrings(sql,parameters);
	}
	//
	public Integer queryForInteger(String sql) {
		return DataTableService.get().queryForInteger(sql,new ArrayList<>());
	}
	//
	public Integer queryForInteger(String sql,List<Object> parameters) {
		return DataTableService.get().queryForInteger(sql,parameters);
	}
	//
	public List<Integer> queryForIntegers(String sql) {
		return DataTableService.get().queryForIntegers(sql,new ArrayList<>());
	}
	//
	public List<Integer> queryForIntegers(String sql,List<Object> parameters) {
		return DataTableService.get().queryForIntegers(sql,parameters);
	}
	//
	public Long queryForLong(String sql) {
		return DataTableService.get().queryForLong(sql,new ArrayList<>());
	}
	//
	public Long queryForLong(String sql,List<Object> parameters) {
		return DataTableService.get().queryForLong(sql,parameters);
	}
	//
	public List<Long> queryForLongs(String sql) {
		return DataTableService.get().queryForLongs(sql,new ArrayList<>());
	}
	//
	public List<Long> queryForLongs(String sql,List<Object> parameters) {
		return DataTableService.get().queryForLongs(sql,parameters);
	}
	//
	public List<Map<String,Object>> queryList(String sql) {
		return DataTableService.get().queryList(sql,new ArrayList<>());
	}
	//
	public List<Map<String,Object>> queryList(String sql,List<Object> parameters) {
		if(logger.isDebugEnabled()) {
			logger.debug("queryList sql:{} \nparameters:{}",
					sql,
					cornerstone.biz.util.DumpUtil.dump(parameters));
			if(parameters!=null) {
				for (Object parameter : parameters) {
					logger.debug("parameter {} {}",parameter.getClass(),parameter);
				}
				
			}
		}
		return DataTableService.get().queryList(sql,parameters);
	}
	//
	public Map<String,Object> queryForObject(String sql) {
		return DataTableService.get().queryForObject(sql,new ArrayList<>());
	}
	//
	public int queryCount(String sql) {
		return DataTableService.get().queryCount(sql, new ArrayList<>());
	}
	//
	public int queryCount(String sql,List<Object> parameters) {
		return DataTableService.get().queryCount(sql, parameters);
	}
	//
	public Double queryForDouble(String sql) {
		return DataTableService.get().queryForDouble(sql,new ArrayList<>());
	}
	//
	public Double queryForDouble(String sql,List<Object> parameters) {
		return DataTableService.get().queryForDouble(sql,parameters);
	}
	//
	public Float queryForFloat(String sql) {
		return DataTableService.get().queryForFloat(sql,new ArrayList<>());
	}
	//
	public Float queryForFloat(String sql,List<Object> parameters) {
		return DataTableService.get().queryForFloat(sql,parameters);
	}
	//
	public void error(String msg) {
		throw new AppException(msg);
	}
	//
	public DataTableResult getTable() {
		return table;
	}
	//
	public XSheet createSheet() {
		XSheet sheet=new XSheet();
		return sheet;
	}
	//
	public XCell createCell() {
		XCell cell=new XCell();
		return cell;
	}
	//
	public XRow createRow() {
		XRow row=new XRow();
		return row;
	}
	//
	public XCol createCol() {
		XCol col=new XCol();
		return col;
	}
	//
	public XRow createRow(int cellCount) {
		XRow row=new XRow();
		for(int i=0;i<cellCount;i++) {
			row.cells.add(new XCell());
		}
		return row;
	}
	//
	public Chart createChart() {
		Chart chart=new Chart();
		return chart;
	}
	//
	public Series createSeries() {
		Series series=new Series();
		return series;
	}
	//
	public Object param(String key) {
		if(key==null) {
			return null;
		}
		if(query==null||query.parameters==null) {
			return null;
		}
		return query.parameters.get(key);
	}
	//
	public String toJson(Object obj) {
		return JSONUtil.toJson(obj);
	}
	//
	public String getClass(Object obj) {
		if(obj==null) {
			return "<null>";
		}
		return obj.getClass().toString();
	}
	//
	public Long toLong(Double value) {
		if(value==null) {
			return null;
		}
		return Long.valueOf(value.longValue());
	}
	//
	public Date toDate(Double value) {
		if(value==null) {
			return null;
		}
		return new Date(value.longValue());
	}

	public Date toDate(Long value) {
		if(value==null) {
			return null;
		}
		return new Date(value);
	}
	//
	public void info(String message) {
		if(logger.isDebugEnabled()) {
			logger.debug("info:"+message);
		}
		messages.add(message);
	}

	//
	/**
	 * @return the pipelineDefine
	 */
	public DataTableDefine getDataTableDefine() {
		return dataTableDefine;
	}
	//
	public String cleanHtml(String htmlContent) {
		return BizUtil.cleanHtml(htmlContent);
	}

	//

}
