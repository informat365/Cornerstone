package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

import jazmin.driver.jdbc.smartjdbc.provider.SqlProvider;

/**
 * 查询条件
 * @author cs
 *
 */
public class QueryCondition {
	//
	public static final int TYPE_查询条件=0;
	public static final int TYPE_满足所有条件=1;//AND
	public static final int TYPE_满足任一条件=2;//OR
	//注意:这些是后端自己的值 跟前端没关系
	public static final int OPERATOR_等于=1;
	public static final int OPERATOR_不等于=2;
	public static final int OPERATOR_大于=3;
	public static final int OPERATOR_小于=4;
	public static final int OPERATOR_LIKE=5;
	public static final int OPERATOR_大于等于=6;
	public static final int OPERATOR_小于等于=7;
	public static final int OPERATOR_IN=8;
	public static final int OPERATOR_NOT_IN=9;
	public static final int OPERATOR_IS_NULL=10;
	public static final int OPERATOR_IS_NOT_NULL=11;

	public String alias;
		
	public String key;
		
	public int operator;
		
	public Object value;
	
	public String whereSql;//优先级最高

	public int type;
	
	public List<QueryCondition> children;
	
	public boolean ignore;
	
	public QueryCondition() {
		children=new ArrayList<>();
		alias=SqlProvider.MAIN_TABLE_ALIAS;
	}
}
