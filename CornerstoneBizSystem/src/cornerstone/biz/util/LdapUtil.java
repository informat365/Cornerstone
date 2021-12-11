package cornerstone.biz.util;


import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.GlobalConfig;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AD域账号登录验证及状态同步工具类
 * LdapAuthService类登录失败，先用这个
 */
public class LdapUtil {

    private static Logger logger = LoggerFactory.getLogger(LdapUtil.class);

    private static DirContext ctx = null;

    private LdapUtil() {
    }


    /**
     * 登录认证，基本信息获取
     */
    public static Map<String, Object> auth(String userName, String password) {
        Map<String, Object> result = new HashMap<>();
        boolean ldapEnable = Boolean.parseBoolean(GlobalConfig.getValue("auth.ldap.enable", "false"));
        if (!ldapEnable) {
            throw new AppException("暂不支持AD域账号登录");
        }
        //通过ldap登录
        Hashtable<String, String> ldap = new Hashtable<>();
        ldap.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldap.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        //ad域地址
        ldap.put(Context.PROVIDER_URL, GlobalConfig.getValue("auth.ldap.url"));
        //ad域登录用户
        String adName = userName;
        if (!adName.contains("@")) {
            adName = adName + "@" + GlobalConfig.getValue("auth.ldap.userNameSuffix");
        }
        String dn = GlobalConfig.getValue("auth.ldap.dn","%s");
        ldap.put(Context.SECURITY_PRINCIPAL, String.format(dn,adName));
        //ad域登录密码
        ldap.put(Context.SECURITY_CREDENTIALS, password);

        try {
            logger.debug("ldap auth info--->{}", DumpUtil.dump(ldap));
            //登录验证
            ctx = new InitialDirContext(ldap);
            String searchFilter =GlobalConfig.getValue( "auth.ldap.searchFilter","(&(objectclass=user)(userprincipalname=%s))");
            if (!adName.contains("@")) {
                adName = adName + "@" + GlobalConfig.getValue("auth.ldap.userNameSuffix");
            }
            result.put("adName", adName);
            searchFilter = String.format(searchFilter, adName);
            //搜索控制器
            SearchControls searchCtls = new SearchControls();
            //创建搜索控制器
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String atts = GlobalConfig.getValue("auth.ldap.returnedAtts");
            if (!BizUtil.isNullOrEmpty(atts)) {
                searchCtls.setReturningAttributes(atts.trim().split(","));
            }
            //域节点组织OU="组织名称"("OU="",DC="",DC="",DC="",DC="";)
            String searchBase = GlobalConfig.getValue("auth.ldap.searchBase","");
            //根据设置的域节点、过滤器类和搜索控制器搜索LDAP得到结果
            NamingEnumeration<? extends SearchResult> answer = ctx.search(searchBase, searchFilter, searchCtls);
            logger.debug("ldap auth info--->searchBase:{},searchFilter:{},searchCtls:{}", searchBase, searchFilter, JSONUtil.toJson(searchCtls));
            while (answer.hasMoreElements()) {
                SearchResult result1 = answer.next();
                NamingEnumeration<? extends Attribute> attrs = result1.getAttributes().getAll();
                while (attrs.hasMore()) {
                    Attribute attr = attrs.next();
                    result.put(attr.getID(), attr.get());
                }
            }
        } catch (ServiceUnavailableException e) {
            logger.error(e.getMessage(),e);;
            logger.error("ldap auth fail ", e);
            throw new AppException("AD域服务暂不可用");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);;
            logger.error("ldap auth fail ", e);
            throw new AppException("AD域账号密码错误或账号被禁用");
        } finally {
            try {
                if(null!=ctx){
                    ctx.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }
        return result;
    }

    /**
     * 查询AD域下的账号列表
     */
    public static List<Account> getAdAccounts() {
        List<Account> accountList = new ArrayList<>();
        boolean ldapEnable = Boolean.parseBoolean(GlobalConfig.getValue("auth.ldap.enable", "false"));
        if (!ldapEnable) {
            return accountList;
        }
        Hashtable<String, String> ldap = new Hashtable<>();
        ldap.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldap.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        //ad域地址
        ldap.put(Context.PROVIDER_URL, GlobalConfig.getValue("auth.ldap.url"));
        //ad域登录用户
        String dn = GlobalConfig.getValue("auth.ldap.dn","%s");
        ldap.put(Context.SECURITY_PRINCIPAL, String.format(dn,GlobalConfig.getValue("auth.ldap.admin")));
        //ldap.put(Context.SECURITY_PRINCIPAL, GlobalConfig.getValue("auth.ldap.admin", "pengyao"));
        //ad域登录密码
        ldap.put(Context.SECURITY_CREDENTIALS, GlobalConfig.getValue("auth.ldap.password"));
        try {
            logger.debug("ldap  account info--->{}", DumpUtil.dump(ldap));
            ctx = new InitialDirContext(ldap);
            //LDAP搜索过滤器类
//            String searchFilter = "objectClass=User";
            String searchFilter = GlobalConfig.getValue("auth.ldap.searchFilter");
            if (BizUtil.isNullOrEmpty(searchFilter)) {
                searchFilter = "(&(objectclass=user))";
            }
//            "(&(objectclass=user)(userprincipalname=pengyao@itit.cn))";
            //搜索控制器
            SearchControls searchCtls = new SearchControls();
            //创建搜索控制器
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String atts = GlobalConfig.getValue("auth.ldap.returnedAtts");
            if (!BizUtil.isNullOrEmpty(atts)) {
                searchCtls.setReturningAttributes(atts.trim().split(","));
            }
            //域节点组织OU="组织名称"("OU="",DC="",DC="",DC="",DC="";)
            String searchBase = GlobalConfig.getValue("auth.ldap.searchBase", "");
            //根据设置的域节点、过滤器类和搜索控制器搜索LDAP得到结果
            NamingEnumeration<? extends SearchResult> answer = ctx.search(searchBase, searchFilter, searchCtls);
            logger.debug("ldap account info--->searchBase:{},searchFilter:{},searchCtls:{}", searchBase,searchFilter, JSONUtil.toJson(searchCtls));

            while (answer.hasMoreElements()) {
                Account account = new Account();
                SearchResult result1 = answer.next();
                NamingEnumeration<? extends Attribute> attrs = result1.getAttributes().getAll();
                while (attrs.hasMore()) {
                    Attribute attr = attrs.next();
                    if ("userPrincipalName".equalsIgnoreCase(attr.getID())) {
                        account.adName = attr.get().toString();
                    } else if ("userAccountControl".equalsIgnoreCase(attr.getID())) {
                        account.status = "514".equals(attr.get()) ? Account.STATUS_无效 : Account.STATUS_有效;
                    }
                }
                accountList.add(account);
            }
            ctx.close();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        if (!BizUtil.isNullOrEmpty(accountList)) {
            accountList = accountList.stream().filter(k -> !BizUtil.isNullOrEmpty(k.adName)).collect(Collectors.toList());
        }
        return accountList;
    }



}
