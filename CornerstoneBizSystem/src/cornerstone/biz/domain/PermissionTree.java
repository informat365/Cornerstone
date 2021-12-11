package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class PermissionTree {

	public String id;
	
	public String name;
	
	public List<Permission> children;
	
	public PermissionTree() {
		children=new ArrayList<>();
	}
}
