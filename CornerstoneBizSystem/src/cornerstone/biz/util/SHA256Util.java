package cornerstone.biz.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jazmin.core.app.AppException;

/**
 * HMACSHA256
 * @author cs
 *
 */
public class SHA256Util {
	
	/**
	 * 
	 * @param data
	 * @param secret
	 * @return
	 */
	public final static String encode(String data, String secret) {
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(data.getBytes());
            String hash = byteArrayToHexString(bytes);
			return hash;

		} catch (Exception e) {
			throw new AppException(e);
		}
	}
	
	/**
	 * 
	 * @param b
	 * @return
	 */
	private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }
	//
}
