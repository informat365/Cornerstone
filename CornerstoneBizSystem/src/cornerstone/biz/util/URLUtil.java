package cornerstone.biz.util;

import java.io.InputStream;
import java.util.Map;

import io.itit.itf.okhttp.FastHttpClient;
import io.itit.itf.okhttp.GetBuilder;
import io.itit.itf.okhttp.Response;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;


/**
 * @author icecooly
 *
 */
public class URLUtil {
	//
	private static Logger logger=LoggerFactory.getLogger(URLUtil.class);
	//
	public static String httpGet(String url){
		String rspBody=null;
		try {
			Response response=FastHttpClient.get().
					url(url).
					build().
					execute();
			checkResult(response);
			rspBody=response.string();
		}catch (Exception e) {
			throw new AppException(e);
		}finally {
			if(logger.isInfoEnabled()){
				logger.info("url:{} \nresponse:{}",url,rspBody);
			}
		}
		return rspBody;
	}
	//
	public static InputStream httpGetReturnInputStream(String url){
		InputStream rspBody=null;
		try {
			Response response=FastHttpClient.get().
					url(url).
					build().
					execute();
			checkResult(response);
			rspBody=response.byteStream();
		}catch (Exception e) {
			throw new AppException(e);
		}finally {
			if(logger.isInfoEnabled()){
				logger.info("url:{} \nresponse:{}",url,rspBody);
			}
		}
		return rspBody;
	}
	//
	public static String httpGet(String url,Map<String,String> paraMap){
		return httpGet(url, paraMap,null);
	}
	//
	public static String httpGet(String url,Map<String,String> paraMap,Map<String,String> headers){
		String rspBody=null;
		try {
			StringBuilder sb=new StringBuilder();
			sb.append(url).append("?");
			if(paraMap!=null) {
				paraMap.forEach((k,v)->{
					sb.append(k).append("=").append(v).append("&");
				});
				if(paraMap.size()>0){
					sb.deleteCharAt(sb.length()-1);
				}
			}
			url=sb.toString();
			GetBuilder builder=FastHttpClient.get();
			builder.url(url);
			if(headers!=null) {
				headers.forEach((k,v)->{
					builder.addHeader(k, v);
				});
			}
			Response response = builder.build().execute();
			checkResult(response);
			rspBody=response.body().string();
		}catch (Exception e) {
			throw new AppException(e);
		}finally {
			if(logger.isInfoEnabled()){
				logger.info("url:{} \nparaMap:{} \nheaders:{}\nresponse:{}",url,
						DumpUtil.dump(paraMap),DumpUtil.dump(headers),
						rspBody);
			}
		}
		return rspBody;
	}
	//
	private static void checkResult(Response response) {
		if(!response.isSuccessful()) {
			throw new IllegalArgumentException(response.code()+"/"+response.message());
		}
	}
	//
	public static String httpPost(String url,Map<String,String> paramMap){ 
		return httpPost(url, paramMap, null);
	}
	//
	public static String httpPost(String url,Map<String,String> paramMap,Map<String,String> headers){ 
		String rspBody=null;
		try {
			Response response = FastHttpClient.post().
					url(url).
					addHeaders(headers).
					addParams(paramMap).
					build().
					execute();
			checkResult(response);
			rspBody=response.string();
		} catch (Exception e) {
			throw new AppException(e);
		}finally {
			if(logger.isInfoEnabled()){
				logger.info("url:{} params:{}\nresponse:{}",url,DumpUtil.dump(paramMap),rspBody);
			}
		}
		return rspBody;
	}
	//
	public static String httpPut(String url,Map<String,String> paramMap,Map<String,String> headers){ 
		String rspBody=null;
		try {
			Response response = FastHttpClient.put().
					url(url).
					addHeaders(headers).
					addParams(paramMap).
					build().
					execute();
			checkResult(response);
			rspBody=response.string();
		} catch (Exception e) {
			throw new AppException(e);
		}finally {
			if(logger.isInfoEnabled()){
				logger.info("url:{} params:{}\nresponse:{}",url,DumpUtil.dump(paramMap),rspBody);
			}
		}
		return rspBody;
	}
	//
	public static String httpPostWithBody(String url,String body){ 
		return httpPostWithBody(url, body, null);
	}
	//
	public static String httpPostWithBody(String url,String body,Map<String,String> headers){ 
		String rspBody=null;
		try {
			Response response=FastHttpClient.post().
					url(url).
					addHeaders(headers).
					body(body).
					build().
					execute();
			checkResult(response);
			rspBody=response.string();
		}catch (Exception e) {
			throw new AppException(e);
		}finally {
			if(logger.isInfoEnabled()){
				logger.info("url:{} body:{} \nresponse:{}",url,body,rspBody);
			}
		}
		return rspBody;
	}
	//
	public static String httpPutWithBody(String url,String body,Map<String,String> headers){ 
		String rspBody=null;
		try {
			Response response=FastHttpClient.put().
					url(url).
					addHeaders(headers).
					body(body).
					build().
					execute();
			checkResult(response);
			rspBody=response.string();
		}catch (Exception e) {
			throw new AppException(e);
		}finally {
			if(logger.isInfoEnabled()){
				logger.info("url:{} body:{} \nresponse:{}",url,body,rspBody);
			}
		}
		return rspBody;
	}
	//
	public static String httpPutWithBody(String url,String body){ 
		String rspBody=null;
		try {
			Response response=FastHttpClient.put().
					url(url).
					body(body).
					build().
					execute();
			checkResult(response);
			rspBody=response.string();
		}catch (Exception e) {
			throw new AppException(e);
		}finally {
			if(logger.isInfoEnabled()){
				logger.info("url:{} body:{} \nresponse:{}",url,body,rspBody);
			}
		}
		return rspBody;
	}
}
