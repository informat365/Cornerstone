package cornerstone.biz.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 思维脑图
 * @author cs
 *
 */
public class MindMapTree {
	//
	public static class Meta{
		public String name;
		public String author;
		public String version;
	}
	//
	public static class Data{
		public String id;
		public String topic;
		public boolean expanded;
		@JSONField(name="background-color")
		public String backgroundColor;
		public List<Children> children;
	}
	//
	public static class Children{
		public String id;//":"5511f859bc0023d1", 如果id是task_开始 说明关联了task
		public String topic;//":"未命名节点",
		public boolean expanded;//":true,
		public String direction;//":"right"
		@JSONField(name="background-color")
		public String backgroundColor;
		public List<Children> children;
	}
	//
	public Meta meta;
	public String format;
	public Data data;
}
