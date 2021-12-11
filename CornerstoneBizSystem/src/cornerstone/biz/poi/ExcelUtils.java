package cornerstone.biz.poi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jazmin.core.app.AppException;
import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegend;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarkerStyle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPie3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarGrouping;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarGrouping.Enum;
import org.openxmlformats.schemas.drawingml.x2006.chart.STCrossBetween;
import org.openxmlformats.schemas.drawingml.x2006.chart.STCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDispBlanksAs;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGrouping;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLblAlgn;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLegendPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.STMarkerStyle;
import org.openxmlformats.schemas.drawingml.x2006.chart.STOrientation;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickLblPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickMark;

import cornerstone.biz.datatable.DataTableResult;
import cornerstone.biz.datatable.DataTableResult.Series;
import cornerstone.biz.datatable.DataTableResult.XCell;
import cornerstone.biz.datatable.DataTableResult.XCol;
import cornerstone.biz.datatable.DataTableResult.XRow;
import cornerstone.biz.datatable.DataTableResult.XSheet;
import cornerstone.biz.util.DumpUtil;
import cornerstone.biz.util.StringUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 *
 */
public class ExcelUtils {
	//
	private static Logger logger = LoggerFactory.get(ExcelUtils.class);
	//
	private static final Class<?>[] supportFieldClasses = { int.class, Integer.class, long.class, Long.class,
			short.class, Short.class, byte.class, Byte.class, boolean.class, Boolean.class, float.class, Float.class,
			double.class, Double.class, Date.class, String.class };

	//
	public static class Coordinate {
		public int x;
		public int y;

		//
		public Coordinate() {

		}

		//
		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * 
	 * @param wb
	 * @param sheetName
	 * @param list
	 */
	public static <T> void writeExcel(XSSFWorkbook wb, String sheetName,Class<T> clazz, List<T> list, List<String> excludeFields) {
		writeExcel0(wb, sheetName, clazz, list, excludeFields);
	}

	private static <T> void writeExcel0(XSSFWorkbook wb, String sheetName,Class<T> clazz, List<T> list, List<String> excludeFields) {
		try {
			Map<String, Field> fieldMap = new HashMap<String, Field>();
			List<String> titles = new ArrayList<>();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if(excludeFields!=null&&excludeFields.contains(field.getName())) {//排除
					continue;
				}
				if (field.isAnnotationPresent(ExcelCell.class)) {
					checkFieldClass(field.getType());
					ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
					fieldMap.put(excelCell.name(), field);
					titles.add(excelCell.name());
				}
			}
			//
			Sheet sheet = null;
			if (sheetName == null) {
				sheet = wb.createSheet();
			} else {
				sheet = wb.createSheet(sheetName);
			}
			// set header
			Row titleRow = sheet.createRow(0);
			for (int i = 0; i < titles.size(); i++) {
				Cell cell = titleRow.createCell(i);
				cell.setCellValue(titles.get(i));
			}
			//
			if(list!=null) {
				int totalRowCount = list.size();
				for (int r = 0; r < totalRowCount; r++) {
					T bean = list.get(r);
					Row row = sheet.createRow(r + 1);
					for (int c = 0; c < titles.size(); c++) {
						Cell cell = row.createCell(c);
						String title = titles.get(c);
						Field field = fieldMap.get(title);
						field.setAccessible(true);
						Object val = field.get(bean);
						if (val == null) {
							continue;
						}
						ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
						if (excelCell.hyperLink() == true) {
							cell.setCellFormula("HYPERLINK(\"" + val.toString() + "\",\"" + val.toString() + "\")");
						} else if (field.getType() == Date.class) {
							cell.setCellValue(new SimpleDateFormat(excelCell.dateFormat()).format((Date) val));
						} else {
							cell.setCellValue(val.toString());
						}
					}
				}
			}
		} catch (Exception e) {
			throw new POIXMLException(e.getMessage(), e);
		}

	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	public static <T> ByteArrayOutputStream writeExcel(Class<T> clazz, List<T> list) throws IOException {
		return writeExcel(null, clazz, list);
	}

	/**
	 * 
	 * @param list
	 * @param file
	 *
	 */
	public static <T> void writeExcelToFile(Class<T> clazz, List<T> list, File file) throws IOException {
		writeExcelToFile(null, clazz, list, file);
	}

	/**
	 * 
	 * @param list
	 * @param file
	 *
	 */
	public static <T> void writeExcelToFile(String sheetName,Class<T> clazz, List<T> list, File file) throws IOException {
		ByteArrayOutputStream bos = writeExcel(sheetName,clazz, list);
		if (bos == null) {
			throw new AppException("no data to write");
		}
		try (OutputStream outputStream = new FileOutputStream(file)) {
			bos.writeTo(outputStream);
		}
	}

	private static void checkFieldClass(Class<?> clazz) {
		for (Class<?> supprtClass : supportFieldClasses) {
			if (supprtClass == clazz) {
				return;
			}
		}
		throw new AppException("Unsupport Field Class " + clazz.getSimpleName());
	}

	/**
	 * 
	 * @param sheetName
	 * @param list
	 * @param file
	 * @param startRow
	 * @param startCol
	 *
	 */
	public static <T> void writeMoreSheetExcelToFile(String sheetName, List<T> list, File file, int startRow,
			int startCol)  {
		if (list == null || list.size() == 0) {
			return;
		}
		Workbook wb = null;
		FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
		try {
            if (!file.exists()) {
                wb = new XSSFWorkbook();
            } else {
                fis = new FileInputStream(file);
                wb = new XSSFWorkbook(fis);
            }
            T test = list.get(0);
            Map<String, Field> fieldMap = new HashMap<String, Field>();
            List<String> titles = new ArrayList<>();
            Field[] fields = test.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ExcelCell.class)) {
                    checkFieldClass(field.getType());
                    ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
                    fieldMap.put(excelCell.name(), field);
                    titles.add(excelCell.name());
                }
            }
            if (fieldMap.size() != titles.size()) {
                wb.close();
                throw new IllegalArgumentException("@ExcelCell name cannot same");
            }
            //
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                sheet = wb.createSheet(sheetName);
            }
            // set header
            Row titleRow = sheet.createRow(startRow);
            for (int i = 0; i < titles.size(); i++) {
                Cell cell = titleRow.createCell(i + startCol);
                cell.setCellValue(titles.get(i));
            }
            //
            int totalRowCount = list.size();
            for (int r = 0; r < totalRowCount; r++) {
                T bean = list.get(r);
                Row row = sheet.createRow(r + 1 + startRow);
                for (int c = 0; c < titles.size(); c++) {
                    Cell cell = row.createCell(c + startCol);
                    String title = titles.get(c);
                    Field field = fieldMap.get(title);
                    field.setAccessible(true);
                    Object val = field.get(bean);
                    if (val == null) {
                        continue;
                    }
                    ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
                    if (field.getType() == Date.class) {
                        cell.setCellValue(new SimpleDateFormat(excelCell.dateFormat()).format((Date) val));
                    } else {
                        cell.setCellValue(val.toString());
                    }
                }
            }
             bos = new ByteArrayOutputStream();
            wb.write(bos);
            bos.close();
            try (OutputStream outputStream = new FileOutputStream(file)) {
                bos.writeTo(outputStream);
            }
        }catch (Exception e){
            logger.error("write excel ERR",e);
        }finally {
		    if(null!=bos){
                try {
                    bos.close();
                } catch (IOException e) {
                    logger.error("write excel ERR",e);
                }
            }
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    logger.error("write excel ERR",e);
                }
            }
            if(null!=fis){
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("write excel ERR",e);
                }
            }
        }


	}

	/**
	 * 
	 * @param sheetName
	 * @param list
	 * @return
	 *
	 */
	public static <T> ByteArrayOutputStream writeExcel(String sheetName,Class<T> clazz, List<T> list) throws IOException {
		if (list == null || list.size() == 0) {
			return null;
		}
		try (XSSFWorkbook wb = new XSSFWorkbook()) {
			writeExcel0(wb, sheetName,clazz, list,null);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);
			bos.close();
			return bos;
		}
	}

	//
	private static void createRow(XSSFSheet sheet, int rowIndex, List<String> values,boolean isHeader) {
		XSSFRow row = sheet.createRow(rowIndex++);
		if (values == null) {
			return;
		}
		for (int i = 0; i < values.size(); i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(values.get(i));
			//
			if (isHeader) {//20200208
				XSSFCellStyle cellStyle = sheet.getWorkbook().createCellStyle();
				XSSFColor bgcolor = new XSSFColor();
				cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				bgcolor.setARGBHex("FFEEEEEE");
				cellStyle.setFillForegroundColor(bgcolor);

				cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
				cellStyle.setBorderLeft(BorderStyle.THIN);//左边框
				cellStyle.setBorderTop(BorderStyle.THIN);//上边框
				cellStyle.setBorderRight(BorderStyle.THIN);//右边框


				cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中

				cell.setCellStyle(cellStyle);
			}else{
				XSSFCellStyle cellStyle = sheet.getWorkbook().createCellStyle();
				cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
				cellStyle.setBorderLeft(BorderStyle.THIN);//左边框
				cellStyle.setBorderTop(BorderStyle.THIN);//上边框
				cellStyle.setBorderRight(BorderStyle.THIN);//右边框
				cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
				cell.setCellStyle(cellStyle);
			}
		}
	}

	private static void createXRow(XSSFSheet sheet, int rowIndex,XCell[][] cells,XCell[] values) {
		XSSFRow row = sheet.createRow(rowIndex++);
		if (values == null) {
			return;
		}
		for (int i = 0; i < values.length; i++) {
			XSSFCell cell = row.createCell(i);
			XCell xcell = values[i];
			if (xcell == null) {
				continue;
			}
			XSSFCellStyle cellStyle = sheet.getWorkbook().createCellStyle();
			if (!StringUtil.isEmpty(xcell.align)) {
				if ("center".equals(xcell.align)) {
					cellStyle.setAlignment(HorizontalAlignment.CENTER);
				}
				if ("left".equals(xcell.align)) {
					cellStyle.setAlignment(HorizontalAlignment.LEFT);
				}
				if ("right".equals(xcell.align)) {
					cellStyle.setAlignment(HorizontalAlignment.RIGHT);
				}
			}
			int fsize = 12;
			String fColor = "000000";
			if (xcell.fsize != null) {
				fsize = xcell.fsize;
			}
			if (xcell.color != null) {
				fColor = xcell.color;
			}
			//
			XSSFFont font = sheet.getWorkbook().createFont();
			XSSFColor fontColor = new XSSFColor();
			fontColor.setARGBHex("FF" + fColor);
			font.setColor(fontColor);
			font.setFontHeightInPoints((short) fsize);// 设置字体大小
			cellStyle.setFont(font);
			//
			cellStyle.setBorderBottom(BorderStyle.THIN);
			// 下边框
			cellStyle.setBorderLeft(BorderStyle.THIN);// 左边框
			cellStyle.setBorderTop(BorderStyle.THIN);// 上边框
			cellStyle.setBorderRight(BorderStyle.THIN);// 右边框
			//
			if (xcell.bgcolor != null) {
				XSSFColor bgcolor = new XSSFColor();
				cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				bgcolor.setARGBHex("FF" + xcell.bgcolor);
				cellStyle.setFillForegroundColor(bgcolor);
			}
			cell.setCellStyle(cellStyle);
			if(xcell.dataType!=null&&xcell.dataType.equals(XCell.DATA_TYPE_数字)) {
				cell.setCellType(CellType.NUMERIC);
			}
			if(values[i].value!=null) {
				if(xcell.dataType!=null&&xcell.dataType.equals(XCell.DATA_TYPE_数字)) {
					cell.setCellValue(Double.valueOf(values[i].value.toString()));
				}else {
					cell.setCellValue(values[i].value.toString());
				}
			}
			//如果是图表
			if(values[i].value!=null&&xcell.type!=null&&xcell.type.equals(XCell.TYPE_图表)) {
				try {
					cornerstone.biz.datatable.DataTableResult.Chart chart=JSONUtil.fromJson(values[i].value.toString(), cornerstone.biz.datatable.DataTableResult.Chart.class);
					if(chart!=null&&chart.cellRange!=null&&chart.series!=null&&chart.series.size()>0) {
						Series series=chart.series.get(0);
						List<String> titleArr = new ArrayList<String>();// 标题
						List<String> fldNameArr=new ArrayList<>();
						 List<Map<String,Object>> dataList=new ArrayList<>();//数据
						int xrow=0;
						for(int xcol=chart.cellRange.x1;xcol<=chart.cellRange.x2;xcol++) {
							XCell titleCell=getXCell(cells, xrow, xcol);
							if(titleCell!=null) {
								titleArr.add(titleCell.value.toString());
							}else {
								titleArr.add("");//no title
							}
						}
						for(int r=1;r<=chart.cellRange.y2+1;r++) {
							Map<String,Object> dataMap=new LinkedHashMap<>();
							for(int c=0;c<=chart.cellRange.x2;c++) {
								dataMap.put("value"+c, getXCellValue(cells, r, c));
							}
							dataList.add(dataMap);
						}
						for(int v=0;v<=chart.cellRange.x2;v++) {
							fldNameArr.add("value"+v);
						}
						int width=4;//图标占几列
						if(dataList.size()>10) {
							width=dataList.size()/2;
						}
						createChart(false,sheet.getWorkbook(), sheet, 0, series.type, 
								STBarGrouping.STACKED, false, false, dataList, 
								fldNameArr, titleArr, null, null,width);
					}	
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}
	
	private static XCell getXCell(XCell[][] values,int row,int col) {
		if(values.length<=row) {
			return null;
		}
		if(values[row].length<=col) {
			return null;
		}
		XCell cell=values[row][col];
		return cell;
	}
	
	private static Object getXCellValue(XCell[][] values,int row,int col) {
		if(values.length<=row) {
			return "";
		}
		if(values[row].length<=col) {
			return "";
		}
		XCell cell=values[row][col];
		if(cell==null) {
			return "";
		}
		return cell.value;
	}

	/**
	 * 
	 * @param datas
	 * @return
	 * @throws IOException
	 */
	public static ByteArrayOutputStream writeSheetsExcel(List<XTableData> datas) throws IOException {
		try (XSSFWorkbook wb = new XSSFWorkbook()) {
			for (XTableData data : datas) {
				writeXExcel0(wb, data);
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);
			bos.close();
			return bos;
		}
	}

	/**
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static ByteArrayOutputStream writeExcel(TableData data) throws IOException {
		try (XSSFWorkbook wb = new XSSFWorkbook()) {
			writeExcel0(wb, data);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);
			bos.close();
			return bos;
		}
	}

	/**
	 * 
	 * @param wb
	 * @param data
	 * @throws IOException
	 */
	private static void writeExcel0(XSSFWorkbook wb, TableData data) throws IOException {
		XSSFSheet sheet = wb.createSheet(data.sheetName);
		if (data.rangeAddresses != null) {
			for (CellRangeAddress e : data.rangeAddresses) {
				sheet.addMergedRegion(e);
			}
		}
		if (data.columnWidth != null) {
			int columnIndex = 0;
			for (int i = 0; i < data.columnWidth.size(); i++) {
				Integer width = data.columnWidth.get(i);
				if (width > 0) {
					sheet.setColumnWidth(columnIndex, width * 50);
				}
				columnIndex++;
			}
		}
		int rowIndex = 0;
		if (data.headers != null && (!data.headers.isEmpty())) {
			createRow(sheet, rowIndex++, data.headers,true);
		}
		if (data.contents != null && (!data.contents.isEmpty())) {
			for (List<String> contents : data.contents) {
				createRow(sheet, rowIndex++, contents,false);
			}
		}
	}

	private static void writeXExcel0(XSSFWorkbook wb, XTableData data) throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("writeXExcel0 data:{}", JSONUtil.toJson(data));
		}
		XSSFSheet sheet = wb.createSheet(data.sheetName);
		if (data.rangeAddresses != null) {
			for (XCellRangeAddress e : data.rangeAddresses) {
				sheet.addMergedRegion(new CellRangeAddress(e.firstRow, e.lastRow, e.firstCol, e.lastCol));
			}
		}
		if (data.col != null) {
			int columnIndex = 0;
			for (XCol col : data.col) {
				if (col.width > 0) {
					sheet.setColumnWidth(columnIndex, col.width * 50);
				}
				columnIndex++;
			}
		}
		int rowIndex = 0;
		for (int i = 0; i < data.cells.length; i++) {
			createXRow(sheet, rowIndex++, data.cells,data.cells[i]);
		}
	}

	/**
	 * 
	 * @param xlsxFile
	 * @param imageData
	 * @param col1
	 * @param row1
	 * @param col2
	 * @param row2
	 * @throws IOException
	 */
	public static void addPicture(File xlsxFile, byte[] imageData, int col1, int row1, int col2, int row2) {
        FileOutputStream fileOut = null;
        try {
            try (XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(xlsxFile))) {
                XSSFSheet sheet = wb.getSheetAt(0);
                XSSFDrawing drawing = sheet.createDrawingPatriarch();
                XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, col1, row1, col2, row2);
                drawing.createPicture(anchor, wb.addPicture(imageData, XSSFWorkbook.PICTURE_TYPE_JPEG));
                 fileOut = new FileOutputStream(xlsxFile);
                wb.write(fileOut);

            }
        } catch (IOException e) {
          logger.error("addPicture ERR",e);
        }finally {
            if(null!=fileOut){
                try {
                    fileOut.close();
                } catch (IOException e) {
                    logger.error("addPicture ERR",e);
                }
            }
        }
    }

	/**
	 * 
	 * @param clazz
	 * @param in
	 * @return
	 *
	 */
	public static <T> List<T> readExcel(Class<T> clazz, InputStream in) throws IOException, IllegalAccessException, InstantiationException, ParseException {
		try (XSSFWorkbook wb = new XSSFWorkbook(in)) {
			if (wb.getNumberOfSheets() <= 0) {
				return new ArrayList<>();
			}
			XSSFSheet sheet = wb.getSheetAt(0);
			//
			List<T> result = new ArrayList<T>(sheet.getLastRowNum() - 1);
			Row row = sheet.getRow(sheet.getFirstRowNum());
			//
			Map<String, Field> fieldMap = new HashMap<String, Field>();
			Map<String, String> titleMap = new HashMap<String, String>();
			//
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(ExcelCell.class)) {
					ExcelCell mapperCell = field.getAnnotation(ExcelCell.class);
					fieldMap.put(mapperCell.name(), field);
				}
			}

			for (Cell title : row) {
				CellReference cellRef = new CellReference(title);
				titleMap.put(cellRef.getCellRefParts()[2], title.getRichStringCellValue().getString());
			}

			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				T t = clazz.newInstance();
				Row dataRow = sheet.getRow(i);
				for (Cell data : dataRow) {
					if(null==data){
						continue;
					}
					CellReference cellRef = new CellReference(data);
					String cellTag = cellRef.getCellRefParts()[2];
					String name = titleMap.get(cellTag);
					Field field = fieldMap.get(name);
					if (null != field) {
						field.setAccessible(true);
						setFiedlValue(data, t, field);
					}
				}
				result.add(t);
			}
			//
			return result;
		}
	}

	//
	public static TableData readExcel(InputStream in) throws IOException {
		return readExcel(in, 0);
	}

	/**
	 * 
	 * @param in
	 * @param sheetIndex 从0开始
	 * @return
	 *
	 */
	public static TableData readExcel(InputStream in, int sheetIndex) throws IOException {
		TableData data = new TableData();
		try (XSSFWorkbook wb = new XSSFWorkbook(in)) {
			if (wb.getNumberOfSheets() <= sheetIndex) {
				return data;
			}
			XSSFSheet sheet = wb.getSheetAt(sheetIndex);
			logger.info("sheet lastRowNum:{}",sheet.getLastRowNum());
			data.sheetName = sheet.getSheetName();
			if (sheet.getLastRowNum() <= 0) {
				return data;
			}
			XSSFRow row = sheet.getRow(sheet.getFirstRowNum());
			for (int i = 0; i <= row.getLastCellNum(); i++) {
				if (i < row.getFirstCellNum()) {
					data.headers.add("");
				} else {
					XSSFCell cell = row.getCell(i);
					data.headers.add(getCellContent(cell));
				}
			}
			//
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				List<String> cc = new ArrayList<String>();
				data.contents.add(cc);
				XSSFRow rr = sheet.getRow(i);
				if (rr == null) {
					continue;
				}
				for (int j = 0; j <= rr.getLastCellNum(); j++) {
					if (j < 0) {
						continue;
					}
					if (j < rr.getFirstCellNum()) {
						cc.add("");
					} else {
						XSSFCell cell = rr.getCell(j);
						cc.add(getCellContent(cell));
					}
				}
			}
		}
		return data;
	}

	//
	@SuppressWarnings("deprecation")
	private static String getCellContent(Cell cell) {
		if (cell != null) {
			if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
				return (cell.getBooleanCellValue() + "");
			}
			if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
					SimpleDateFormat sdf = null;
					if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
						sdf = new SimpleDateFormat("HH:mm");
					} else {// 日期
						sdf = new SimpleDateFormat("yyyy-MM-dd");
					}
					Date date = cell.getDateCellValue();
					return sdf.format(date);
				} else if (cell.getCellStyle().getDataFormat() == 58) {
					// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					double value = cell.getNumericCellValue();
					Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
					return sdf.format(date);
				}
				DecimalFormat df = new DecimalFormat("0");
				return (df.format(cell.getNumericCellValue()));
			}
			if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				return (cell.getStringCellValue() + "");
			}
		}
		return "";
	}

	//
	private static void setFiedlValue(Cell cell, Object o, Field field) throws IllegalAccessException, ParseException {
		String content = getCellContent(cell);
		if (content == null || content.length() == 0) {
			return;
		}
		//
		Class<?> filedClass = field.getType();
		if (filedClass == int.class || filedClass == Integer.class) {
			field.set(o, Integer.valueOf(content));
		} else if (filedClass == short.class || filedClass == Short.class) {
			field.set(o, Short.valueOf(content));
		} else if (filedClass == float.class || filedClass == Float.class) {
			field.set(o, Float.valueOf(content));
		} else if (filedClass == double.class || filedClass == Double.class) {
			field.set(o, Double.valueOf(content));
		} else if (filedClass == byte.class || filedClass == Byte.class) {
			field.set(o, Byte.valueOf(content));
		} else if (filedClass == boolean.class || filedClass == Boolean.class) {
			field.set(o, Boolean.valueOf(content));
		} else if (filedClass == Date.class) {
			ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
			field.set(o, new SimpleDateFormat(excelCell.dateFormat()).parse(content));
		} else if (filedClass == String.class) {
			field.set(o, content);
		}
	}

	//
	//
	public static List<XTableData> createTableDatas(DataTableResult table) {
		List<XTableData> list = new ArrayList<>();
		if (table.sheets == null || table.sheets.isEmpty()) {
			return list;
		}
		for (XSheet sheet : table.sheets) {
			list.add(createTableData(sheet));
		}
		return list;
	}

	//
	private static void addValue(Map<Integer, Integer> map, Integer key, int add) {
		Integer value = map.get(key);
		if (value == null) {
			value = 0;
		}
		map.put(key, value + add);
	}

	//
	private static int calcMaxCol(int maxCol, Map<Integer, Integer> rowColsNumMap, int rowIndex, List<XRow> rows) {
		if (rows == null) {
			return maxCol;
		}
		for (XRow row : rows) {
			if (row.cells == null) {
				continue;
			}
			int colIndex = 0;
			// 上面行对我这行的影响
			Integer addColIndex = rowColsNumMap.get(rowIndex);
			if (addColIndex == null) {
				addColIndex = 0;
			}
			for (XCell cell : row.cells) {
				if (cell.rowspan == null || cell.rowspan < 1) {
					cell.rowspan = 1;
				}
				if (cell.colspan == null || cell.colspan < 1) {
					cell.colspan = 1;
				}
				colIndex += cell.colspan;
				//
				if (cell.rowspan > 1) {
					for (int r = 1; r < cell.rowspan; r++) {// 对下面行的影响
						addValue(rowColsNumMap, rowIndex + r, cell.colspan);
					}
				}
			}
			colIndex += addColIndex;
			if (colIndex > maxCol) {
				maxCol = colIndex;
			}
			rowIndex++;
		}
		return maxCol;
	}

	//
	private static void setupCellRowCol(XCell[][] cells, List<XCellRangeAddress> rangeAddresses, int rowIndex,
			List<XRow> rows) {
		if (rows == null) {
			return;
		}
		for (XRow row : rows) {
			if (row.cells == null) {
				continue;
			}
			XCell[] content = cells[rowIndex - 1];
			int colIndex = 1;
			for (XCell cell : row.cells) {
				cell.row = rowIndex;
				// 找到这行的空位
				for (int i = colIndex; i <= content.length; i++) {
					if (content[i - 1] == null) {
						cell.col = i;
						content[i - 1] = cell;
						colIndex = i;
						for (int j = 1; j < cell.colspan; j++) {// 补上
							content[i - 1 + j] = new XCell();
						}
						break;
					}
				}
				colIndex += cell.colspan;
				// 对下面行的影响
				if (cell.rowspan > 1) {
					for (int i = 0; i < cell.colspan; i++) {
						for (int r = rowIndex + 1; r <= rowIndex + cell.rowspan - 1; r++) {
							cells[r - 1][cell.col - 1 + i] = new XCell();
						}
					}
				}
				// 合并单元格
				if (cell.rowspan > 1 || cell.colspan > 1) {
					rangeAddresses.add(new XCellRangeAddress(cell.row - 1, cell.row + cell.rowspan - 2, cell.col - 1,
							cell.col + cell.colspan - 2));
				}
			}
			rowIndex++;
		}
	}

	//
	private static void dumpXCells(XCell[][] cells) {
		if (!logger.isDebugEnabled()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (cells[i][j] != null) {
					sb.append(cells[i][j].value + ",");
				} else {
					sb.append(",");
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("\n");
		}
		logger.info("dumpXCells XCells:" + sb.toString());
	}

	//
	private static XTableData createTableData(XSheet sheet) {
		XTableData data = new XTableData();
		data.sheetName = sheet.name;
		data.col = sheet.col;
		int maxRowCount = 0;
		int maxColCount = 0;
		Map<Integer, Integer> rowColsNumMap = new HashMap<>();// <行号,列数>行号从1开始
		if (sheet.header != null) {
			maxRowCount += sheet.header.size();
		}
		if (sheet.body != null) {
			maxRowCount += sheet.body.size();
		}
		maxColCount = calcMaxCol(maxColCount, rowColsNumMap, 1, sheet.header);
		maxColCount = calcMaxCol(maxColCount, rowColsNumMap, 1 + sheet.header.size(), sheet.body);
		if (maxRowCount == 0 || maxColCount == 0) {
			logger.warn("maxRowCount:{} ==0||maxColCount:{}==0", maxRowCount, maxColCount);
			return data;
		}
		//
		logger.info("maxRowCount:{} maxColCount:{}", maxRowCount, maxColCount);
		XCell[][] cells = new XCell[maxRowCount][maxColCount];
		List<XCellRangeAddress> rangeAddresses = new ArrayList<>();
		setupCellRowCol(cells, rangeAddresses, 1, sheet.header);
		setupCellRowCol(cells, rangeAddresses, sheet.header.size() + 1, sheet.body);
		dumpXCells(cells);
		data.rangeAddresses = rangeAddresses;
		data.cells = cells;
		//
		return data;
	}
	//
	/**
	 * @Description: 创建Excel数据
	 * @param wb:工作薄
	 * @param sheet：wb.createSheet();
	 * @param dataList
	 * @param fldNameArr
	 * @param titleArr
	 * @param showtailArr
	 * @param ispercentArr
	 * @param position:从第几行开始(0：就是第一行)
	 * @return boolean
	 */
	public static boolean refreshChartExcel(XSSFWorkbook wb, XSSFSheet sheet, List<Map<String,Object>> dataList,
			List<String> fldNameArr, List<String> titleArr, List<String> showtailArr, List<String> ispercentArr,
			int position) {
		boolean result = true;
		// 样式准备
		CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN); // 下边框
		style.setBorderLeft(BorderStyle.THIN);// 左边框
		style.setBorderTop(BorderStyle.THIN);// 上边框
		style.setBorderRight(BorderStyle.THIN);// 右边框
		style.setAlignment(HorizontalAlignment.CENTER);

		CellStyle style1 = wb.createCellStyle();
		style1.setBorderBottom(BorderStyle.THIN); // 下边框
		style1.setBorderLeft(BorderStyle.THIN);// 左边框
		style1.setBorderTop(BorderStyle.THIN);// 上边框
		style1.setBorderRight(BorderStyle.THIN);// 右边框
		style1.setAlignment(HorizontalAlignment.CENTER);

		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setBorderTop(BorderStyle.THIN);// 上边框
		cellStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		cellStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		cellStyle.setBorderRight(BorderStyle.THIN);// 右边框
		cellStyle.setAlignment(HorizontalAlignment.CENTER);// 水平对齐方式
		// cellStyle.setVerticalAlignment(VerticalAlignment.TOP);//垂直对齐方式

		// 根据数据创建excel第一行标题行
		for (int i = 0; i < titleArr.size(); i++) {
			if (sheet.getRow(position) == null) {
				sheet.createRow(position).createCell(i).setCellValue(titleArr.get(i) == null ? "" : titleArr.get(i));

			} else {
				sheet.getRow(position).createCell(i).setCellValue(titleArr.get(i) == null ? "" : titleArr.get(i));
			}
			// 标题行创建背景颜色
			sheet.getRow(position).getCell(i).setCellStyle(style);
		}

		// 遍历数据行
		for (int i = 0; i < dataList.size(); i++) {
			Map<String,Object> baseFormMap = dataList.get(i);// 数据行
			// fldNameArr字段属性
			for (int j = 0; j < fldNameArr.size(); j++) {
				if (sheet.getRow(position + i + 1) == null) {
					if (j == 0) {
						try {
							sheet.createRow(position + i + 1).createCell(j)
									.setCellValue(((String) baseFormMap.get(fldNameArr.get(j))) == null ? ""
											: ((String) baseFormMap.get(fldNameArr.get(j))));
						} catch (Exception e) {
							if ((String) baseFormMap.get(fldNameArr.get(j)) == null) {
								sheet.createRow(position + i + 1).createCell(j).setCellValue("");
							} else {
								sheet.createRow(position + i + 1).createCell(j)
										.setCellValue((Date) baseFormMap.get(fldNameArr.get(j)));
							}
						}
					}
					// 标题行创建背景颜色
					sheet.getRow(position + i + 1).getCell(j).setCellStyle(style1);
				} else {
					BigDecimal b = (BigDecimal) baseFormMap.get(fldNameArr.get(j));
					double value = 0d;
					if (b != null) {
						value = b.doubleValue();
					}
					if (value == 0) {
						sheet.getRow(position + i + 1).createCell(j);
					} else {
						sheet.getRow(position + i + 1).createCell(j)
								.setCellValue(((BigDecimal) baseFormMap.get(fldNameArr.get(j))).doubleValue());
					}
					if ("1".equals(ispercentArr.get(j))) {// 是否设置百分比
						// 设置Y轴的数字为百分比样式显示
						StringBuilder sb = new StringBuilder();

						if ("0".equals(showtailArr.get(j))) {// 保留几位小数
							sb.append("0");
							if ("1".equals(ispercentArr.get(j))) {// 是否百分比
								sb.append("%");
							}
						} else {
							sb.append("0.");
							for (int k = 0; k < Integer.parseInt(showtailArr.get(j)); k++) {
								sb.append("0");
							}
							if ("1".equals(ispercentArr.get(j))) {// 是否百分比
								sb.append("%");
							}
						}
						cellStyle.setDataFormat(wb.createDataFormat().getFormat(sb.toString()));
						sheet.getRow(position + i + 1).getCell(j).setCellStyle(cellStyle);
					} else {
						// 是否设置百分比
						// 设置Y轴的数字为百分比样式显示
						StringBuilder sb = new StringBuilder();

						if ("0".equals(showtailArr.get(j))) {// 保留几位小数
							sb.append("0");
						} else {
							sb.append("0.");
							for (int k = 0; k < Integer.parseInt(showtailArr.get(j)); k++) {
								sb.append("0");
							}
						}
						cellStyle.setDataFormat(wb.createDataFormat().getFormat(sb.toString()));
						sheet.getRow(position + i + 1).getCell(j).setCellStyle(cellStyle);
					}
				}
			}

		}
		return result;
	}

	/**
	 * @Description:创建动态图
	 * @param wb
	 * @param sheet
	 * @param curRow:当前行号
	 * @param type:图类型
	 * @param group:柱状图类型
	 * 			@see STBarGrouping
	 * @param isLegend:是否添加图注
	 * @param isvalAxis:是否添加Y左轴
	 * @param dataList:数据
	 * @param fldNameArr:属性
	 * @param titleArr:标题
	 * @param showtailArr:保留几位小数
	 * @param ispercentArr:是否百分比
	 * @param width 宽度占几列5表示5列
	 * @return
	 */
	public static boolean createChart(boolean refreshChartExcel,XSSFWorkbook wb, XSSFSheet sheet, int curRow, String type, Enum group,
			boolean isLegend, boolean isvalAxis, List<Map<String,Object>> dataList, List<String> fldNameArr,
			List<String> titleArr, List<String> showtailArr, List<String> ispercentArr,int width)  {
		if(logger.isInfoEnabled()) {
			logger.info("createChart curRow:{} type:{} dataList:{} fldNameArr:{} titleArr:{}",
					curRow,type,DumpUtil.dump(dataList),DumpUtil.dump(fldNameArr),DumpUtil.dump(titleArr));
		}
		boolean result = false;
		String sheetName = sheet.getSheetName();
		// 动态表sheet刷新
		if(refreshChartExcel) {
			result = refreshChartExcel(wb, sheet, dataList, fldNameArr, titleArr, showtailArr, ispercentArr, curRow);
		}
		// 创建一个画布
		Drawing drawing = sheet.createDrawingPatriarch();

		// 前四个默认0，[0,5]：从0列5行开始;[6,20]:宽度6，20向下扩展到20行
		// 默认宽度(14-8)*12
		ClientAnchor anchor = null;
		anchor = drawing.createAnchor(0, 0, 0, 0, 0, curRow + dataList.size() + 1, width,
					curRow + dataList.size() + 12);

		// 创建一个chart对象
		Chart chart = drawing.createChart(anchor);
		CTChart ctChart = ((XSSFChart) chart).getCTChart();
		CTPlotArea ctPlotArea = ctChart.getPlotArea();
		if ("bar".equals(type)) {
			CTBarChart ctBarChart = ctPlotArea.addNewBarChart();
			CTBoolean ctBoolean = ctBarChart.addNewVaryColors();
			ctBarChart.getVaryColors().setVal(true);

			// 设置类型
			ctBarChart.addNewGrouping().setVal(group);
			ctBoolean.setVal(true);
			ctBarChart.addNewBarDir().setVal(STBarDir.COL);

			// 是否添加左侧坐标轴
			ctChart.addNewDispBlanksAs().setVal(STDispBlanksAs.ZERO);
			ctChart.addNewShowDLblsOverMax().setVal(true);

			// 设置这两个参数是为了在STACKED模式下生成堆积模式；(standard)标准模式时需要将这两行去掉
			if ("stacked".equals(group.toString()) || "percentStacked".equals(group.toString())) {
				ctBarChart.addNewGapWidth().setVal(150);
				ctBarChart.addNewOverlap().setVal((byte) 100);
			}

			// 创建序列,并且设置选中区域
			for (int i = 0; i < fldNameArr.size() - 1; i++) {
				CTBarSer ctBarSer = ctBarChart.addNewSer();
				CTSerTx ctSerTx = ctBarSer.addNewTx();
				// 图例区
				CTStrRef ctStrRef = ctSerTx.addNewStrRef();
				String legendDataRange = new CellRangeAddress(curRow, curRow, i + 1, i + 1).formatAsString(sheetName,
						true);
				ctStrRef.setF(legendDataRange);
				ctBarSer.addNewIdx().setVal(i);

				// 横坐标区
				CTAxDataSource cttAxDataSource = ctBarSer.addNewCat();
				ctStrRef = cttAxDataSource.addNewStrRef();
				String axisDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), 0, 0)
						.formatAsString(sheetName, true);
				ctStrRef.setF(axisDataRange);

				// 数据区域
				CTNumDataSource ctNumDataSource = ctBarSer.addNewVal();
				CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
				String numDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), i + 1, i + 1)
						.formatAsString(sheetName, true);
				ctNumRef.setF(numDataRange);

				ctBarSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { 0, 0, 0 });

				// 设置负轴颜色不是白色
				ctBarSer.addNewInvertIfNegative().setVal(false);
				// 设置标签格式
				ctBoolean.setVal(false);
				CTDLbls newDLbls = ctBarSer.addNewDLbls();
				newDLbls.setShowLegendKey(ctBoolean);
				ctBoolean.setVal(true);
				newDLbls.setShowVal(ctBoolean);
				ctBoolean.setVal(false);
				newDLbls.setShowCatName(ctBoolean);
				newDLbls.setShowSerName(ctBoolean);
				newDLbls.setShowPercent(ctBoolean);
				newDLbls.setShowBubbleSize(ctBoolean);
				newDLbls.setShowLeaderLines(ctBoolean);
			}

			// telling the BarChart that it has axes and giving them Ids
			ctBarChart.addNewAxId().setVal(123456);
			ctBarChart.addNewAxId().setVal(123457);

			// cat axis
			CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
			ctCatAx.addNewAxId().setVal(123456); // id of the cat axis
			CTScaling ctScaling = ctCatAx.addNewScaling();
			ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctCatAx.addNewAxPos().setVal(STAxPos.B);
			ctCatAx.addNewCrossAx().setVal(123457); // id of the val axis
			ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// val axis
			CTValAx ctValAx = ctPlotArea.addNewValAx();
			ctValAx.addNewAxId().setVal(123457); // id of the val axis
			ctScaling = ctValAx.addNewScaling();
			ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
			// 设置位置
			ctValAx.addNewAxPos().setVal(STAxPos.L);
			ctValAx.addNewCrossAx().setVal(123456); // id of the cat axis
			ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// 是否删除主左边轴
			if (isvalAxis) {
				ctValAx.addNewDelete().setVal(false);
			} else {
				ctValAx.addNewDelete().setVal(true);
			}

			// 是否删除横坐标
			ctCatAx.addNewDelete().setVal(false);

			// legend图注
			if (isLegend) {
				CTLegend ctLegend = ctChart.addNewLegend();
				ctLegend.addNewLegendPos().setVal(STLegendPos.B);
				ctLegend.addNewOverlay().setVal(false);
			}
		} else if ("line".equals(type)) {
			CTLineChart ctLineChart = ctPlotArea.addNewLineChart();
			CTBoolean ctBoolean = ctLineChart.addNewVaryColors();
			ctLineChart.addNewGrouping().setVal(STGrouping.STANDARD);

			// 创建序列,并且设置选中区域
			for (int i = 0; i < fldNameArr.size() - 1; i++) {
				CTLineSer ctLineSer = ctLineChart.addNewSer();
				CTSerTx ctSerTx = ctLineSer.addNewTx();
				// 图例区
				CTStrRef ctStrRef = ctSerTx.addNewStrRef();
				String legendDataRange = new CellRangeAddress(curRow, curRow, i + 1, i + 1).formatAsString(sheetName,
						true);
				ctStrRef.setF(legendDataRange);
				ctStrRef.setF(legendDataRange);
				ctLineSer.addNewIdx().setVal(i);

				// 横坐标区
				CTAxDataSource cttAxDataSource = ctLineSer.addNewCat();
				ctStrRef = cttAxDataSource.addNewStrRef();
				String axisDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), 0, 0)
						.formatAsString(sheetName, true);
				ctStrRef.setF(axisDataRange);

				// 数据区域
				CTNumDataSource ctNumDataSource = ctLineSer.addNewVal();
				CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
				String numDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), i + 1, i + 1)
						.formatAsString(sheetName, true);
				ctNumRef.setF(numDataRange);

				// 设置标签格式
				ctBoolean.setVal(false);
				CTDLbls newDLbls = ctLineSer.addNewDLbls();
				newDLbls.setShowLegendKey(ctBoolean);
				ctBoolean.setVal(true);
				newDLbls.setShowVal(ctBoolean);
				ctBoolean.setVal(false);
				newDLbls.setShowCatName(ctBoolean);
				newDLbls.setShowSerName(ctBoolean);
				newDLbls.setShowPercent(ctBoolean);
				newDLbls.setShowBubbleSize(ctBoolean);
				newDLbls.setShowLeaderLines(ctBoolean);

				// 是否是平滑曲线
				CTBoolean addNewSmooth = ctLineSer.addNewSmooth();
				addNewSmooth.setVal(false);

				// 是否是堆积曲线
				CTMarker addNewMarker = ctLineSer.addNewMarker();
				CTMarkerStyle addNewSymbol = addNewMarker.addNewSymbol();
				addNewSymbol.setVal(STMarkerStyle.NONE);
			}
			// telling the BarChart that it has axes and giving them Ids
			ctLineChart.addNewAxId().setVal(123456);
			ctLineChart.addNewAxId().setVal(123457);

			// cat axis
			CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
			ctCatAx.addNewAxId().setVal(123456); // id of the cat axis
			CTScaling ctScaling = ctCatAx.addNewScaling();
			ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctCatAx.addNewAxPos().setVal(STAxPos.B);
			ctCatAx.addNewCrossAx().setVal(123457); // id of the val axis
			ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// val axis
			CTValAx ctValAx = ctPlotArea.addNewValAx();
			ctValAx.addNewAxId().setVal(123457); // id of the val axis
			ctScaling = ctValAx.addNewScaling();
			ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctValAx.addNewAxPos().setVal(STAxPos.L);
			ctValAx.addNewCrossAx().setVal(123456); // id of the cat axis
			ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// 是否删除主左边轴
			if (isvalAxis) {
				ctValAx.addNewDelete().setVal(false);
			} else {
				ctValAx.addNewDelete().setVal(true);
			}

			// 是否删除横坐标
			ctCatAx.addNewDelete().setVal(false);

			// legend图注
			if (isLegend) {
				CTLegend ctLegend = ctChart.addNewLegend();
				ctLegend.addNewLegendPos().setVal(STLegendPos.B);
				ctLegend.addNewOverlay().setVal(false);
			}
		} else if ("bar-line-2".equals(type)) {
			CTBarChart ctBarChart = ctPlotArea.addNewBarChart();
			CTBoolean ctBoolean = ctBarChart.addNewVaryColors();
			ctBarChart.getVaryColors().setVal(true);

			// 设置类型
			ctBarChart.addNewGrouping().setVal(STBarGrouping.CLUSTERED);
			ctBoolean.setVal(true);
			ctBarChart.addNewBarDir().setVal(STBarDir.COL);

			// 是否添加左侧坐标轴
			ctChart.addNewDispBlanksAs().setVal(STDispBlanksAs.ZERO);
			ctChart.addNewShowDLblsOverMax().setVal(true);

			// 创建序列,并且设置选中区域
			for (int i = 0; i < fldNameArr.size() - 2; i++) {
				CTBarSer ctBarSer = ctBarChart.addNewSer();
				CTSerTx ctSerTx = ctBarSer.addNewTx();
				// 图例区
				CTStrRef ctStrRef = ctSerTx.addNewStrRef();
				String legendDataRange = new CellRangeAddress(curRow, curRow, i + 1, i + 1).formatAsString(sheetName,
						true);
				ctStrRef.setF(legendDataRange);
				ctBarSer.addNewIdx().setVal(i);

				// 横坐标区
				CTAxDataSource cttAxDataSource = ctBarSer.addNewCat();
				ctStrRef = cttAxDataSource.addNewStrRef();
				String axisDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), 0, 0)
						.formatAsString(sheetName, true);
				ctStrRef.setF(axisDataRange);

				// 数据区域
				CTNumDataSource ctNumDataSource = ctBarSer.addNewVal();
				CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
				String numDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), i + 1, i + 1)
						.formatAsString(sheetName, true);
				ctNumRef.setF(numDataRange);

				ctBarSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { 0, 0, 0 });
				// 设置负轴颜色不是白色
				ctBarSer.addNewInvertIfNegative().setVal(false);
				// 设置标签格式
				ctBoolean.setVal(false);
				CTDLbls newDLbls = ctBarSer.addNewDLbls();
				newDLbls.setShowLegendKey(ctBoolean);
				ctBoolean.setVal(true);
				newDLbls.setShowVal(ctBoolean);
				ctBoolean.setVal(false);
				newDLbls.setShowCatName(ctBoolean);
				newDLbls.setShowSerName(ctBoolean);
				newDLbls.setShowPercent(ctBoolean);
				newDLbls.setShowBubbleSize(ctBoolean);
				newDLbls.setShowLeaderLines(ctBoolean);
			}

			// telling the BarChart that it has axes and giving them Ids
			ctBarChart.addNewAxId().setVal(123456);
			ctBarChart.addNewAxId().setVal(123457);

			// telling the BarChart that it has axes and giving them Ids
			ctBarChart.addNewAxId().setVal(123456);
			ctBarChart.addNewAxId().setVal(123457);

			// cat axis
			CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
			ctCatAx.addNewAxId().setVal(123456); // id of the cat axis
			CTScaling ctScaling = ctCatAx.addNewScaling();
			ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctCatAx.addNewAxPos().setVal(STAxPos.B);
			ctCatAx.addNewCrossAx().setVal(123457); // id of the val axis
			ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// val axis
			CTValAx ctValAx = ctPlotArea.addNewValAx();
			ctValAx.addNewAxId().setVal(123457); // id of the val axis
			ctScaling = ctValAx.addNewScaling();
			ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctValAx.addNewAxPos().setVal(STAxPos.L);
			ctValAx.addNewCrossAx().setVal(123456); // id of the cat axis
			ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// 是否删除主左边轴
			if (isvalAxis) {
				ctValAx.addNewDelete().setVal(false);
			} else {
				ctValAx.addNewDelete().setVal(true);
			}

			// 是否删除横坐标
			ctCatAx.addNewDelete().setVal(false);

			// legend图注
			if (isLegend) {
				CTLegend ctLegend = ctChart.addNewLegend();
				ctLegend.addNewLegendPos().setVal(STLegendPos.B);
				ctLegend.addNewOverlay().setVal(false);
			}

			CTLineChart ctLineChart = ctPlotArea.addNewLineChart();
			ctLineChart.addNewGrouping().setVal(STGrouping.STANDARD);

			// 创建序列,并且设置选中区域
			for (int i = 1; i < fldNameArr.size() - 1; i++) {
				CTLineSer ctLineSer = ctLineChart.addNewSer();
				CTSerTx ctSerTx = ctLineSer.addNewTx();
				// 图例区
				CTStrRef ctStrRef = ctSerTx.addNewStrRef();
				String legendDataRange = new CellRangeAddress(curRow, curRow, i + 1, i + 1).formatAsString(sheetName,
						true);
				ctStrRef.setF(legendDataRange);
				ctStrRef.setF(legendDataRange);
				ctLineSer.addNewIdx().setVal(i);

				// 横坐标区
				CTAxDataSource cttAxDataSource = ctLineSer.addNewCat();
				ctStrRef = cttAxDataSource.addNewStrRef();
				String axisDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), 0, 0)
						.formatAsString(sheetName, true);
				ctStrRef.setF(axisDataRange);

				// 数据区域
				CTNumDataSource ctNumDataSource = ctLineSer.addNewVal();
				CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
				String numDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), i + 1, i + 1)
						.formatAsString(sheetName, true);
				ctNumRef.setF(numDataRange);

				// 是否是平滑曲线
				CTBoolean addNewSmooth = ctLineSer.addNewSmooth();
				addNewSmooth.setVal(false);

				// 是否是堆积曲线
				CTMarker addNewMarker = ctLineSer.addNewMarker();
				CTMarkerStyle addNewSymbol = addNewMarker.addNewSymbol();
				addNewSymbol.setVal(STMarkerStyle.NONE);
			}

			// telling the BarChart that it has axes and giving them Ids
			// TODO:写死是否有影响？
			ctLineChart.addNewAxId().setVal(1234567);
			ctLineChart.addNewAxId().setVal(1234578);

			// cat axis
			CTCatAx ctCatAxline = ctPlotArea.addNewCatAx();
			ctCatAxline.addNewAxId().setVal(1234567); // id of the cat axis
			CTScaling ctScalingline = ctCatAxline.addNewScaling();
			ctScalingline.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctCatAxline.addNewDelete().setVal(true);
			ctCatAxline.addNewAxPos().setVal(STAxPos.B);
			ctCatAxline.addNewMajorTickMark().setVal(STTickMark.OUT);
			ctCatAxline.addNewMinorTickMark().setVal(STTickMark.NONE);
			ctCatAxline.addNewAuto().setVal(true);
			ctCatAxline.addNewLblAlgn().setVal(STLblAlgn.CTR);
			ctCatAxline.addNewLblOffset().setVal(100);
			ctCatAxline.addNewNoMultiLvlLbl().setVal(false);
			ctCatAxline.addNewCrossAx().setVal(1234578); // id of the val axis
			ctCatAxline.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// val axis
			CTValAx ctValAxline = ctPlotArea.addNewValAx();
			ctValAxline.addNewAxId().setVal(1234578); // id of the val axis
			ctScalingline = ctValAxline.addNewScaling();
			ctScalingline.addNewOrientation().setVal(STOrientation.MIN_MAX);
			// Y轴右侧坐标true删除，false保留
			ctValAxline.addNewDelete().setVal(false);
			ctValAxline.addNewAxPos().setVal(STAxPos.R);
			ctValAxline.addNewMajorTickMark().setVal(STTickMark.OUT);
			ctValAxline.addNewMinorTickMark().setVal(STTickMark.NONE);
			ctValAxline.addNewCrosses().setVal(STCrosses.MAX);
			ctValAxline.addNewCrossBetween().setVal(STCrossBetween.BETWEEN);
			ctValAxline.addNewCrossAx().setVal(1234567); // id of the cat axis
			ctValAxline.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
		} else if ("bar-line-4".equals(type)) {
			CTBarChart ctBarChart = ctPlotArea.addNewBarChart();
			CTBoolean ctBoolean = ctBarChart.addNewVaryColors();
			ctBarChart.getVaryColors().setVal(true);

			// 设置类型
			ctBarChart.addNewGrouping().setVal(STBarGrouping.CLUSTERED);
			ctBoolean.setVal(true);
			ctBarChart.addNewBarDir().setVal(STBarDir.COL);

			// 是否添加左侧坐标轴
			ctChart.addNewDispBlanksAs().setVal(STDispBlanksAs.ZERO);
			ctChart.addNewShowDLblsOverMax().setVal(true);

			// 创建序列,并且设置选中区域
			for (int i = 0; i < fldNameArr.size() - 1 - 2; i++) {
				CTBarSer ctBarSer = ctBarChart.addNewSer();
				CTSerTx ctSerTx = ctBarSer.addNewTx();
				// 图例区
				CTStrRef ctStrRef = ctSerTx.addNewStrRef();
				String legendDataRange = new CellRangeAddress(curRow, curRow, i + 1, i + 1).formatAsString(sheetName,
						true);
				ctStrRef.setF(legendDataRange);
				ctBarSer.addNewIdx().setVal(i);

				// 横坐标区
				CTAxDataSource cttAxDataSource = ctBarSer.addNewCat();
				ctStrRef = cttAxDataSource.addNewStrRef();
				String axisDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), 0, 0)
						.formatAsString(sheetName, true);
				ctStrRef.setF(axisDataRange);

				// 数据区域
				CTNumDataSource ctNumDataSource = ctBarSer.addNewVal();
				CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
				String numDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), i + 1, i + 1)
						.formatAsString(sheetName, true);
				ctNumRef.setF(numDataRange);

				ctBarSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { 0, 0, 0 });
				// 设置负轴颜色不是白色
				ctBarSer.addNewInvertIfNegative().setVal(false);
				// 设置标签格式
				ctBoolean.setVal(false);
				CTDLbls newDLbls = ctBarSer.addNewDLbls();
				newDLbls.setShowLegendKey(ctBoolean);
				ctBoolean.setVal(true);
				newDLbls.setShowVal(ctBoolean);
				ctBoolean.setVal(false);
				newDLbls.setShowCatName(ctBoolean);
				newDLbls.setShowSerName(ctBoolean);
				newDLbls.setShowPercent(ctBoolean);
				newDLbls.setShowBubbleSize(ctBoolean);
				newDLbls.setShowLeaderLines(ctBoolean);
			}

			// telling the BarChart that it has axes and giving them Ids
			ctBarChart.addNewAxId().setVal(123456);
			ctBarChart.addNewAxId().setVal(123457);

			// telling the BarChart that it has axes and giving them Ids
			ctBarChart.addNewAxId().setVal(123456);
			ctBarChart.addNewAxId().setVal(123457);

			// cat axis
			CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
			ctCatAx.addNewAxId().setVal(123456); // id of the cat axis
			CTScaling ctScaling = ctCatAx.addNewScaling();
			ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctCatAx.addNewAxPos().setVal(STAxPos.B);
			ctCatAx.addNewCrossAx().setVal(123457); // id of the val axis
			ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// val axis
			CTValAx ctValAx = ctPlotArea.addNewValAx();
			ctValAx.addNewAxId().setVal(123457); // id of the val axis
			ctScaling = ctValAx.addNewScaling();
			ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctValAx.addNewAxPos().setVal(STAxPos.L);
			ctValAx.addNewCrossAx().setVal(123456); // id of the cat axis
			ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// 是否删除主左边轴
			if (isvalAxis) {
				ctValAx.addNewDelete().setVal(false);
			} else {
				ctValAx.addNewDelete().setVal(true);
			}

			// 是否删除横坐标
			ctCatAx.addNewDelete().setVal(false);

			// legend图注
			if (isLegend) {
				CTLegend ctLegend = ctChart.addNewLegend();
				ctLegend.addNewLegendPos().setVal(STLegendPos.B);
				ctLegend.addNewOverlay().setVal(false);
			}

			CTLineChart ctLineChart = ctPlotArea.addNewLineChart();
			ctLineChart.addNewGrouping().setVal(STGrouping.STANDARD);

			// 创建序列,并且设置选中区域
			for (int i = 2; i < fldNameArr.size() - 1; i++) {
				CTLineSer ctLineSer = ctLineChart.addNewSer();
				CTSerTx ctSerTx = ctLineSer.addNewTx();
				// 图例区
				CTStrRef ctStrRef = ctSerTx.addNewStrRef();
				String legendDataRange = new CellRangeAddress(curRow, curRow, i + 1, i + 1).formatAsString(sheetName,
						true);
				ctStrRef.setF(legendDataRange);
				ctStrRef.setF(legendDataRange);
				ctLineSer.addNewIdx().setVal(i);

				// 横坐标区
				CTAxDataSource cttAxDataSource = ctLineSer.addNewCat();
				ctStrRef = cttAxDataSource.addNewStrRef();
				String axisDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), 0, 0)
						.formatAsString(sheetName, true);
				ctStrRef.setF(axisDataRange);

				// 数据区域
				CTNumDataSource ctNumDataSource = ctLineSer.addNewVal();
				CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
				String numDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), i + 1, i + 1)
						.formatAsString(sheetName, true);
				ctNumRef.setF(numDataRange);

				// 是否是平滑曲线
				CTBoolean addNewSmooth = ctLineSer.addNewSmooth();
				addNewSmooth.setVal(false);

				// 是否是堆积曲线
				CTMarker addNewMarker = ctLineSer.addNewMarker();
				CTMarkerStyle addNewSymbol = addNewMarker.addNewSymbol();
				addNewSymbol.setVal(STMarkerStyle.NONE);
			}

			// telling the BarChart that it has axes and giving them Ids
			// TODO:写死是否有影响？
			ctLineChart.addNewAxId().setVal(1234567);
			ctLineChart.addNewAxId().setVal(1234578);

			// cat axis
			CTCatAx ctCatAxline = ctPlotArea.addNewCatAx();
			ctCatAxline.addNewAxId().setVal(1234567); // id of the cat axis
			CTScaling ctScalingline = ctCatAxline.addNewScaling();
			ctScalingline.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctCatAxline.addNewDelete().setVal(true);
			ctCatAxline.addNewAxPos().setVal(STAxPos.B);
			ctCatAxline.addNewMajorTickMark().setVal(STTickMark.OUT);
			ctCatAxline.addNewMinorTickMark().setVal(STTickMark.NONE);
			ctCatAxline.addNewAuto().setVal(true);
			ctCatAxline.addNewLblAlgn().setVal(STLblAlgn.CTR);
			ctCatAxline.addNewLblOffset().setVal(100);
			ctCatAxline.addNewNoMultiLvlLbl().setVal(false);
			ctCatAxline.addNewCrossAx().setVal(1234578); // id of the val axis
			ctCatAxline.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// val axis
			CTValAx ctValAxline = ctPlotArea.addNewValAx();
			ctValAxline.addNewAxId().setVal(1234578); // id of the val axis
			ctScalingline = ctValAxline.addNewScaling();
			ctScalingline.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctValAxline.addNewDelete().setVal(false);
			ctValAxline.addNewAxPos().setVal(STAxPos.R);
			ctValAxline.addNewMajorTickMark().setVal(STTickMark.OUT);
			ctValAxline.addNewMinorTickMark().setVal(STTickMark.NONE);
			ctValAxline.addNewCrosses().setVal(STCrosses.MAX);
			ctValAxline.addNewCrossBetween().setVal(STCrossBetween.BETWEEN);
			ctValAxline.addNewCrossAx().setVal(1234567); // id of the cat axis
			ctValAxline.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
		} else if ("pie".equals(type)) {// pie
			CTPieChart ctPieChart = ctPlotArea.addNewPieChart();
			CTBoolean ctBoolean = ctPieChart.addNewVaryColors();

			// 创建序列,并且设置选中区域
			for (int i = 0; i < fldNameArr.size() - 1; i++) {
				CTPieSer ctPieSer = ctPieChart.addNewSer();
				CTSerTx ctSerTx = ctPieSer.addNewTx();
				// 图例区
				CTStrRef ctStrRef = ctSerTx.addNewStrRef();
				String legendDataRange = new CellRangeAddress(curRow, curRow, i + 1, i + 1).formatAsString(sheetName,
						true);
				ctStrRef.setF(legendDataRange);
				ctPieSer.addNewIdx().setVal(i);

				// 横坐标区
				CTAxDataSource cttAxDataSource = ctPieSer.addNewCat();
				ctStrRef = cttAxDataSource.addNewStrRef();
				String axisDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), 0, 0)
						.formatAsString(sheetName, true);
				ctStrRef.setF(axisDataRange);

				// 数据区域
				CTNumDataSource ctNumDataSource = ctPieSer.addNewVal();
				CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
				String numDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), i + 1, i + 1)
						.formatAsString(sheetName, true);
				ctNumRef.setF(numDataRange);

				ctPieSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { 0, 0, 0 });

				// 设置标签格式
				ctBoolean.setVal(true);
			}
			// legend图注
			CTLegend ctLegend = ctChart.addNewLegend();
			ctLegend.addNewLegendPos().setVal(STLegendPos.B);
			ctLegend.addNewOverlay().setVal(true);
		} else if ("pie3D".equals(type)) {// pie3D
			CTPie3DChart ctPie3DChart = ctPlotArea.addNewPie3DChart();
			CTBoolean ctBoolean = ctPie3DChart.addNewVaryColors();

			// 创建序列,并且设置选中区域
			for (int i = 0; i < fldNameArr.size() - 1; i++) {
				CTPieSer ctPieSer = ctPie3DChart.addNewSer();
				CTSerTx ctSerTx = ctPieSer.addNewTx();
				// 图例区
				CTStrRef ctStrRef = ctSerTx.addNewStrRef();
				String legendDataRange = new CellRangeAddress(curRow, curRow, i + 1, i + 1).formatAsString(sheetName,
						true);
				ctStrRef.setF(legendDataRange);
				ctPieSer.addNewIdx().setVal(i);

				// 横坐标区
				CTAxDataSource cttAxDataSource = ctPieSer.addNewCat();
				ctStrRef = cttAxDataSource.addNewStrRef();
				String axisDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), 0, 0)
						.formatAsString(sheetName, true);
				ctStrRef.setF(axisDataRange);

				// 数据区域
				CTNumDataSource ctNumDataSource = ctPieSer.addNewVal();
				CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
				String numDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), i + 1, i + 1)
						.formatAsString(sheetName, true);
				ctNumRef.setF(numDataRange);

				ctPieSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[] { 0, 0, 0 });

				// 设置标签格式
				ctBoolean.setVal(true);
			}
			// legend图注
			CTLegend ctLegend = ctChart.addNewLegend();
			ctLegend.addNewLegendPos().setVal(STLegendPos.B);
			ctLegend.addNewOverlay().setVal(true);
		} else {// area
			CTAreaChart ctAreaChart = ctPlotArea.addNewAreaChart();
			CTBoolean ctBoolean = ctAreaChart.addNewVaryColors();
			ctAreaChart.addNewGrouping().setVal(STGrouping.STANDARD);

			// 创建序列,并且设置选中区域
			for (int i = 0; i < fldNameArr.size() - 1; i++) {
				CTAreaSer ctAreaSer = ctAreaChart.addNewSer();
				CTSerTx ctSerTx = ctAreaSer.addNewTx();
				// 图例区
				CTStrRef ctStrRef = ctSerTx.addNewStrRef();
				String legendDataRange = new CellRangeAddress(curRow, curRow, i + 1, i + 1).formatAsString(sheetName,
						true);
				ctStrRef.setF(legendDataRange);
				ctStrRef.setF(legendDataRange);
				ctAreaSer.addNewIdx().setVal(i);

				// 横坐标区
				CTAxDataSource cttAxDataSource = ctAreaSer.addNewCat();
				ctStrRef = cttAxDataSource.addNewStrRef();
				String axisDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), 0, 0)
						.formatAsString(sheetName, true);
				ctStrRef.setF(axisDataRange);

				// 数据区域
				CTNumDataSource ctNumDataSource = ctAreaSer.addNewVal();
				CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
				String numDataRange = new CellRangeAddress(curRow + 1, curRow + dataList.size(), i + 1, i + 1)
						.formatAsString(sheetName, true);
				ctNumRef.setF(numDataRange);

				// 设置标签格式
				ctBoolean.setVal(false);
				CTDLbls newDLbls = ctAreaSer.addNewDLbls();
				newDLbls.setShowLegendKey(ctBoolean);
				ctBoolean.setVal(true);
				newDLbls.setShowVal(ctBoolean);
				ctBoolean.setVal(false);
				newDLbls.setShowCatName(ctBoolean);
				newDLbls.setShowSerName(ctBoolean);
				newDLbls.setShowPercent(ctBoolean);
				newDLbls.setShowBubbleSize(ctBoolean);
				newDLbls.setShowLeaderLines(ctBoolean);

				/*
				 * //是否是平滑曲线 CTBoolean addNewSmooth = ctAreaSer.addNewSmooth();
				 * addNewSmooth.setVal(false); //是否是堆积曲线 CTMarker addNewMarker =
				 * ctAreaSer.addNewMarker(); CTMarkerStyle addNewSymbol =
				 * addNewMarker.addNewSymbol(); addNewSymbol.setVal(STMarkerStyle.NONE);
				 */
			}
			// telling the BarChart that it has axes and giving them Ids
			ctAreaChart.addNewAxId().setVal(123456);
			ctAreaChart.addNewAxId().setVal(123457);

			// cat axis
			CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
			ctCatAx.addNewAxId().setVal(123456); // id of the cat axis
			CTScaling ctScaling = ctCatAx.addNewScaling();
			ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctCatAx.addNewAxPos().setVal(STAxPos.B);
			ctCatAx.addNewCrossAx().setVal(123457); // id of the val axis
			ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// val axis
			CTValAx ctValAx = ctPlotArea.addNewValAx();
			ctValAx.addNewAxId().setVal(123457); // id of the val axis
			ctScaling = ctValAx.addNewScaling();
			ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
			ctValAx.addNewAxPos().setVal(STAxPos.L);
			ctValAx.addNewCrossAx().setVal(123456); // id of the cat axis
			ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

			// 是否删除主左边轴
			if (isvalAxis) {
				ctValAx.addNewDelete().setVal(false);
			} else {
				ctValAx.addNewDelete().setVal(true);
			}

			// 是否删除横坐标
			ctCatAx.addNewDelete().setVal(false);

			// legend图注
			if (isLegend) {
				CTLegend ctLegend = ctChart.addNewLegend();
				ctLegend.addNewLegendPos().setVal(STLegendPos.B);
				ctLegend.addNewOverlay().setVal(false);
			}
		}
		return result;
	}
}
