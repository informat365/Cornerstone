package cornerstone.biz.srv;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.unboundid.ldap.listener.Base64PasswordEncoderOutputFormatter;
import com.unboundid.ldap.listener.ClearInMemoryPasswordEncoder;
import com.unboundid.ldap.listener.HexPasswordEncoderOutputFormatter;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.UnsaltedMessageDigestInMemoryPasswordEncoder;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFException;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;


/**
 * 
 * @author cs
 *
 */
public class LdapService {
	//
	private static Logger logger=LoggerFactory.get(LdapService.class);
	//
	InMemoryDirectoryServer server;
	//
	@AutoWired
	BizDAO dao;
	//
	public LdapService() {
		
	}
	//CN=Organizational-Unit,CN=Schema,CN=Configuration,DC=ncbank,DC=cn
	public void init()  {
		String ldapUser=GlobalConfig.getValue("ldap.credentials.user");
		if(StringUtil.isEmpty(ldapUser)) {
			return;
		}
		try {
			this.server=newEmbeddedServer(
					GlobalConfig.getValue("ldap.credentials.user"),
					GlobalConfig.getValue("ldap.credentials.password"),
					Integer.parseInt(GlobalConfig.getValue("ldap.credentials.port")));
		} catch (Exception e) {
			logger.error("init ERROR",e);
		}
		reloadAccount();
	}
	//
	public void reloadAccount() {
		List<Account> list=dao.getAllPrivateDeployCompanyAccountList();
		for (Account account : list) {
			addAccount(account.userName, account.password);
			logger.info("reloadAccount account.userName:{}",account.userName);
		}
		logger.info("reloadAccount list size:{} ",list.size());
	}


	//账号导入到AD域中
	public void addAccount(String userName,String password) {
		if(server==null) {
			return;
		}
		try {
			String dn="cn="+userName+",dc=users,dc=itit,dc=io";
			if(server.getEntry(dn)!=null) {
				server.delete(dn);
			}
			password = "{MD5}" + password;
			server.add("dn: cn="+userName+",dc=users,dc=itit,dc=io", 
					"objectClass: account", 
					"cn:"+userName,
					"uid:"+userName, 
					"userPassword:" + password);
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
	//
	/**
	 * 
	 * @param userName
	 * @param userPassword
	 * @param port
	 */
	public InMemoryDirectoryServer newEmbeddedServer(String userName,String userPassword,int port) throws LDAPException, NoSuchAlgorithmException, LDIFException {
		InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig("dc=io");
		config.addAdditionalBindCredentials(userName, userPassword);
		final MessageDigest sha1Digest = MessageDigest.getInstance("MD5");
	    config.setPasswordEncoders(
	         new ClearInMemoryPasswordEncoder("{CLEAR}", null),
	         new ClearInMemoryPasswordEncoder("{HEX}",
	              HexPasswordEncoderOutputFormatter.getLowercaseInstance()),
	         new ClearInMemoryPasswordEncoder("{BASE64}",
	              Base64PasswordEncoderOutputFormatter.getInstance()),
	         new UnsaltedMessageDigestInMemoryPasswordEncoder("{MD5}",
	        		 HexPasswordEncoderOutputFormatter.getLowercaseInstance(), sha1Digest));
		
		config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("default", port));
		config.setSchema(null);
		InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
		ds.startListening();
		ds.add("dn: dc=io", "objectClass: top", "objectClass: domain", "dc: io");
		ds.add("dn: dc=itit,dc=io", "objectClass: top", "objectClass: domain", "dc: itit");
		ds.add("dn: dc=users,dc=itit,dc=io", "objectClass: top", "objectClass: domain", "dc: users");
		//

		return ds;
	}
}
