package cornerstone.biz.util;
 
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
 
/**
 * 
 * @author cs
 *
 */
public class RSA {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";
    public static final int KEY_SIZE = 2048;

    private static Logger logger = LoggerFactory.get(RSA.class);
    /**
     * 生成密钥对
     * 
     * @return
     */
    public static Map<String, byte[]> generateKeyBytes() {
 
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator
                    .getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            Map<String, byte[]> keyMap = new HashMap<String, byte[]>();
            keyMap.put(PUBLIC_KEY, publicKey.getEncoded());
            keyMap.put(PRIVATE_KEY, privateKey.getEncoded());
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(),e);;
        }
        return null;
    }
 
    /**
     * 还原公钥
     * 
     * @param keyBytes
     * @return
     */
    public static PublicKey restorePublicKey(byte[] keyBytes) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error(e.getMessage(),e);;
        }
        return null;
    }
 
    /**
     * 还原私钥
     * 
     * @param keyBytes
     * @return
     */
    public static PrivateKey restorePrivateKey(byte[] keyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = factory
                    .generatePrivate(pkcs8EncodedKeySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error(e.getMessage(),e);;
        }
        return null;
    }
}