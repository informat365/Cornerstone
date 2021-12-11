package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class DirectoryNode {

	public int id;
	
	public String title;
	
	public List<DirectoryNode> children;
	
	public boolean isDirectory;
	
	public int level;
	
	public int parentId;
	
	public boolean expand;

	public String color;

	public int nodeKey;

	public boolean isDelete;
	
	public String path;
	
	public DirectoryNode() {
		children=new ArrayList<>();
	}
}