package cornerstone.biz.util;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cornerstone.biz.domain.ExportData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cornerstone.biz.poi.ExcelUtils;
import cornerstone.biz.poi.PoiUtils;
import cornerstone.biz.poi.TableData;
import cornerstone.biz.poi.XTableData;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.mvc.Context;
import jazmin.util.IOUtil;

/**
 * 导出excel utils
 * 
 * @author raoyi
 *
 */
public class ExportBeanToExcelUtil {
	//
	private static Logger logger = LoggerFactory.getLogger(ExportBeanToExcelUtil.class);

	/**
	 * 
	 * @param <T>
	 * @param ctx
	 * @param fileName
	 * @param list
	 * @param inline
	 */
	public static <T> void exportExcel(Context ctx, String fileName,Class<T> clazz, List<T> list, boolean inline) {
		exportExcel(ctx, fileName, clazz, list, null, inline);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param ctx
	 * @param fileName
	 * @param list
	 * @param excludeFields 排除导出的字段
	 * @param inline
	 */
	public static <T> void exportExcel(Context ctx, String fileName,Class<T> clazz,List<T> list, List<String> excludeFields,boolean inline) {
		ByteArrayOutputStream bos = null;
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook();
			PoiUtils.writeExcel(wb, fileName,clazz,list, excludeFields);
			bos = new ByteArrayOutputStream();
			wb.write(bos);
			render(ctx, fileName, bos.toByteArray(), inline);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AppException(e.getMessage());
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			IOUtil.closeQuietly(bos);
		}
	}

	/**
	 * 
	 * @param ctx
	 * @param fileName
	 * @param sheetName
	 * @param headers
	 * @param contents
	 * @param inline
	 */
	public static void exportExcel(Context ctx, String fileName, String sheetName, List<String> headers,
			List<List<String>> contents, boolean inline) {
		exportExcel(ctx, fileName, sheetName, null, headers, contents, inline);
	}
	/**
	 * 
	 * @param ctx
	 * @param headers
	 * @param contents
	 * @param fileName
	 */
	public static void exportExcel(Context ctx, String fileName, String sheetName, List<Integer> columnWidth,List<String> headers,
								   List<List<String>> contents, boolean inline) {
		ByteArrayOutputStream bos = null;
		TableData data = new TableData();
		data.sheetName = sheetName;
		data.columnWidth=columnWidth;
		data.headers = headers;
		data.contents = contents;
		try {
			bos = PoiUtils.writeExcel(data);
			render(ctx, fileName, bos.toByteArray(), inline);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} finally {
			IOUtil.closeQuietly(bos);
		}
	}

	public static void exportExcel(Context ctx, String fileName, String sheetName, ExportData exportData, boolean inline) {
		ByteArrayOutputStream bos = null;
		TableData data = exportData.tableData;
		data.sheetName = sheetName;
		try {
			bos = PoiUtils.writeExcel(data);
			render(ctx, fileName, bos.toByteArray(), inline);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} finally {
			IOUtil.closeQuietly(bos);
		}
	}
	//
	/**
	 * 以下为服务器端判断客户端浏览器类型的方法
	 * 
	 * @param request
	 * @return
	 */
	private static String getBrowser(HttpServletRequest request) {
		String userAgent = request.getHeader("USER-AGENT").toLowerCase();
		if (userAgent != null) {
			if (userAgent.indexOf("msie") != -1)
				return "IE";
			if (userAgent.indexOf("firefox") != -1)
				return "FF";
			if (userAgent.indexOf("safari") != -1)
				return "SF";
		}
		return null;
	}
	
	/**
	 * 
	 * @param ctx
	 * @param fileName
	 * @param datas
	 * @param inline
	 */
	public static void exportMoreSheetsExcel(Context ctx, String fileName, List<XTableData> datas, boolean inline) {
		ByteArrayOutputStream bos = null;
		try {
			bos =ExcelUtils.writeSheetsExcel(datas);
			render(ctx, fileName, bos.toByteArray(), inline);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		} finally {
			IOUtil.closeQuietly(bos);
		}
	}
	//
	//
	public static void render(Context ctx, String fileName, byte[] content, boolean inline)  {
		HttpServletResponse response = ctx.response().raw();
		ServletOutputStream outStream = null;
		try {
			String mimetype = "application/x-msdownload";
			response.setContentType(mimetype);
			response.addHeader("Cache-Control", "no-cache");//不能有缓存
			response.addHeader("charset", "utf-8");
			String browser=getBrowser(ctx.request().raw());
			if ("FF".equals(browser)) {// 针对火狐浏览器处理方式不一样了
				fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}else if ("SF".equals(browser)) {// safari 会自动加上exe
				response.setContentType("applicatoin/octet-stream");
				fileName = new String(fileName.getBytes("GB2312"), "ISO-8859-1");
			}else{
				fileName = URLEncoder.encode(fileName, "UTF-8");// encode解决中文乱码
				fileName = fileName.replace("+", "%20");// encode后替换 解决空格问题
			}
			if (inline) {
				response.addHeader("Content-Disposition", "inline;filename=\"" + fileName + "\"");
			} else {
				response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
			}
			outStream = response.getOutputStream();
			outStream.write(content);
			outStream.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AppException(e);
		} finally {
			IOUtil.closeQuietly(outStream);
		}
	}
}