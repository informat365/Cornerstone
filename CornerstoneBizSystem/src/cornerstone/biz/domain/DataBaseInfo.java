package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cornerstone.biz.util.JdbcUtil.ColumnInfo;
import cornerstone.biz.util.JdbcUtil.IndexInfo;
import cornerstone.biz.util.JdbcUtil.TableInfo;
/**
 * 
 * @author cs
 *
 */
public class DataBaseInfo {
	//
	public static class TableDetailInfo{
		public TableInfo tableInfo;
		public List<ColumnInfo> columnInfo;
		public Map<String,List<IndexInfo>> uniqueIndexMap;
	}
	
	public List<ColumnInfo> columnInfos;
	//
	public int id;
	
	public Map<String,TableDetailInfo> tableInfos;
	
	public DataBaseInfo() {
		columnInfos=new ArrayList<>();
		tableInfos=new LinkedHashMap<>();
	}
}
