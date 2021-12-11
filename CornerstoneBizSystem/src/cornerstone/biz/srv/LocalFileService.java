package cornerstone.biz.srv;

import java.io.File;

import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * 
 * @author cs
 *
 */
public class LocalFileService extends FileService{
	//
	private static Logger logger=LoggerFactory.getLogger(LocalFileService.class);
	//
	private static LocalFileService instance;
	//
	public LocalFileService() {
		
	}
	//
	public static LocalFileService get() {
		if(instance==null) {
			instance=new LocalFileService();
		}
		return instance;
	}
	//
	/**
	 * 下载图片
	 * @param fileId
	 * @return
	 */
	@Override
	public File download(String fileId) {
		long startTime=System.currentTimeMillis();
		File file=getFile(fileId);
		if(!file.exists()) {
//			throw new AppException("file not existed "+fileId);
			logger.error("file not existed "+fileId);
		}
		if (logger.isInfoEnabled()) {
			logger.info("download success using {}ms fileId:{}",
					(System.currentTimeMillis()-startTime),
					fileId);
		}
		return download(fileId, file);
	}
}
