package cornerstone.biz.poi;

/**
 * 
 * @author cs
 *
 */
public class XCellRangeAddress {

	public int firstCol;
	
	public int firstRow;
	
	public int lastCol;
	
	public int lastRow;
	
	public XCellRangeAddress() {
		
	}
	
	public XCellRangeAddress(int firstRow,int lastRow,int firstCol,int lastCol) {
		this.firstRow=firstRow;
		this.lastRow=lastRow;
		this.firstCol=firstCol;
		this.lastCol=lastCol;
	}
}
