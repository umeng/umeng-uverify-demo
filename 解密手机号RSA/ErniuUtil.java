package com.aliyun.api.gateway.demo.util;

import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class ErniuUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static ECPrivateKey string2PrivateKey(String priStr) throws Exception {
        byte[] keyBytes = base642Byte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    public static byte[] base642Byte(String base64Str) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(base64Str);
    }

    public static String byte2Base64(byte[] bytes) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes);
    }

    public static byte[] privateDecrypt(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }

    public static byte[] privateDecryptRSA(String str, String privateKey) throws Exception {
        byte[] encryptedData = base642Byte(str);
        byte[] keyBytes = base642Byte(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        byte[] deBytes = null;
        for (int i = 0; i < encryptedData.length; i += 64) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(encryptedData, i, i + 64));
            deBytes = ArrayUtils.addAll(deBytes, doFinal);
        }
        return deBytes;
    }

    public static byte[] decryptAES(byte[] source, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(source);
    }

    public static SecretKey loadKeyAES(byte[] base64Key) throws Exception {
        return new SecretKeySpec(base64Key, "AES");
    }
}
