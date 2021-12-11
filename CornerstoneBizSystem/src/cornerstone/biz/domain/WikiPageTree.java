package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class WikiPageTree {

	public int id;

	public String name;

	public List<WikiPageTree> children;

	public int level;

	public int type;

	public int parentId;
	
	public int draftId;

	public boolean expand;

	public WikiPageTree() {
		children = new ArrayList<>();
	}
}
