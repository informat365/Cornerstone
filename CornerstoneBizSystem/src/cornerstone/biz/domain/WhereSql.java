package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class WhereSql {

	public String sql;
	
	public List<Object> values;
	//
	public WhereSql() {
		values=new ArrayList<>();
	}
}
