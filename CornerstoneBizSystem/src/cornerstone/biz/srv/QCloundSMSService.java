package cornerstone.biz.srv;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.http.HttpClientDriver;
import jazmin.driver.http.HttpRequest;
import jazmin.driver.http.HttpResponse;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;

/**
 * https://www.qcloud.com/document/product/382/5808
 * @author yan
 *
 */
public class QCloundSMSService {
	//
	private static Logger logger=LoggerFactory.get(QCloundSMSService.class);
	//
	@AutoWired
	public HttpClientDriver httpClientDriver;
	 
	public QCloundSMSService() {
		
	}
	//
	private void checkAppIdAppKey() {
		if(StringUtil.isEmpty(GlobalConfig.qcloundSmsAppId)) {
			throw new AppException("发送短信失败，请先配置smsAppId");
		}
		if(StringUtil.isEmpty(GlobalConfig.qcloundSmsAppKey)) {
			throw new AppException("发送短信失败，请先配置smsAppKey");
		}
	}
	//
	public void sendSms(String mobile,String content) throws AppException{
		checkAppIdAppKey();
		String random=System.currentTimeMillis()+"";
		String url="https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid="+GlobalConfig.qcloundSmsAppId+"&random="+random;
		String postBody="";
		ArrayList<String> mobiles = new ArrayList<String>();
		mobiles.add(mobile);
		long time = System.currentTimeMillis()/1000;
		String sig = null;
		try {
			sig = calculateSig(GlobalConfig.qcloundSmsAppKey, Long.parseLong(random), content,time , mobiles);
		} catch (Exception e1) {
			logger.error(e1.getMessage(),e1);
			throw new AppException("签名错误");
		} 
		//
		Map<String, Object>reqMap=new HashMap<>();
		reqMap.put("type", "0");
		reqMap.put("msg", content);
		reqMap.put("sig", sig);
		reqMap.put("extend","");
		reqMap.put("time",time);
		reqMap.put("ext", "");
		Map<String,String>codeMap=new HashMap<>();
		codeMap.put("nationcode", "86");
		codeMap.put("mobile", mobile);
		reqMap.put("tel", codeMap);
		postBody=JSONUtil.toJson(reqMap);
		
		HttpRequest req;
		HttpResponse rsp=null;
		try {
			req = httpClientDriver.post(url).setBody(postBody.getBytes("UTF-8"));
			rsp=req.execute().get();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
		int code=rsp.getStatusCode();
		if(code!=200){
			throw new AppException("bad request,code="+code);
		}
		String rspBody="";
		Map<Object,Object>repObject;
		try {
			rspBody=rsp.getResponseBody("UTF-8");
			if(logger.isDebugEnabled()){
				logger.debug("response="+rspBody);
			}
			repObject=JSONUtil.fromJson(rspBody,Map.class);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}finally {
			logger.info("url:{} \npostBody:{}",url,postBody);
		}
		String msg=(String) repObject.get("errmsg");
		String result = String.valueOf(repObject.get("result"));
		if(!"0".equals(result)){
			logger.error("errmsg:"+msg);
			throw new AppException("发送短信失败");
		}
	}
	//
	
	protected String strToHash(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] inputByteArray = str.getBytes();
        messageDigest.update(inputByteArray);
        byte[] resultByteArray = messageDigest.digest();
        return byteArrayToHex(resultByteArray);
    }
	
	public String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }
	
	public String calculateSig(
    		String appkey,
    		long random,
    		String msg,
    		long curTime,    		
    		ArrayList<String> phoneNumbers) throws NoSuchAlgorithmException {
        String phoneNumbersString = phoneNumbers.get(0);
        for (int i = 1; i < phoneNumbers.size(); i++) {
            phoneNumbersString += "," + phoneNumbers.get(i);
        }
        return strToHash(String.format(
        		"appkey=%s&random=%d&time=%d&mobile=%s",
        		appkey, random, curTime, phoneNumbersString));
    }
}
