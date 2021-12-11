package cornerstone.biz.domain;

/**
 * 上传文件
 * @author cs
 *
 */
public class UploadFileResult {
	//
	/**是否成功*/
	public boolean success;
	
	/**文件路径*/
	public String file_path;
	
	/**完全路径 http://sssssssss/sssssss.png*/
	public String full_path;
	
	/**附件*/
	public Attachment attachment;
	
	public String reverse;

	public long size;
	
}
