package cornerstone.biz.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import cornerstone.biz.util.DumpUtil;
import jazmin.util.JSONUtil;

/**
 * 高级思维脑图
 * @author cs
 *
 */
public class SeniorMindMapTree {
	//
	public static class RootData{
		public String text;
		public String created;
		public String expandState;
	}
	//
	public static class SeniorData{
		public String id;
		public String text;
		public String created;
		public String background;
		public String expandState;
		public int priority;
		public int progress;
		public String note;
		@JSONField(name="font-weight")
		public String fontWeight;
		@JSONField(name="font-style")
		public String fontStyle;
		public String color;
		@JSONField(name="font-family")
		public String fontFamily;
		@JSONField(name="font-size")
		public int fontSize;
		public String hyperlink; 
		public String hyperlinkTitle;//": "",
		public String image;
		public String imageTitle;
		public SeniorSize imageSize;
	}
	//
	public static class SeniorSize{
		public int width;//": 48,
		public int height;//": 48
	}
	//
	public static class SeniorChildren{
		public SeniorData data;
		public List<SeniorChildren> children;
	}
	//
	public static class Root{
		public RootData data;
		public List<SeniorChildren> children;
	}
	//
	public String template;
	public String theme;
	public String version;
	public Root root;
	//
}
