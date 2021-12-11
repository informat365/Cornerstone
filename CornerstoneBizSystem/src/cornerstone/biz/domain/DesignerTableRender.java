package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class DesignerTableRender {

	public String tableName;//t_user
	
	public String domainName;//User
	
	public String author;
	
	public String createTime;
	
	public String packageName;//lzhbzl.biz.domain	
	
	public String displayName;//用户
	
	public boolean isShowDetailPage;
	
	public boolean isCanUpdate;
	
	public boolean isShowSelect;
	
	public boolean isFormInline;
	
	public boolean isSortable;
	
	public List<UniqueIndex> uniqueIndexs;//
	//
	public String listToString(List<String>list) {
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<list.size();i++) {
			sb.append("\""+list.get(i)+"\"");
			if(i<list.size()-1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	//
	public static class UniqueIndex{
		public String name;
		public List<String> domainNames;//userName
		//
		public UniqueIndex() {
			domainNames=new ArrayList<>();
		}
	}
}
