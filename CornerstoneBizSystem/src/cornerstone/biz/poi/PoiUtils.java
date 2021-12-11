package cornerstone.biz.poi;

import java.io.*;
import java.text.ParseException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author cs
 *
 */
public class PoiUtils {
	//
	/**
	 * 
	 * @param wb
	 * @param sheetName
	 * @param list
	 *
	 */
	public static <T> void writeExcel(XSSFWorkbook wb,String sheetName,Class<T> clazz, List<T> list,List<String> excludeFields)  {
		 ExcelUtils.writeExcel(wb,sheetName, clazz, list, excludeFields);
	}
	/**
	 * 
	 * @param list
	 * @return
	 *
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
		writeExcelToFile(null,clazz,list, file);
	}

	/**
	 * 
	 * @param list
	 * @param file
	 *
	 */
	public static <T> void writeExcelToFile(String sheetName,Class<T> clazz, List<T> list, File file) throws IOException {
		ExcelUtils.writeExcelToFile(sheetName,clazz, list, file);
	}

	/**
	 *
	 * @param sheetName
	 * @param list
	 * @param file
	 * @param <T>
	 *
	 */
	public static <T> void writeMoreSheetExcelToFile(String sheetName, List<T> list, File file)  {
		ExcelUtils.writeMoreSheetExcelToFile(sheetName, list, file,0,0);
	}
	
	public static <T> void writeMoreSheetExcelToFile(String sheetName, List<T> list, File file,int startRow,int startCol)  {
		ExcelUtils.writeMoreSheetExcelToFile(sheetName,list,file,startRow,startCol);
	}

	/**
	 * 
	 * @param sheetName
	 * @param list
	 * @return
	 *
	 */
	public static <T> ByteArrayOutputStream writeExcel(String sheetName,Class<T> clazz, List<T> list) throws IOException {
		return ExcelUtils.writeExcel(sheetName,clazz, list);
	}

	/**
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static ByteArrayOutputStream writeExcel(TableData data) throws IOException {
		return ExcelUtils.writeExcel(data);
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
	public static void addPicture(File xlsxFile,byte[] imageData,
			 int col1, int row1, int col2, int row2) throws IOException{
		ExcelUtils.addPicture(xlsxFile,imageData,col1, row1, col2, row2);
	}

	/**
	 * 
	 * @param clazz
	 * @param in
	 * @return
	 *
	 */
	public static <T> List<T> readExcel(Class<T> clazz, InputStream in) throws InstantiationException, IllegalAccessException, ParseException, IOException {
		return ExcelUtils.readExcel(clazz, in);
	}
	
	/**
	 * 
	 * @param clazz
	 * @param file
	 * @return
	 *
	 */
	public static <T> List<T> readExcel(Class<T> clazz, File file) throws IOException, InstantiationException, IllegalAccessException, ParseException {
		return ExcelUtils.readExcel(clazz, new FileInputStream(file));
	}

	/**
	 * 
	 * @param in
	 * @return
	 *
	 */
	public static TableData readExcel(InputStream in) throws IOException {
		return ExcelUtils.readExcel(in);
	}

}
