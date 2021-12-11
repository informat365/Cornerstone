package cornerstone.biz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.jcraft.jsch.*;

import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.FileUtil;

/**
 * @author yama 7 Jan, 2015
 */
public class SshUtil {
    //
    public static int DEFAULT_TIMEOUNT = 15000;
    //
    private static Logger logger = LoggerFactory.get(SshUtil.class);

    //
    public static class MyUserInfo implements UserInfo,
            UIKeyboardInteractive {
        //
        public String password;

        //
        public MyUserInfo() {

        }

        //
        public MyUserInfo(String password) {
            this.password = password;
        }

        //
        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public boolean promptYesNo(String str) {
            return true;
        }

        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public boolean promptPassphrase(String message) {
            return false;
        }

        @Override
        public boolean promptPassword(String message) {
            return true;
        }

        @Override
        public void showMessage(String message) {
        }

        @Override
        public String[] promptKeyboardInteractive(String destination,
                                                  String name, String instruction, String[] prompt, boolean[] echo) {
            return null;
        }
    }

    //
    private static File createPrivateKeyFile(String privateKey) {
        try {
            File tmpFile = File.createTempFile(UUID.randomUUID().toString(), "key");
            FileUtil.saveContent(privateKey.getBytes(), tmpFile);
            return tmpFile;
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    //
    public static ChannelShell shell(
            String host,
            int port,
            String user,
            String pwd,
            String privateKey,
            int connectTimeout) throws JSchException {
        Session session = getSession(host, port, user, pwd, privateKey);
        ChannelShell channel = (ChannelShell) session.openChannel("shell");
        channel.connect(connectTimeout);
        return channel;
    }

    //
    public static ChannelSftp sftp(
            String host,
            int port,
            String user,
            String pwd,
            String privateKey) throws JSchException {
        Session session = getSession(host, port, user, pwd, privateKey);
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        return channel;
    }

    //
    private static Session getSession(String host,
                                      int port,
                                      String user,
                                      String pwd,
                                      String privateKey) {
        return getSession(host, port, user, pwd, privateKey, DEFAULT_TIMEOUNT);
    }

    //
    private static Session getSession(String host,
                                      int port,
                                      String user,
                                      String pwd,
                                      String privateKey,
                                      int connectTimeout) {
        File privateKeyFile = null;
        try {
            JSch jsch = new JSch();
            if (privateKey != null) {
                privateKeyFile = createPrivateKeyFile(privateKey);
                jsch.addIdentity(privateKeyFile.getAbsolutePath());
            }
            Session session = jsch.getSession(user, host, port);
            if (logger.isDebugEnabled()) {
                logger.debug("getSession {}@{}:{}", user, host, port);
            }
            if (pwd != null) {
                session.setPassword(pwd);
            }
            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            UserInfo ui = new MyUserInfo();
            session.setUserInfo(ui);
            session.connect(connectTimeout);
            return session;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("getSession failed.host:{} port:{} user:{} "
                            + "pwd:{} privateKey:{} connectTimeout:{}",
                    host, port, user, pwd, privateKey, connectTimeout);
            throw new AppException("连接失败【" + e.getMessage() + "】");
        } finally {
            if (privateKeyFile != null) {
                privateKeyFile.delete();
            }
        }
    }

    //
    public static int execute(
            String host,
            int port,
            String user,
            String pwd,
            String privateKey,
            String cmd,
            boolean pty,
            BiConsumer<String, String> callback) {
        try {
            Session session = getSession(host, port, user, pwd, privateKey, DEFAULT_TIMEOUNT);
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            if (pty) {
                channel.setPty(true);
            }
            channel.setCommand(cmd);
            channel.setInputStream(null);
            InputStream in = channel.getInputStream();
            InputStream err = channel.getErrStream();
            channel.connect(DEFAULT_TIMEOUNT);
            int exitCode = 0;
            String outResult = "";
            String errResult = "";
            long totalExecuteTime = 0;
            while (true) {
                outResult = getResult(in);
                errResult = getResult(err);
                if (!outResult.isEmpty() || !errResult.isEmpty()) {
                    callback.accept(outResult, errResult);
                }
                if (channel.isClosed()) {
                    if (in.available() > 0)
                        continue;
                    exitCode = channel.getExitStatus();
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ee) {
                    logger.error(ee.getMessage(), ee);
                }
                totalExecuteTime += 100;
                //
                if (totalExecuteTime >= 1000 * 30 * 60) {
                    break;
                }
            }
            channel.disconnect();
            session.disconnect();
            return exitCode;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e.getMessage());
        }

    }

    //
    private static String getResult(InputStream in) throws IOException {
        byte[] tmp = new byte[1024];
        StringBuilder resultOut = new StringBuilder();
        int available = 0;
        while ((available = in.available()) > 0) {
            if (available > 1024) {
                available = 1024;
            }
            int i = in.read(tmp, 0, available);
            if (i < 0)
                break;
            resultOut.append(new String(tmp, 0, i));
        }
        return resultOut.toString();
    }

    //
    public static void scpFrom(
            String remoteHost,
            int remotePort,
            String remoteUser,
            String remotePwd,
            String remoteFile,
            String localFile) {
        FileOutputStream fos = null;
        logger.info("scpFrom remoteHost:{} remotePort:{} remoteUser:{} "
                        + "remoteFile:{} localFile:{}",
                remoteHost, remotePort, remoteUser, remoteFile, localFile);
        try {
            String prefix = null;
            if (new File(localFile).isDirectory()) {
                prefix = localFile + File.separator;
            }

            JSch jsch = new JSch();
            Session session = jsch.getSession(remoteUser, remoteHost, remotePort);

            UserInfo ui = new MyUserInfo(remotePwd);
            session.setUserInfo(ui);
            session.connect(DEFAULT_TIMEOUNT);

            String command = "scp -f " + remoteFile;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();
            byte[] buf = new byte[1024];

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }

                // read '0644 '
                if (null != in && null != buf) {
                    in.read(buf, 0, 5);
                }
                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        // error
                        break;
                    }
                    if (buf[0] == ' ')
                        break;
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }

                String file = null;
                for (int i = 0; ; i++) {
                    //代码扫描修改
                    if (null != in && null != buf) {
                        in.read(buf, i, 1);
                    }
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }

                // //System.out.println("filesize="+filesize+", file="+file);

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();

                // read a content of lfile
                fos = new FileOutputStream(prefix == null ? localFile : prefix + file);
                int foo;
                while (true) {
                    if (buf.length < filesize)
                        foo = buf.length;
                    else
                        foo = (int) filesize;
                    foo = in.read(buf, 0, foo);
                    if (foo < 0) {
                        // error
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize -= foo;
                    if (filesize == 0L)
                        break;
                }
                fos.close();
                fos = null;

                if (checkAck(in) != 0) {
                    System.exit(0);
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }

            session.disconnect();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    //
    public static void scpTo(
            String host,
            int port,
            String user,
            String pwd,
            String privateKey,
            String localFile,
            String remoteFile,
            BiConsumer<Long, Long> callback) throws AppException {
        FileInputStream fis = null;
        try {
            Session session = getSession(host, port, user, pwd, privateKey);
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("scp -t " + remoteFile);
            channel.setInputStream(null);
            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();
            channel.connect(DEFAULT_TIMEOUNT);
            checkAck(in);
            File lFile = new File(localFile);
            long filesize = lFile.length();
            String command = "C0644 " + filesize + " ";
            if (localFile.lastIndexOf('/') > 0) {
                command += localFile.substring(localFile.lastIndexOf('/') + 1);
            } else {
                command += localFile;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            checkAck(in);
            //
            fis = new FileInputStream(lFile);
            byte[] buf = new byte[1024 * 500];
            long sendLen = 0;
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                sendLen += len;
                if (callback != null) {
                    callback.accept(filesize, sendLen);
                }
                if (len <= 0)
                    break;
                out.write(buf, 0, len); // out.flush();
            }
            fis.close();
            fis = null;
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            checkAck(in);
            out.close();
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    //
    static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        // 1 for error,
        // 2 for fatal error,
        // -1
        if (b == 0)
            return b;
        if (b == -1)
            return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                logger.error(sb.toString());
                throw new AppException(sb.toString());
            }
            if (b == 2) { // fatal error
                logger.error(sb.toString());
                throw new AppException(sb.toString());
            }
        }
        return b;
    }
    //

    /**
     * 本地转发到远程(正向代理)
     */
    public static void setPortForwardingL(String userName, String password, String host, int port, String lHost,
                                          int lPort, String rHost, int rPort) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(userName, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            logger.info(session.getServerVersion());
            // ssh -L lHost:lPort:rHost:rPort userName@host 正向代理
            int assingedPort = session.setPortForwardingL(lHost, lPort, rHost, rPort);
            logger.info("assingedPort:" + assingedPort);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e.getMessage());
        }
    }
}
