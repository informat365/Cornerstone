package cornerstone.biz.poi;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 一个Sheet
 * @author cs
 *
 */
public class TableData {

	public String sheetName;
	
	public List<String>headers;

	public List<Integer>columnWidth;
	
	public List<List<String>>contents;
	
	public List<CellRangeAddress> rangeAddresses;
	//
	public TableData() {
		headers=new ArrayList<String>();
		contents=new ArrayList<List<String>>();
		columnWidth=new ArrayList<>();
	}
	//
	@Override
	public String toString() {
		return "TableData [sheetName=" + sheetName + ", headers=" + headers + ", contents=" + contents + "]";
	}
	
}
