package cornerstone.biz.domain.query;

import jazmin.driver.jdbc.smartjdbc.Query;

/**
 * 
 * @author cs
 *
 */
public class BizQuery extends Query{
	//
	public static final int ORDER_TYPE_CREATE_TIME_DESC=100;
	public static final int ORDER_TYPE_CREATE_TIME_ASC=101;
	public static final int ORDER_TYPE_UPDATE_TIME_DESC=102;
	public static final int ORDER_TYPE_UPDATE_TIME_ASC=103;
	public static final int ORDER_TYPE_ID_DESC=104;
	public static final int ORDER_TYPE_ID_ASC=105;
	//
	public BizQuery() {
	}
}
