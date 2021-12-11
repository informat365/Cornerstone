package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class WikiPageSimpleInfo {

	public int id;

	public String uuid;

	public String name;

	public int type;

	public int status;
	//
	public static WikiPageSimpleInfo create(WikiPage bean) {
		WikiPageSimpleInfo info=new WikiPageSimpleInfo();
		info.id=bean.id;
		info.uuid=bean.uuid;
		info.name=bean.name;
		info.type=bean.type;
		info.status=bean.status;
		return info;
	}
}
