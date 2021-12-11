package cornerstone.biz.datatable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;

/**
 * 
 * @author cs
 *
 */
public class DataTableResult {
	//
	public List<XSheet> sheets;
	//
	public String fileName;
	//
	public static class CellRange{
		public int x1;//:0,
		public int y1;//:1,
		public int x2;//:1,
		public int y2;//:5
	}
	//
	public static class Series{
		 public int index;//:0,
         public String name;//:'数量1',
         public String type;//: 'pie','line','bar'
         public boolean area;//:false
	}
	//
	public static class Axis{
		 public int index;//:1,
         public String type;//: 'category'
	}
	//
	public static class Chart{
		 public String title="";//:'图表标题',
         public int height=400;//:400,
         public boolean swapAxis;//:true,
         public CellRange cellRange=new CellRange();
         public Axis xAxis=new Axis();
         public Axis yAxis=new Axis();
         public List<Series> series=new ArrayList<>();
	}
	//
	public static class XRow{
		public List<XCell> cells; 
		//
		public XRow() {
			cells=new ArrayList<>();
		}
	}
	//
	public static class XCell{
		//
		public static final String DATA_TYPE_字符串="string";
		public static final String DATA_TYPE_数字="number";
		//
		public static final String TYPE_字符串="text";
		public static final String TYPE_输入框="input";
		public static final String TYPE_图表="chart";
		//
		public Object value;
		public String bgcolor;//backgroud color
		public String color;//font color
		public Integer fsize;//font size
		public Integer colspan;
		public Integer rowspan;
		public String align;//center 居中 right 居右 left居左（默认）
		public String dataType;//数据类型(string,number) 默认string
		public String type;//空 字符串,input 输入框,chart
		//
		public Integer row;//从1开始
		public Integer col;//从1开始
	}
	//
	public static class XCol{
		public int width;
	}
	//
	public static class XSheet{
		public String name;
		public List<XRow> header;
		public List<XRow> body;
		public List<XCol> col;
		//
		public XSheet() {
			header=new ArrayList<>();
			body=new ArrayList<>();
			col=new ArrayList<>();
		}
	}
	//
	public DataTableResult() {
		sheets=new ArrayList<>();
	}
	//
	public static void checkValid(DataTableResult result) {
		if(result.sheets==null) {
			result.sheets=new ArrayList<>();
		}
		if(result.sheets.isEmpty()) {
			throw new AppException("sheet不能为空");
		}
		Set<String> names=new HashSet<>();
		for (XSheet e : result.sheets) {
			if(StringUtil.isEmpty(e.name)) {
				throw new AppException("sheet名称不能为空");
			}
			if(names.contains(e.name)) {
				throw new AppException("sheet名称不能重复");
			}
			names.add(e.name);
		}
	}
}
