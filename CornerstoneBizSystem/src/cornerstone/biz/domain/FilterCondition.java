package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class FilterCondition {
	//
	public static final int TYPE_查询条件=0;
	public static final int TYPE_满足所有条件=1;//AND
	public static final int TYPE_满足任一条件=2;//OR
	//
	//前端设置的
	public static final int OPERATOR_等于=1;
	public static final int OPERATOR_不等于=2;
	public static final int OPERATOR_大于=3;
	public static final int OPERATOR_小于=4;
//	public static final int OPERATOR_包含=5;//前端没有
	public static final int OPERATOR_IS_NULL=10;
	public static final int OPERATOR_IS_NOT_NULL=11;
	
	//
	public int type;
	
	public int key;//t_project_field_define表的id
	
	public int operator;
	
	public List<Integer> intValueList;//
	
	public List<String> stringValueList;//ProjectFieldDefine里的复选框,单选框也用这个
	
	public String stringValue;
	
	public Integer intValue;
	
	public Date dateValue;//日期
	
	public List<FilterCondition> children;
}
