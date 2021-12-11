package cornerstone.biz.datatable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author cs
 *
 */
public class DataTableDefine {
	//
	public List<QueryItem> query;
	//
	public static class QueryItem{
		//
		public static final String TYPE_PROJECT="project";
		public static final String TYPE_DATE="date";
		public static final String TYPE_TEXT="text";
		public static final String TYPE_SELECT="select";
		//
		public static final Set<String> TYPES=new HashSet<>(Arrays.asList(
				TYPE_DATE,TYPE_TEXT,TYPE_SELECT,TYPE_PROJECT
				));
		//
		public String name;
		public String type;
		public Object value;
		public List<String> options;
	}
	//
	public static class DataTableDefineQuery{
		
		public Integer projectId;

		public Integer iterationId;
		
		public Map<String,Object> parameters;
		
		public DataTableDefineQuery() {
			parameters=new HashMap<>();
		}
	}
}
