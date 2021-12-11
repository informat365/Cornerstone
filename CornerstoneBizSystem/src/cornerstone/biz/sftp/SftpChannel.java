package cornerstone.biz.sftp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.sshd.common.io.IoInputStream;

import com.jcraft.jsch.ChannelSftp;

import cornerstone.biz.domain.SftpFile;
import cornerstone.biz.domain.SftpUploadFile;
import cornerstone.biz.ssh.ConnectionInfo;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.SshUtil;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.IOUtil;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 *
 */
public class SftpChannel {
	//
	private static Logger logger=LoggerFactory.get(SftpChannel.class);
	//
	Date createTime;
	public String uuid;
	private ChannelSftp sftp;
	public ConnectionInfo connectionInfo;
	//
	private SftpChannel(ConnectionInfo connectionInfo) {
		this.connectionInfo=connectionInfo;
		createTime=new Date();
		uuid=BizUtil.randomUUID();
	}
	//
	public static SftpChannel create(ConnectionInfo connectionInfo) {
		SftpChannel bean=new SftpChannel(connectionInfo);
		bean.startSftp();
		return bean;
	}
	//
	public void startSftp(){
		logger.info("startSftp connection to {}", DumpUtil.dump(connectionInfo));
		try {
			sftp = SshUtil.sftp(connectionInfo.host, connectionInfo.port,
					connectionInfo.user, connectionInfo.password,connectionInfo.privateKey);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException("连接失败");
		}
	}
	//
	public void quit() {
		sftp.quit();
	}
	//
	public void exit() {
		try {
			sftp.exit();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	//
	public String pwd() {
		try {
			return sftp.pwd();
		} catch (Exception e) {
			logger.error("pwd");
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
	}
	//
	public void cd(String dir) {
		try {
			sftp.cd(dir);
		} catch (Exception e) {
			logger.error("cd {}",dir);
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
	}
	//
	public List<SftpFile> ls(String path) {
		List<SftpFile> list=new ArrayList<>();
		try {
			 Vector<?> vv=sftp.ls(path);
			 list=JSONUtil.fromJsonList(JSONUtil.toJson(vv),SftpFile.class);
		} catch (Exception e) {
			logger.error("ls {}",path);
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}	
		return list;
	}
	//
	public void mkdir(String path) {
		try {
			sftp.mkdir(path);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
	}
	//
	public void rm(String path) {
		try {
			sftp.rm(path);
		} catch (Exception e) {
			logger.error("rm {}",path);
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
	}
	//
	public void rmdir(String dir) {
		try {
			sftp.rmdir(dir);
		} catch (Exception e) {
			logger.error("rmdir {}",dir);
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
	}
	//
	public InputStream get(String filePath) {
		try {
			return sftp.get(filePath);
		} catch (Exception e) {
			logger.error("get {}",filePath);
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
	}
	//
	public void put(InputStream is,String filePath) {
		try {
			sftp.put(is, filePath);
		} catch (Exception e) {
			logger.error("put {}",filePath);
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
	}
	//
	public void rename(String oldpath, String newpath) {
		try {
			sftp.rename(oldpath, newpath);
		} catch (Exception e) {
			logger.error("rename oldpath:{} newpath:{}",oldpath,newpath);
			logger.error(e.getMessage(),e);
		}
	}
}