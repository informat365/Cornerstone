package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class TaskRemindRule {
	//
	public static final int TYPE_开始时间前=1;
	public static final int TYPE_开始时间后=2;
	public static final int TYPE_截止时间前=3;
	public static final int TYPE_截止时间后=4;
	public static final int TYPE_指定时间=5;
	//
	public static final int UNIT_分钟=1;
	public static final int UNIT_小时=2;
	public static final int UNIT_天=3;
	//
	public int type;
	
	public long value;
	
	public int unit;
}
