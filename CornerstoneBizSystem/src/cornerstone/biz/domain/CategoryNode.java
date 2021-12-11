package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class CategoryNode{

	public int id;
	
	public String name;
	
	public String remark;

	public String color;
	
	public int projectId;
    
    public int objectType;
	
	public int level;
	
	public int parentId;
	
	public boolean expand;
	
	public List<CategoryNode> children;
	
	public CategoryNode() {
		children=new ArrayList<>();
	}
}