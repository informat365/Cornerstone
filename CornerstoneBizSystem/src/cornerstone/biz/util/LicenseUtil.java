package cornerstone.biz.util;

import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashSet;

import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 *
 */
public class LicenseUtil {
	//
	public static final String RSAPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyKBn7C+YCM5TS7qdupbgFMJhVPQGeGzBSdImkwiNaXW4NKQorq7DVj4F7gmg7V1l9Ixc1ffJrMiLhn9LnmoazcHKKCXjalphZ25/u5UPDBXY6zF8Qx4/cKCxqNXka8JK6kqbgmrcN51q/jdoS3SGSDaJ+7Rq46Bh1tCntIn6rp2IWFi8xMryQxsDUlZa8JD4DszqylXWbZrG2NyOddL6u4QaglPrUG3oRAHpqFTi9ZjEefZMxpJT4IeoMys9EkLygQd5EDcif9bsVGtuZ+I0JxcROWCrQi4wjlS8nS6ATtiXwXfvyDfVKKqhig1wHsTsMLsSmrsZm6+P21fyDgAbdQIDAQAB";
	//LICENSE模块列表
	//
	private static Logger logger=LoggerFactory.get(LicenseUtil.class);
	//
	public static class License{
		//
		public static final String MODULE_DEVOPS="DEVOPS";//DEVOPS
		public static final String MODULE_WORKFLOW="WORKFLOW";//流程
		public static final String MODULE_CMDB="CMDB";//CMDB
		public static final String MODULE_CODEGEN="CODEGEN";//代码助手
		public static final String MODULE_FILE="FILE";//文件
		public static final String MODULE_WIKI="WIKI";//WIKI
		public static final String MODULE_REPORT="REPORT";//汇报
		public static final String MODULE_DISCUSS="DISCUSS";//讨论
		public static final String MODULE_PROJECTSET="PROJECTSET";//项目集
		//
		public String id;//合同编号:Trail-20200401-02 带Trail就是试用版
		public String companyName;
		public Date startDate;
		public Date expireDate;
		public int maxUserNum;
		public LinkedHashSet<String> moduleList;//模块列表
	}
	//
	public static License decryptByPublicKey(String licenese) {
		try {
			licenese=licenese.replaceAll(" ", "");
			licenese=licenese.replaceAll("\n", "");
			byte[] decode=null;
			if(licenese.indexOf("=")==-1&&licenese.indexOf("-")==-1&&licenese.indexOf("_")==-1) {
				logger.info("Base62 decode start");
				decode=Base62.createInstance().decode(licenese.getBytes());
			}else {
				logger.info("Base64 decode start");
				decode=Base64.getUrlDecoder().decode(licenese.getBytes());
			}
			decode=RSAUtils.decryptByPublicKey(decode,RSAPublicKey);
			String json=new String(decode);
			License license=JSONUtil.fromJson(json, License.class);
			if(logger.isInfoEnabled()) {
				logger.info("decryptByPublicKey license:{}",DumpUtil.dump(license));
			}
			return license;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("decryptByPublicKey failed.licenese:{}END",licenese);
			throw new AppException(e.getMessage());
		}
	}
}
