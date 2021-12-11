package cornerstone.biz.util;

import java.io.IOException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jazmin.core.app.AppException;
import jazmin.util.FileUtil;

/**
 * 
 * @author cs
 *
 */
public class HmacSha1 {

	/**
	 * 
	 * @param value
	 * @param key
	 * @return
	 */
	public static String hmacSha1(String value, String key) {
		try {
			// Get an hmac_sha1 key from the raw key bytes
			byte[] keyBytes = key.getBytes();
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

			// Get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			return byte2hex(mac.doFinal(value.getBytes()));
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	// 二行制转字符串
	private static String byte2hex(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b != null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toUpperCase();
	}


}
