package cornerstone.biz.srv;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import cornerstone.biz.domain.FileInfo;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.util.BizUtil;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Amazon s3对象存储服务
 * https://docs.aws.amazon.com/zh_cn/amazonglacier/latest/dev/uploading-an-archive-single-op-using-java.html
 */
public class AmazonS3Service extends FileService {
    //
    private static Logger logger = LoggerFactory.getLogger(AmazonS3Service.class);
    //
    private static AmazonS3 client;
    //
    private static AmazonS3Service instance;

    //
    private AmazonS3Service() {

    }

    //
    public static AmazonS3Service get() {
        if (instance == null) {
            instance = new AmazonS3Service();
        }
        return instance;
    }

    //
    private AmazonS3 getClient() {
        if (null == client) {
            super.init();
            String accessKey = GlobalConfig.getValue("fileservice.s3.accessKey");
            if (BizUtil.isNullOrEmpty(accessKey)) {
                throw new AppException("amazon s3 access key is not exist");
            }
            String secretKey = GlobalConfig.getValue("fileservice.s3.secretKey");
            if (BizUtil.isNullOrEmpty(secretKey)) {
                throw new AppException("amazon s3 secret key is not exist");
            }
//            AwsClientBuilder.EndpointConfiguration configuration = new AwsClientBuilder.EndpointConfiguration(
//                    GlobalConfig.getValue("fileservice.s3.endpoint"),
//                    GlobalConfig.getValue("fileservice.s3.region"));
//
//            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
//
//            client = AmazonS3ClientBuilder.standard()
//                    .withEndpointConfiguration(configuration)
//                    .withCredentials(new ProfileCredentialsProvider())
//                    .build();
//
            // 新建一个连接.
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.setSignerOverride("S3SignerType");
            // 设置协议
            clientConfig.setProtocol(Protocol.HTTP);
            client = new AmazonS3Client(credentials,clientConfig);
            // 服务地址
            client.setEndpoint(GlobalConfig.getValue("fileservice.s3.endpoint"));
        }
        return client;
    }

    @Override
    public FileInfo upload(InputStream is, String fileName) {
        long startTime = System.currentTimeMillis();
        FileInfo info = super.upload(is, fileName);
        uploadFile(info.fileId, info.fileAbsolutePath);
        if (logger.isInfoEnabled()) {
            logger.info("upload success using {}ms fileName:{} filePath:{} info:{}",
                    (System.currentTimeMillis() - startTime),
                    fileName,
                    info.fileAbsolutePath,
                    DumpUtil.dump(info));
        }
        return info;
    }

    /**
     * 上传到Amazon s3
     */
    private void uploadFile(String fileId, String path) {
        File localFile = new File(path);
        try {
            String bucketName = GlobalConfig.getValue("fileservice.s3.bucketName");
            if (BizUtil.isNullOrEmpty(bucketName)) {
                throw new AppException("amazon s3 bucket name is not exist");
            }
            // 设置文件上传对象
            PutObjectRequest request = new PutObjectRequest(bucketName, fileId, localFile);
            // 设置公共读取
            request.withCannedAcl(CannedAccessControlList.PublicRead);
            // 上传文件
            PutObjectResult putObjectResult = getClient().putObject(request);
            logger.debug("s3 upload file result:{}", JSONUtil.toJson(putObjectResult));
        } catch (Exception e) {
            logger.error("upload file err", e);
        }
    }


    /**
     * 下载文件
     */
    @Override
    public File download(String fileId) {
        long startTime = System.currentTimeMillis();
        File file = getFile(fileId);
        if (!file.exists()) {
            downloadFile(fileId, file.getAbsolutePath());
        }
        if (logger.isInfoEnabled()) {
            logger.info("download  s3 success using {}ms fileId:{}",
                    (System.currentTimeMillis() - startTime),
                    fileId);
        }
        return download(fileId, file);
    }

    private void downloadFile(String fileId, String localPath) {
        File downFile = new File(localPath);
        String bucketName = GlobalConfig.getValue("fileservice.s3.bucketName");
        if (BizUtil.isNullOrEmpty(bucketName)) {
            throw new AppException("amazon s3 bucket name is not exist");
        }
        try {
            GeneratePresignedUrlRequest httpRequest = new GeneratePresignedUrlRequest(bucketName, fileId);
            URL downloadUrl = getClient().generatePresignedUrl(httpRequest);
            PresignedUrlDownloadRequest downRequest = new PresignedUrlDownloadRequest(downloadUrl);
            getClient().download(downRequest, downFile);
        } catch (Exception e) {
            logger.error("download file err", e);
        }
    }

    /**
     * @param fileId
     */
    public void deleteFile(String fileId) {
        super.delete(fileId);
        String bucketName = GlobalConfig.getValue("fileservice.s3.bucketName");
        if (BizUtil.isNullOrEmpty(bucketName)) {
            throw new AppException("amazon s3 bucket name is not exist");
        }
        try {
            DeleteObjectRequest deleteRequest = new DeleteObjectRequest(bucketName, fileId);
            getClient().deleteObject(deleteRequest);
        } catch (Exception e) {
            logger.error("delete fail", e);
        }
    }
}
