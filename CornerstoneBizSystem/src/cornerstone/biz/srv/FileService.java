package cornerstone.biz.srv;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.UUID;

import cornerstone.biz.domain.FileInfo;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.IOUtil;
import org.apache.http.util.TextUtils;

/**
 * @author cs
 */
public abstract class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    //
    private static File homeDir = new File(GlobalConfig.fileServiceHomePath);

    /**
     *
     */
    public void init() {
        FileService.homeDir = new File(GlobalConfig.fileServiceHomePath);
    }


    /**
     * 上传文件
     *
     * @return
     */
    public FileInfo upload(InputStream is, String fileName) {
        FileInfo info = new FileInfo();
        info.fileId = UUID.randomUUID().toString().replaceAll("-", "");
        String fileSuffix = "";
        if (!StringUtil.isEmpty(fileName)) {
            int pos = fileName.lastIndexOf(".");
            if (pos != -1) {
                fileSuffix = fileName.substring(pos, fileName.length()).toLowerCase();
            }
        }
        info.fileId += fileSuffix;
        String parent = info.fileId.charAt(0) + "";
        String child = info.fileId.charAt(1) + "";
        File file = null;
        try {
            String filePath = parent + File.separator + child + File.separator + info.fileId;
            file = new File(homeDir, filePath);
            if (!file.getParentFile().exists()) {//有时候没创建成功，都是因为权限问题，目录是ROOT创建的
                file.getParentFile().mkdirs();
                if (logger.isInfoEnabled()) {
                    logger.info("mkdirs file:{} parentFile:{}",
                            file.getAbsolutePath(), file.getParentFile().getAbsolutePath());
                }
            }
            saveContent(is, file);
            info.fileAbsolutePath = file.getAbsolutePath();
            info.length = file.length();
            return info;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (file != null && file.exists()) {
                file.delete();
            }
            throw new AppException(e.getMessage());
        }
    }

    /**
     * 保存分片文件
     */
    public void saveChunkFile(InputStream is, String identifier, int chunkNumber) {
        FileOutputStream fos = null;
        try {
            File tmpParent = new File(homeDir, "tmp");
            if (!tmpParent.exists()) {
                boolean cp = tmpParent.mkdirs();
                if (!cp) {
                    throw new AppException("分片文件上传失败,请确认磁盘读写权限，"+tmpParent.getPath());
                }
            }
            File tmpFile = new File(tmpParent, identifier + "_" + chunkNumber);
            fos = new FileOutputStream(tmpFile);
            IOUtil.copy(is, fos);
            if (!tmpFile.exists()) {
                logger.error("can't save chunk file:" + tmpFile.getAbsolutePath());
                throw new AppException("分片文件上传失败");
            }
            logger.info("save chunk  file:{}", tmpFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error(e.getMessage(),e);;
            throw new AppException("分片文件上传失败");
        } finally {
            IOUtil.closeQuietly(is);
            IOUtil.closeQuietly(fos);
        }

    }

    /**
     * 上传分片合并文件
     */
    public FileInfo mergeChunkUpload(String identifier, String fileName, int totaChunks) {
        FileInfo info = new FileInfo();
        String fileId = identifier + fileName.substring(fileName.lastIndexOf("."));
        File tmpParent = new File(homeDir, "tmp");
        String mergeFilePath = tmpParent.getAbsolutePath() + File.separator + fileId;
        String[] chunkFilePaths = new String[totaChunks];
        for (int i = 0; i < totaChunks; i++) {
            chunkFilePaths[i] = tmpParent.getAbsolutePath() + File.separator + identifier + "_" + (i + 1);
        }
        boolean merge = mergeFiles(chunkFilePaths, mergeFilePath);
        if (!merge) {
            throw new AppException("分片文件合并失败");
        }

        //返回新的文件ID
        info.fileId = BizUtil.randomUUID() + fileId.substring(fileId.lastIndexOf("."));
        String parent = info.fileId.charAt(0) + "";
        String child = info.fileId.charAt(1) + "";
        File file = null;
        try {
            String filePath = parent + File.separator + child + File.separator + info.fileId;
            file = new File(homeDir, filePath);
            if (!file.getParentFile().exists()) {
                boolean cp=file.getParentFile().mkdirs();
                if(!cp){
                    throw new AppException("创建父级目录失败,请确认磁盘读写权限，"+file.getParentFile().getPath());
                }
                if (logger.isInfoEnabled()) {
                    logger.info("merge chunk ,mkdirs file:{} parentFile:{}",
                            file.getAbsolutePath(), file.getParentFile().getAbsolutePath());
                }
            }
            File f0 = new File(mergeFilePath);
            boolean rename = f0.renameTo(file);
            if (!rename) {
                throw new AppException("分片文件合并失败,重命名失败");
            }
            info.fileAbsolutePath = file.getAbsolutePath();
            info.length = file.length();
            return info;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (file != null && file.exists()) {
                file.delete();
            }
            throw new AppException(e.getMessage());
        }
    }

    private boolean mergeFiles(String[] fpaths, String resultPath) {
        if (fpaths == null || fpaths.length < 1 || BizUtil.isNullOrEmpty(resultPath)) {
            return false;
        }
        if (fpaths.length == 1) {
            return new File(fpaths[0]).renameTo(new File(resultPath));
        }

        File[] files = new File[fpaths.length];
        for (int i = 0; i < fpaths.length; i++) {
            files[i] = new File(fpaths[i]);
            if (TextUtils.isEmpty(fpaths[i]) || !files[i].exists() || !files[i].isFile()) {
                return false;
            }
        }

        File resultFile = new File(resultPath);
        FileOutputStream fos = null;
        FileChannel resultFileChannel = null;
        try {
            fos = new FileOutputStream(resultFile,true);
            resultFileChannel = fos.getChannel();
            for (int i = 0; i < fpaths.length; i++) {
                try (FileInputStream fis = new FileInputStream(files[i]); FileChannel fc = fis.getChannel()) {
                    resultFileChannel.transferFrom(fc, resultFileChannel.size(), fc.size());
                } catch (IOException e) {
                    logger.error("merge file err", e.getMessage());
                }
            }

        } catch (IOException e) {
            logger.error("merge file err", e.getMessage());
            return false;
        }finally {
            try {
                if (resultFileChannel != null) {
                    resultFileChannel.close();
                }
            } catch (IOException e) {
              logger.error("mergeFiles ERROR",e);
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
              logger.error("mergeFiles ERROR",e);
            }
        }

        for (int i = 0; i < fpaths.length; i++) {
            files[i].delete();
        }

        return true;
    }


    /**
     * @param fileId
     * @return
     */
    public File getFile(String fileId) {
        String parent = "" + fileId.charAt(0);
        String child = "" + fileId.charAt(1);
        String filePath = parent + File.separator + child + File.separator + fileId;
        File file = new File(homeDir, filePath);
        if (!file.exists()) {//本地不存在
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
                if (logger.isInfoEnabled()) {
                    logger.info("mkdirs file:{} parentFile:{}",
                            file.getAbsolutePath(), file.getParentFile().getAbsolutePath());
                }
            }
        }
        return file;
    }

    /**
     * @param fileId
     * @return
     */
    public abstract File download(String fileId);

    /**
     * 下载文件
     *
     * @param fileId
     * @throws FileNotFoundException
     */
    protected File download(String fileId, File file) {
        long startTime = System.currentTimeMillis();
        try {
            if (logger.isInfoEnabled()) {
                logger.info("download success using {}ms fileId:{}",
                        (System.currentTimeMillis() - startTime),
                        fileId);
            }
            return file;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param fileId
     * @throws FileNotFoundException
     */
    public void delete(String fileId) {
        try {
            String parent = "" + fileId.charAt(0);
            String child = "" + fileId.charAt(1);
            String filePath = parent + File.separator + child + File.separator + fileId;
            File file = new File(homeDir, filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param is
     * @param file
     * @throws IOException
     */
    protected void saveContent(InputStream is, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            IOUtil.copy(is, fos);
        }
    }


}
