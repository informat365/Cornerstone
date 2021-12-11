package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class FileTree {
	
	public int id;

	public String uuid;
	
	public String name;
	
	public boolean isDirectory;
	
	public List<FileTree> children;
	//
	public FileTree() {
		children=new ArrayList<>();
	}
	//
	public static FileTree createFileTree(File file) {
		FileTree tree=new FileTree();
		tree.id=file.id;
		tree.uuid=file.uuid;
		tree.name=file.name;
		tree.isDirectory=file.isDirectory;
		return tree;
	}
}
