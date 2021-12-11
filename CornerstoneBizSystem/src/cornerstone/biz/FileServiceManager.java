package cornerstone.biz;

import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.srv.AmazonS3Service;
import cornerstone.biz.srv.CosService;
import cornerstone.biz.srv.FileService;
import cornerstone.biz.srv.LocalFileService;
import cornerstone.biz.util.StringUtil;

/**
 * 
 * @author cs
 *
 */
public class FileServiceManager {
	//
	public static final String TYPE_COS="cos";//默认cos
	public static final String TYPE_OSS="oss";
	//Amazon s3
	public static final String TYPE_S3="s3";
	//
	public static String getType() {
		String type=GlobalConfig.fileServiceType;
		if(StringUtil.isEmptyWithTrim(type)) {
			type=TYPE_COS;
		}
		return type;
	}
	//
	public static FileService get() {
		if(GlobalConfig.fileserviceSaveLocal) {//存到本地
			return LocalFileService.get();
		}
		String type=getType();
		if(type.equals(TYPE_COS)) {
			return CosService.get();
		}else if(type.equals(TYPE_S3)){
			return AmazonS3Service.get();
		}
		return LocalFileService.get();
	}
}
