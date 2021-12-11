package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class SftpFile {
	//
	public static class Attrs {
		public long aTime;// ":1576225407,
		public String atimeString;// ":"Fri Dec 13 16:23:27 CST 2019",
		public boolean blk;// ":false,
		public boolean chr;// ":false,
		public boolean dir;// ":true,
		public Boolean fifo;// ":false,
		public long flags;// ":15,
		public long gId;// ":500,
		public boolean link;// ":false,
		public long mTime;// ":1551071020,
		public String mtimeString;// ":"Mon Feb 25 13:03:40 CST 2019",
		public long permissions;// ":16877,
		public String permissionsString;// ":"drwxr-xr-x",
		public boolean reg;// ":false,
		public long size;// ":4096,
		public boolean sock;// ":false,
		public long uId;// ":500
	}

	public Attrs attrs;
	public String filename;// "filename":"hadoop-2.7.3",
	public String longname;// "longname":"drwxr-xr-x 11 appadmin appadmin 4096 Feb 25 2019 hadoop-2.7.3"

}
