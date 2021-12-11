package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class DepartmentTree {
	//
	public int id;
	
	public String title;
	
	public String pinyinName;
	
	public String userName;
	
	public List<DepartmentTree> children;
	
	public int level;
	
	public int type;
	
	public int parentId;
	
	public int accountId;
	
	public boolean expand;
	
	public DepartmentTree() {
		children=new ArrayList<>();
	}
}
