package com.li.utils;

import javax.crypto.Cipher;
import javax.security.cert.X509Certificate;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.Base64;

public class EncryptionUtils {
    private static final String CIPHER_PROVIDER = "SunJCE";
    private static final String TRANSFORMATION_PKCS1Paddiing = "RSA/ECB/PKCS1Padding";

    private static final String CHAR_ENCODING = "UTF-8";//固定值，无须修改


    //数据加密方法
    private static byte[] encryptPkcs1padding(PublicKey publicKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(TRANSFORMATION_PKCS1Paddiing, CIPHER_PROVIDER);
        ci.init(Cipher.ENCRYPT_MODE, publicKey);
        return ci.doFinal(data);
    }
    //加密后的秘文，使用base64编码方法
    private static String encodeBase64(byte[] bytes) throws Exception {
        return Base64.getEncoder().encodeToString(bytes);
    }
    /**
     *  对敏感内容（入参Content）加密
     *  path 为平台序列号接口解密后的密钥 pem 路径
     */
    public static String rsaEncrypt(String Content, String path) throws Exception {
        final byte[] PublicKeyBytes = Files.readAllBytes(Paths.get(path));
        X509Certificate certificate = X509Certificate.getInstance(PublicKeyBytes);
        PublicKey publicKey = certificate.getPublicKey();

        return encodeBase64(encryptPkcs1padding(publicKey, Content.getBytes(CHAR_ENCODING)));
    }

    /**
     * 为了自己方便，多加个个传内容的，因为我解密后并没有保存到文件里，而是自己重新解密
     * 要问为什么？
     * 需求有多个服务商号，没办法
     * @param Content
     * @param certStr
     * @return
     * @throws Exception
     */
    public static String rsaEncryptByCert(String Content, String certStr) throws Exception {
        X509Certificate certificate = X509Certificate.getInstance(certStr.getBytes());
        PublicKey publicKey = certificate.getPublicKey();
        return encodeBase64(encryptPkcs1padding(publicKey, Content.getBytes(CHAR_ENCODING)));
    }

}
