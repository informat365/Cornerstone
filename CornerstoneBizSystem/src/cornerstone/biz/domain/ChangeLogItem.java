package cornerstone.biz.domain;

import java.util.Date;

/**
 * 变更记录
 * @author cs
 *
 */
public class ChangeLogItem {
	//
	public String name;
	
	public String beforeContent;
	
	public String afterContent;
	
	public Date createTime;
	
	public ChangeLogItem() {
		createTime=new Date();
	}
}
