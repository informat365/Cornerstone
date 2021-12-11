package cornerstone.biz.srv;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;

import cornerstone.biz.domain.FileInfo;
import cornerstone.biz.domain.GlobalConfig;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.FileUtil;

/**
 * V4升级到V5 如果之前是v4使用新库则需要修改fileservice.cos.region 之前是gz 修改为ap-guangzhou；bucketName之前是ititbucket修改为ititbucket-appid
 * https://cloud.tencent.com/document/product/436/6224
 * @author cs
 *
 */
public class CosService extends FileService{
	//
	private static Logger logger=LoggerFactory.getLogger(CosService.class);
	//
	private COSClient cosClient;
	//
	private static CosService instance;
	//
	private CosService() {
		
	}
	//
	public static CosService get() {
		if(instance==null) {
			instance=new CosService();
			
		}
		return instance;
	}
	//
	private COSClient getClient() {
		if(cosClient==null) {
			super.init();
			COSCredentials cred = new BasicCOSCredentials(GlobalConfig.fileServiceCosSecretId,GlobalConfig.fileServiceCosSecretKey);
			ClientConfig clientConfig = new ClientConfig(new Region(GlobalConfig.fileServiceCosRegion));
			cosClient=new COSClient(cred,clientConfig);
			if(logger.isInfoEnabled()) {
				logger.info("CosService init fileServiceCosAppId:{} fileServiceCosSecretId:{} fileServiceCosRegion:{}",
						GlobalConfig.fileServiceCosAppId, 
						GlobalConfig.fileServiceCosSecretId,
						GlobalConfig.fileServiceCosRegion);
			}
		}
		return cosClient;
	}
	
	@Override
	public FileInfo upload(InputStream is, String fileName) {
		long startTime=System.currentTimeMillis();
		FileInfo info=super.upload(is, fileName);
		uploadFile(info.fileId, info.fileAbsolutePath);
		if (logger.isInfoEnabled()) {
			logger.info("upload success using {}ms fileName:{} filePath:{} info:{}",
					(System.currentTimeMillis()-startTime),
					fileName,
					info.fileAbsolutePath,
					DumpUtil.dump(info));
		}
		return info;
	}
	
	/**
	 * 上传到腾讯Cos
	 * 
	 * @param path
	 */
	private void uploadFile(String fileId, String path) {
		File localFile = new File(path);
		// 指定要上传到 COS 上对象键
		// 对象键（Key）是对象在存储桶中的唯一标识。例如，在对象的访问域名 `bucket1-1250000000.cos.ap-guangzhou.myqcloud.com/doc1/pic1.jpg` 中，对象键为 doc1/pic1.jpg, 详情参考 [对象键](https://cloud.tencent.com/document/product/436/13324)
		String etag=null;
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(GlobalConfig.fileServiceCosBucketName, fileId, localFile);
			PutObjectResult result = getClient().putObject(putObjectRequest);
			// putobjectResult会返回文件的etag
            etag = result.getETag();
		}catch(Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException("上传文件失败");
		}finally {
			logger.info("uploadFile fileId:{} path:{} etag{}",fileId, path,etag);
		}
	}

	
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
			downloadFile(fileId,file.getAbsolutePath());
		}
		if (logger.isInfoEnabled()) {
			logger.info("download success using {}ms fileId:{}",
					(System.currentTimeMillis()-startTime),
					fileId);
		}
		return download(fileId, file);
	}
	
	/**
	 * 从腾讯Cos下载文件
	 * 
	 * @param fileId
	 */
	private void downloadFile(String fileId, String localPath) {
		File downFile = new File(localPath);
		GetObjectRequest getObjectRequest = new GetObjectRequest(GlobalConfig.fileServiceCosBucketName, fileId);
		String etag = null;
		try {
			ObjectMetadata downObjectMeta = getClient().getObject(getObjectRequest, downFile);
			etag=downObjectMeta.getETag();
		}catch(Exception e) {
			logger.warn(e.getMessage(),e);
			throw new AppException("下载文件失败");
		}finally {
			logger.info("downloadFile fileId:{} localPath:{} etag:{}", fileId, localPath, etag);
		}
	}
	
	/**
	 * 
	 * @param fileId
	 */
	public void deleteFile(String fileId) {
		super.delete(fileId);
		try {
			getClient().deleteObject(GlobalConfig.fileServiceCosBucketName, fileId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException("删除文件失败");
		}
	}
	
}
