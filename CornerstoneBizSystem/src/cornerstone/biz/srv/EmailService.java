package cornerstone.biz.srv;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 *
 * @author icecooly
 *
 */
public class EmailService {
	//
	private static Logger logger = LoggerFactory.getLogger(EmailService.class);
	//
	//
	static class MyAuthenricator extends Authenticator {
		String u = null;
		String p = null;

		public MyAuthenricator(String u, String p) {
			this.u = u;
			this.p = p;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(u, p);
		}
	}

	/**
	 *
	 * @param toMails		;分隔
	 * @param subject
	 * @param content
	 * @throws AppException
	 */
	public void sendMail(String toMails,String subject, String content) throws AppException{
		sendMail(toMails,null, subject, content, null);
	}

	/**
	 *
	 * @param toMails ;分隔
	 * @param ccMails
	 * @param subject
	 * @param content
	 * @throws AppException
	 */
	public void sendMail(String toMails,String ccMails,String subject, String content) throws AppException{
		sendMail(toMails,ccMails,subject, content, null);
	}

	/**
	 *
	 * @param toMails	;分隔
	 * @param ccMails
	 * @param subject
	 * @param content
	 * @param attachments
	 * @throws AppException
	 */
	public void sendMail(String toMails,String ccMails,String subject, String content,List<File> attachments) throws AppException{
		if(StringUtil.isEmpty(toMails)) {
			throw new AppException("接收人邮件不能为空");
		}
		String[] toMailList=toMails.split(";");
		InternetAddress[] to=new InternetAddress[toMailList.length];
		int i=0;
		try {
			for (String toMail : toMailList) {
				InternetAddress addr=new InternetAddress(toMail);
				to[i++]=addr;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}finally {
			logger.debug("sendMail toMails:{} subject:{} content:{}",toMails,subject,content);
		}
		//
		InternetAddress[] cc=null;
		if(!StringUtil.isEmpty(ccMails)) {
			String[] ccMailList=ccMails.split(";");
			cc=new InternetAddress[ccMailList.length];
			i=0;
			try {
				for (String ccMail : ccMailList) {
					InternetAddress addr=new InternetAddress(ccMail);
					cc[i++]=addr;
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		sendMail(to, cc, subject, content,attachments);
	}
	//
	public void sendMail(InternetAddress[] toMails,InternetAddress[] ccMails,
			String subject, String content) throws AppException{
		sendMail(toMails, ccMails, subject, content, null);
	}
	//
	public void sendMail(InternetAddress[] toMails,InternetAddress[] ccMails,
			String subject, String content,List<File> attachments) throws AppException{
		long startTime=System.currentTimeMillis();
		String account=GlobalConfig.emailServiceAccount;
		String accountName=GlobalConfig.emailServiceAccountName;
		String passord=GlobalConfig.emailServicePassword;
		String host=GlobalConfig.emailServiceHost;
		int port=GlobalConfig.emailServicePort;//25
		String protocol=GlobalConfig.emailServiceProtocol;//smtp
		try {
			Properties prop = new Properties();
			prop.setProperty("mail.transport.protocol", protocol);
			prop.setProperty("mail.smtp.host", host);
			prop.setProperty("mail.smtp.socketFactory.port", "465");
			prop.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			prop.setProperty("mail.smtp.port", port+"");
			prop.setProperty("mail.smtp.auth", "true");
			//
			Session session = Session.getInstance(prop, new MyAuthenricator(account, passord));
			session.setDebug(true);
			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setFrom(new InternetAddress(account,accountName));
			if(toMails!=null) {
				mimeMessage.addRecipients(Message.RecipientType.TO, toMails);
			}
			if(ccMails!=null) {
				mimeMessage.addRecipients(Message.RecipientType.CC, ccMails);
			}
			if(toMails==null&&ccMails==null) {
				logger.warn("no receiver email found subject:{}",subject);
				return;
			}
			//
			if(attachments!=null&&attachments.size()>0) {
				Multipart multipart = new MimeMultipart();
				BodyPart contentPart = new MimeBodyPart();
	            contentPart.setContent(content, "text/html;charset=UTF-8");
	            multipart.addBodyPart(contentPart);
	            //
	            for (File attachment : attachments) {
       			 	BodyPart attachmentBodyPart = new MimeBodyPart();
	                DataSource source = new FileDataSource(attachment);
	                attachmentBodyPart.setDataHandler(new DataHandler(source));
	                attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
	                multipart.addBodyPart(attachmentBodyPart);
	            }
				mimeMessage.setContent(multipart);
			}else {
				mimeMessage.setContent(content, "text/html; charset=utf-8");
			}
			//
			mimeMessage.setSubject(subject);
			mimeMessage.setSentDate(new Date());
			mimeMessage.saveChanges();
			Transport.send(mimeMessage);
			logger.info("sendMail success using {}ms toMail:{} subject:{}",
					System.currentTimeMillis()-startTime,
					dumpInternetAddresss(toMails),
					subject);
		}catch (Exception e) {
			logger.error(e.getMessage()+",fail mail:"+dumpInternetAddresss(toMails));
			throw new AppException(e.getMessage());
		}
	}
	
	private String dumpInternetAddresss(InternetAddress[] addressList) {
		StringBuilder sb=new StringBuilder();
		if(addressList==null) {
			return "";
		}
		for (InternetAddress e : addressList) {
			sb.append(e.getAddress()).append(";");
		}
		if(sb.length()>0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
}