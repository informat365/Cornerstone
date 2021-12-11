package cornerstone.biz.util;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
 
/**
 * 
 * @author cs
 *
 */
public class TripleDESUtil {
	//
	private static Logger logger=LoggerFactory.get(TripleDESUtil.class);
	//
	/**
	 * 生成密钥
	 * 
	 * @return
	 */
	public static byte[] createKey() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
			keyGen.init(168);
			SecretKey secretKey = keyGen.generateKey();
			return secretKey.getEncoded();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e);
		}
	}
	
	/**
	 * 
	 * @return byte[] base64 format
	 */
	public static String createBase64Key() {
		return Base64.getEncoder().encodeToString(createKey());
	}
 
	/**
	 * 加密
	 * @param data
	 * @param base64Key
	 * @return byte[] base64 format
	 */
	public static String encrypt(String data, String base64Key) {
		byte[] key=Base64.getDecoder().decode(base64Key);
		byte[] encryptData=encrypt(data.getBytes(), key);
		return Base64.getEncoder().encodeToString(encryptData);
	}
	
	/**
	 * 
	 * @param data
	 * @param base64Key
	 * @return
	 */
	public static String encryptWithUrlEncoder(String data, String base64Key) {
		byte[] key=Base64.getDecoder().decode(base64Key);
		byte[] encryptData=encrypt(data.getBytes(), key);
		return Base64.getUrlEncoder().encodeToString(encryptData);
	}
	
	/**
	 * 使用DES算法，对数据进行加密
	 * 
	 * @param data
	 *            要加密的数据
	 * @param key
	 *            加密数据的秘钥
	 * @return
	 */
	public static byte[] encrypt(byte[] data, byte[] key) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, "DESede");
			Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] cipherBytes = cipher.doFinal(data);
			return cipherBytes;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e);
		}
	}
 
	/**
	 * 解密
	 * @param base64Data
	 * @param base64Key
	 * @return new String(byte[])
	 */
	public static String decrypt(String base64Data, String base64Key) {
		byte[] key=Base64.getDecoder().decode(base64Key);
		byte[] data=Base64.getDecoder().decode(base64Data);
		return new String(decrypt(data, key));
	}
	
	/**
	 * 
	 * @param base64Data
	 * @param base64Key
	 * @return
	 */
	public static String decryptWithUrlEncoder(String base64Data, String base64Key) {
		byte[] key=Base64.getDecoder().decode(base64Key);
		byte[] data=Base64.getUrlDecoder().decode(base64Data);
		return new String(decrypt(data, key));
	}
	/**
	 * 使用DES数据解密
	 * 
	 * @param data
	 *            要解密的数据
	 * @param key
	 *            解密数据用到的秘钥
	 * @return
	 */
	public static byte[] decrypt(byte[] data, byte[] key) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, "DESede");
			Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] plainBytes = cipher.doFinal(data);
			return plainBytes;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e);
		}
	}
}
