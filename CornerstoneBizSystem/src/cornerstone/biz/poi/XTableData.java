package cornerstone.biz.poi;

import java.util.List;

import cornerstone.biz.datatable.DataTableResult.XCell;
import cornerstone.biz.datatable.DataTableResult.XCol;

/**
 * 一个Sheet
 * @author cs
 *
 */
public class XTableData {

	public String sheetName;
	
	public List<XCol> col;
	
	public XCell[][] cells;
	
	public List<XCellRangeAddress> rangeAddresses;
	//
	public XTableData() {
	}
	
}
