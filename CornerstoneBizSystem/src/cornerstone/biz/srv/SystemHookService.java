package cornerstone.biz.srv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cornerstone.biz.dao.BizDAO;
import jazmin.core.Jazmin;
import jazmin.core.app.AutoWired;

/**
 * 
 * @author cs
 *
 */
public class SystemHookService {

	@AutoWired
	BizDAO dao;
	//
	public static SystemHookService get() {
		return Jazmin.getApplication().getWired(SystemHookService.class);
	}
	//
	public List<Map<String, Object>> queryList(String sql,List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.querListMap(sql,parameters.toArray());
	}
	//
	
	public Long queryLong(String sql,List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.queryForLong(sql,parameters.toArray());
	}
	//
	
	public Integer queryCount(String sql,List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.queryForInteger(sql,parameters.toArray());
	}
	//
	
	public Integer queryInteger(String sql,List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.queryForInteger(sql,parameters.toArray());
	}
	
	public String queryForString(String sql, List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.queryForString(sql, parameters.toArray());
	}
}
