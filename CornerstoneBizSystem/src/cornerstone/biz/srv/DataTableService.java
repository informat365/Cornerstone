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
public class DataTableService {

	@AutoWired
	BizDAO dao;
	//
	public static DataTableService get() {
		return Jazmin.getApplication().getWired(DataTableService.class);
	}
	//
	public List<Map<String, Object>> queryList(String sql,List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.querListMap(sql,parameters.toArray());
	}
	//
	public Map<String, Object> queryForObject(String sql,List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.queryMap(sql,parameters.toArray());
	}
	//
	
	public Long queryForLong(String sql,List<Object> parameters) {
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
	
	public Integer queryForInteger(String sql,List<Object> parameters) {
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
	//
	public List<String> queryForStrings(String sql, List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.queryForStrings(sql, parameters.toArray());
	}
	//
	public List<Integer> queryForIntegers(String sql, List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.queryForIntegers(sql, parameters);
	}
	//
	public List<Long> queryForLongs(String sql, List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.queryForLongs(sql, parameters);
	}
	//
	public Double queryForDouble(String sql,List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.queryForDouble(sql,parameters.toArray());
	}
	//
	public Float queryForFloat(String sql,List<Object> parameters) {
		if(parameters==null) {
			parameters=new ArrayList<>();
		}
		return dao.queryForFloat(sql,parameters.toArray());
	}
}
