package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class ProjectSimpleInfo {

	public int id;
	public String name;
	//
	public static ProjectSimpleInfo create(Project bean) {
		ProjectSimpleInfo info=new ProjectSimpleInfo();
		info.id=bean.id;
		info.name=bean.name;
		return info;
	}
}
