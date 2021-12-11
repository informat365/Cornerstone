package cornerstone.biz.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * @author cs
 */
public class FileDownloadUtil {
    //
    private static Logger logger = LoggerFactory.get(FileDownloadUtil.class);
    //

    /**
     * 批量下载文件
     *
     * @param res
     * @return
     * @throws IOException
     */
    public static String downloadFileList(String srcPath, HttpServletResponse res) {
        //压缩文件
        String fileName = UUID.randomUUID().toString();
        File zip = null;
        OutputStream out = null;
        try {
            zip = File.createTempFile(fileName, ".zip");
            if (logger.isDebugEnabled()) {
                logger.debug("downloadFileList fileName:{} srcPath:{} zip:{}",
                        fileName, srcPath, zip.getAbsolutePath());
            }
            compress(srcPath, zip);
            //设置下载的压缩文件名称
            res.setHeader("Content-disposition", "attachment;filename=" + zip.getName());
            //将打包后的文件写到客户端，输出的方法同上，使用缓冲流输出
            byte[] buff;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zip))) {
                buff = new byte[bis.available()];
                if (null != bis&&null!=buff) {
                     bis.read(buff);
                }
            }
            //创建页面返回方式为输出流，会自动弹出下载框
            out = res.getOutputStream();
            out.write(buff);//输出数据文件
        } catch (IOException e) {
            logger.error("downloadFileList ERR", e);
        } finally {
            if (null != out) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    ;
                }
            }
            if (null != zip) {
                zip.delete();
            }
        }
        return null;

    }

    static final int BUFFER = 8192;

    /**
     * @param srcPath
     * @throws IOException
     */
    public static void compress(String srcPath, File dstFile) throws IOException {
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            throw new FileNotFoundException(srcPath + "不存在！");
        }
        FileOutputStream out = null;
        ZipOutputStream zipOut = null;
        try {
            out = new FileOutputStream(dstFile);
            CheckedOutputStream cos = new CheckedOutputStream(out, new CRC32());
            zipOut = new ZipOutputStream(cos);
            String baseDir = "";
            compress(srcFile, zipOut, baseDir);
        } finally {
            if (null != zipOut) {
                zipOut.close();
                out = null;
            }
            if (null != out) {
                out.close();
            }
        }
    }

    private static void compress(File file, ZipOutputStream zipOut, String baseDir) throws IOException {
        if (file.isDirectory()) {
            compressDirectory(file, zipOut, baseDir);
        } else {
            compressFile(file, zipOut, baseDir);
        }
    }

    /**
     * 压缩一个目录
     */
    private static void compressDirectory(File dir, ZipOutputStream zipOut, String baseDir) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                compress(file, zipOut, baseDir + dir.getName() + "/");
            }
        }
    }

    /**
     * 压缩一个文件
     */
    private static void compressFile(File file, ZipOutputStream zipOut, String baseDir) throws IOException {
        if (!file.exists()) {
            return;
        }

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(baseDir + file.getName());
            zipOut.putNextEntry(entry);
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                zipOut.write(data, 0, count);
            }

        } finally {
            if (null != bis) {
                bis.close();
            }
        }
    }
}
