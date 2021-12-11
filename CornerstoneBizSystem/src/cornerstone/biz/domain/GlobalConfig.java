package cornerstone.biz.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cornerstone.biz.util.StringUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.misc.InfoBuilder;

/**
 * @author icecooly
 *
 */
public class GlobalConfig {
	//
	private static Logger logger=LoggerFactory.get(GlobalConfig.class);
	//
	public static String webUrl;//最后以/结尾
	public static String pdfUrl;//最后以/结尾
	public static boolean isPrivateDeploy;
	public static String mysqlDBUrlParams;
	public static String mysqlDriverClass;
	//文件服务器跟路径
	public static String fileServiceHomePath="file_storage";
	//腾讯云cos
	public static long   fileServiceCosAppId;
	public static String fileServiceCosSecretId;
	public static String fileServiceCosSecretKey;
	public static String fileServiceCosBucketName;
	public static String fileServiceCosRegion;
	public static String fileServiceCosRootPath;
	public static String fileServiceType;//见FileServiceManager.java的定义 默认cos
	public static boolean fileserviceSaveLocal;
	//
	public static String emailServiceAccount;
	public static String emailServiceAccountName;
	public static String emailServicePassword;
	public static String emailServiceHost;
	public static String emailServiceProtocol;
	public static int emailServicePort;
	//
	/**微信公众号appid*/
	public static String appId="";//"";
	/**微信公众号appSecret*/
	public static String appSecret="";//"";
	//
	/**飞书appid*/
	public static String larkAppId="";//"";
	/**飞书appSecret*/
	public static String larkAppSecret="";//"";
	/**飞书appType*/
	public static String larkAppType="";//"";
	//
	public static String qcloundSmsAppId;
	public static String qcloundSmsAppKey;
	//
	public static boolean authLdapEnable;
	public static String authLdapUrl;//LDAP://10.25.8.232:389
	public static String authLdapUserNameSuffix;//用户名后缀 比如"@itit.local
	public static String authLdapSearchFilter;//"(&(objectCategory=person)(objectClass=user)(sAMAccountName="+userName+"))"
	public static String authLdapSearchBase;//"DC=ITIT,DC=local";
	public static String authLdapReturnedAtts;//{"memberOf","displayName"};

	//office
	public static String owaUrl;//office预览地址https://view.officeapps.live.com  注意最后没有/
	public static String officeType;//office预览操作类型，wps、office、pageoffice/
	public static boolean officeEditable;// office是否支持编辑
	//
	/**是否默认禁用邮件通知*/
	public static boolean isMailNotificationAutoDisabled;
	/**是否默认禁用微信通知*/
	public static boolean isWechatNotificationAutoDisabled;
	//企业微信
	public static String qywxCorpId;
	public static String qywxCorpSecret;
	public static String qywxProviderSecret;
	public static String qywxToken;
	public static String qywxEncodingAESKey;
	public static String qywxAppId;
	public static String qywxAppSecret;
	public static String qywxAppToken;
	public static String qywxAppEncodingAESKey;
	//
	//钉钉
	public static String dingtalkCorpId;
//	public static String dingtalkCorpSecret;
	public static String dingtalkAppKey;
	public static String dingtalkAppSecret;
	public static String dingtalkAgentId;
	public static String dingtalkMobileAppId; // 移动应用APPID(扫码登陆)
	public static String dingtalkMobileAppSecret; // 移动应用AppSecret(扫码登陆)
//	public static String dingtalkAppToken;
	//
	private static List<Config>configs=new ArrayList<>();
	//
	public synchronized static void setupConfig(List<Config>configs){
		if(configs==null) {
			configs=new ArrayList<>();
		}
		GlobalConfig.configs=configs;
		webUrl=getValue("system.web.url");
		pdfUrl=getValue("system.pdf.url","http://localhost:8888/");
		qcloundSmsAppId=getValue("system.qcloundSmsAppId");
		qcloundSmsAppKey=getValue("system.qcloundSmsAppKey");
		isPrivateDeploy=Boolean.parseBoolean(getValue("system.privateDeploy"));
		fileServiceHomePath=getValue("fileservice.homePath","file_storage");
		fileServiceCosAppId=getLongValue("fileservice.cos.appId");
		fileServiceCosSecretId=getValue("fileservice.cos.secretId");
		fileServiceCosSecretKey=getValue("fileservice.cos.secretKey");
		fileServiceCosBucketName=getValue("fileservice.cos.bucketName");
		fileServiceCosRegion=getValue("fileservice.cos.region");
		fileServiceCosRootPath=getValue("fileservice.cos.rootPath");
		fileserviceSaveLocal=getBooleanValue("fileservice.saveLocal");
		fileServiceType = getValue("fileservice.type","local");
		
		emailServiceAccount=getValue("emailservice.account");
		emailServiceAccountName=getValue("emailservice.accountName");
		emailServicePassword=getValue("emailservice.password");
		emailServiceHost=getValue("emailservice.host");
		emailServiceProtocol=getValue("emailservice.protocol");
		emailServicePort=getIntValue("emailservice.port");
		
		appId=getValue("wx.appId");
		appSecret=getValue("wx.appSecret");
		
		larkAppId=getValue("lark.appId");
		larkAppSecret=getValue("lark.appSecret");
		larkAppType=getValue("lark.appType");
		
		authLdapEnable=getBooleanValue("auth.ldap.enable");
		authLdapUrl=getValue("auth.ldap.url");
		authLdapUserNameSuffix=getValue("auth.ldap.userNameSuffix");
		authLdapSearchFilter=getValue("auth.ldap.searchFilter");
		authLdapSearchBase=getValue("auth.ldap.searchBase");
		authLdapReturnedAtts=getValue("auth.ldap.returnedAtts");

		//office
		owaUrl=getValue("owa.url");
		officeType=getValue("owa.type");
		officeEditable=getBooleanValue("owa.editable");
		//
		isMailNotificationAutoDisabled=getBooleanValue("account.isMailNotificationAutoDisabled");
		isWechatNotificationAutoDisabled=getBooleanValue("account.isWechatNotificationAutoDisabled");
		//mysql
		mysqlDBUrlParams=getValue("mysql.dbUrlParams", "useSSL=false&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&useUnicode=true&characterEncoding=UTF-8");
		mysqlDriverClass=getValue("mysql.driverClass","com.mysql.cj.jdbc.Driver");
		//
		qywxCorpId=getValue("qywx.corpId");
		qywxCorpSecret=getValue("qywx.corpSecret");
		qywxProviderSecret=getValue("qywx.providerSecret");
		qywxToken=getValue("qywx.token");
		qywxEncodingAESKey=getValue("qywx.encodingAESKey");
		qywxAppId=getValue("qywx.appId");
		qywxAppSecret=getValue("qywx.appSecret");
		qywxAppToken=getValue("qywx.appToken");
		qywxAppEncodingAESKey=getValue("qywx.appEncodingAESKey");
		//
		dingtalkCorpId = getValue("dingtalk.corpId");
//		dingtalkCorpSecret = getValue("dingtalk.corpSecret");
		dingtalkAppKey = getValue("dingtalk.appKey");
		dingtalkAppSecret = getValue("dingtalk.appSecret");
		dingtalkAgentId = getValue("dingtalk.agentId");
		dingtalkMobileAppId = getValue("dingtalk.mobile.appId");
		dingtalkMobileAppSecret = getValue("dingtalk.mobile.appSecret");
//		dingtalkAppToken = getValue("dingtalk.dingtalkAppToken");
//		if(logger.isInfoEnabled()){
//			logger.info("setupConfig configs:"+dump());
//		}
	}
	//
	public static void setValue(String name,String value){
		Config config=new Config();
		config.name=name;
		config.value=value;
		configs.add(config);
	}
	//
	public static String getValue(String name){
		for(Config c:configs){
			if(c.name.equals(name)){
				return c.value;
			}
		}
		return null;
	}
	
	public static String getValue(String name,String defualtValue){
		/*for(Config c:configs){
			if(c.name.equals(name)){
				return c.value;
			}
		}*/
		String value = getValue(name);
		if(!StringUtil.isEmptyWithTrim(value)){
			return value;
		}
		return defualtValue;
	}
	
	public static int getIntValue(String key) {
		String value=getValue(key);
		if(value==null) {
			return 0;
		}
		return Integer.valueOf(value);
	}
	
	public static boolean getBooleanValue(String key) {
		String value=getValue(key);
		if(value==null) {
			return false;
		}
		return Boolean.valueOf(value);
	}
	
	public static long getLongValue(String key) {
		String value=getValue(key);
		if(value==null) {
			return 0;
		}
		return Long.valueOf(value);
	}
	//
	public static String dump(){
		InfoBuilder ib=InfoBuilder.create();
		try {
			ib.section("GlobalConfig dump info");
			ib.format("%-30s:%-30s\n");
			Field[] fields=GlobalConfig.class.getFields();
			for (Field field : fields) {
				ib.print(field.getName(),field.get(GlobalConfig.class));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return ib.toString();
	}
	//
	public static List<Config> getConfigs(){
		return configs;
	}
	//
}
