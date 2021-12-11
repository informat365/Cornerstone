package cornerstone.biz.domain;

import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class GithubWebhook {

	public String event;
	
	public GithubWebhookBody body;
	
	public String reqBody;
	
	public String signature;
	
	public String delivery;
	
	//
	public static class GithubWebhookBody {
		//
		public static class GithubWebhookAuthor{
			public String name;
			public String email;
			public String username;
		}
		//
		public static class GithubWebhookProject{
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
		public static class GithubWebhookCommit{
			public String id;//954cf646bc3aaa132bd2675f5d93056eb67162b2
			public String tree_id;//4fdc1cdbb0c8e0ff3a21fe24cf809efd78e60ef7
			public Boolean distinct;//
			public String message;//#20407 test
			public String timestamp;//2018-10-26T07:01:26Z
			public String url;//http://134.175.144.232/root/test/commit/954cf646bc3aaa132bd2675f5d93056eb67162b2
			public GithubWebhookAuthor author; 
			public GithubWebhookCommitter committer;
			public List<String> added;
			public List<String> modified;
			public List<String> removed;
		}
		//
		public static class GithubWebhookCommitter{
			public String name;//GitHub
			public String email;//noreply@github.com
			public String username;//web-flow
		}
		//
		public static class GithubWebhookRepository{
			public String id;
			public String node_id;
			public String name;//test
			public String full_name;
			public String url;//git@134.175. 144.232:root/test.git
			public String html_url;
			public Boolean fork;
			public String forks_url;
			public String keys_url;
			public String pushed_at;
			public String description;
			public String homepage;//http://134.175.144.232/root/test
		}
		//
		public static class GithubWebhookPusher{
			public String name;
			public String email;
		}
		//
		public static class GithubWebhookSender{
			public String login;
			public String id;
			public String type;
		}
		//
		public String ref;//"refs/heads/master"
		
		public String before;
		
		public String after;
		
		public Boolean created;

		public Boolean deleted;
		
		public Boolean forced;
		
		public String compare;
		
		public List<GithubWebhookCommit> commits;
		
		public GithubWebhookCommit head_commit;
		
		public GithubWebhookRepository repository;
		
		public GithubWebhookPusher pusher;
		
		public GithubWebhookSender sender;
	}

}
