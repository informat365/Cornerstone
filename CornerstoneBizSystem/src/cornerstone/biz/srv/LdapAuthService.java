package cornerstone.biz.srv;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import cornerstone.biz.domain.GlobalConfig;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;


/**
 * 
 * @author cs
 *
 */
public class LdapAuthService implements AuthService{
	//
	private static Logger logger=LoggerFactory.get(LdapAuthService.class);
	//
	@Override
	public Attributes auth(String userName, String password) {
		Properties env = new Properties();
		String adminName = userName+GlobalConfig.authLdapUserNameSuffix;//"ayan@itit.local";//username@domain
		String adminPassword = password;//password
		String ldapURL = GlobalConfig.authLdapUrl;//"LDAP://127.0.0.1:389"
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
		env.put(Context.SECURITY_PRINCIPAL, adminName);
		env.put(Context.SECURITY_CREDENTIALS, adminPassword);
		env.put(Context.PROVIDER_URL, ldapURL);
		String searchFilter = String.format(GlobalConfig.authLdapSearchFilter, userName);//"(&(objectCategory=person)(objectClass=user)(sAMAccountName="+userName+"))";
		String searchBase = GlobalConfig.authLdapSearchBase;//
		String returnedAtts[] = new String[0];
		if(GlobalConfig.authLdapReturnedAtts!=null) {
			returnedAtts= GlobalConfig.authLdapReturnedAtts.split(",");
		}
		try {
			LdapContext ctx = new InitialLdapContext(env, null);
			SearchControls searchCtls = new SearchControls();
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			searchCtls.setReturningAttributes(returnedAtts);
			NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();
				return sr.getAttributes();
			}
			throw new AppException("账号没有属性");
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException("用户名或密码错误");
		}finally {
			logger.info("auth ldapURL:{} adminName:{} searchFilter:{} searchBase:{} returnedAtts:{}",
					ldapURL,adminName,searchFilter,searchBase,DumpUtil.dump(returnedAtts));
		}
	}





}
