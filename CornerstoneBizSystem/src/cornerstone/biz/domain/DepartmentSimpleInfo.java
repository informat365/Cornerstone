package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class DepartmentSimpleInfo {

	public String id;
	public String name;
	public int companyId;
	//
	public static DepartmentSimpleInfo create(Department bean) {
		DepartmentSimpleInfo info=new DepartmentSimpleInfo();
		info.id=bean.id+"";
		info.name=bean.name;
		return info;
	}
}
