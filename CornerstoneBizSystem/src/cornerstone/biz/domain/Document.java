package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class Document {
	//
	public static final int TYPE_TASK=1;//task
	public static final int TYPE_WIKI=2;
	public static final int TYPE_FILE=3;
	///
	public int id;
	public String uuid;
	public String serialNo;
	public int projectId;
	public String projectName;
	public String projectUuid;
	public int type;
	public int objectType;
	public String name;
	public String createAccountName;
	public String updateTime;
}
