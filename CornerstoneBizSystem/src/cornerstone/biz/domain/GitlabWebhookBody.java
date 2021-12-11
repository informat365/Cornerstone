package cornerstone.biz.domain;

import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class GitlabWebhookBody {
	//
	public static class GitlabWebhookAuthor{
		public String name;
		public String email;
	}
	//
	public static class GitlabWebhookProject{
		public int id;
		public String name;
		public String description;
		public String web_url;//http://134.175.144.232/root/test
		public String avatar_url;//
		public String git_ssh_url;//git@134.175.144.232:root/test.git
		public String git_http_url;//http://134.175.144.232 /root/test.git
		public String namespace;//root
		public int visibility_level;
		public String path_with_namespace;//root/test
		public String default_branch;//master
		public String ci_config_path;//
		public String homepage;//http://134.175.144.232/root/test
		public String url;//git@134.175 .144.232:root/test.git
		public String ssh_url;//git@134.175.144.232:root/test.git
		public String http_url;//http://134.175.144.232/root/test.git
	}
	//
	public static class GitlabWebhookCommit{
		public String id;//954cf646bc3aaa132bd2675f5d93056eb67162b2
		public String message;//wrote a re adme and a learn_git file\n
		public String timestamp;//2018-10-26T07:01:26Z
		public String url;//http://134.175.144.232/root/test/commit/954cf646bc3aaa132bd2675f5d93056eb67162b2
		public GitlabWebhookAuthor author; 
		public List<String> added;
		public List<String> modified;
		public List<String> removed;
	}
	//
	public static class GitlabWebhookRepository{
		public String name;//test
		public String url;//git@134.175. 144.232:root/test.git
		public String description;
		public String homepage;//http://134.175.144.232/root/test
		public String git_http_url;//http://134.175.144.232/root/test.git
		public String git_ssh_url;//git@134.175.144.232:root/test.git
		public int visibility_level;//0
	}
	//
	public String object_kind;//push
	
	public String event_name;//push
	
	public String before;
	
	public String after;
	
	public String ref;//"refs/heads/master",
	
	public String checkout_sha;//bd94f74117433c9ace9522d23e815a1d9df98a2f
	
	public String message;//null
	
	public int user_id;//1
	
	public String user_name;//Administrator
	
	public String user_username;//root
	
	public String user_email;//admin@example.com
	
	public String user_avatar;//https://www.gravatar.com/avatar/e64c7d89f26bd1972efa854d13d7dd61?s=80\u0026d=identicon
	
	public int project_id;//1 
	
	public GitlabWebhookProject project;
	
	public List<GitlabWebhookCommit> commits;
	
	public int total_commits_count;
	
	public GitlabWebhookRepository repository;
}
