package cornerstone.biz.util.example;

import java.io.InputStream;

import cornerstone.biz.poi.ExcelUtils;
import cornerstone.biz.poi.TableData;
import cornerstone.biz.util.DumpUtil;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * 示例任务
 * @author cs
 *
 */
public class ExampleTaskUtil {
	//
	private static Logger logger=LoggerFactory.get(ExampleTaskUtil.class);
	//
	/**
	 * 
	 * @param objectType
	 * @return
	 */
	public static TableData readTasks(int objectType){
		TableData data=null;
		InputStream is=null;
		try {
			is=ExampleTaskUtil.class.getResourceAsStream("example-tasks.xlsx");
			data=ExcelUtils.readExcel(is,objectType-1);
			if(logger.isDebugEnabled()) {
				logger.debug("readTasks data:{}",DumpUtil.dump(data));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException("readTasks failed.objectType:"+objectType); 
		}finally {
			if(is!=null) {
				try {
					is.close();
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		return data;
	}
	//
}
