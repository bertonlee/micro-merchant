package com.li.utils;

import org.apache.http.ssl.SSLContexts;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class SSLContextUtils {
    /**
     * 获取证书内容
     * @param certPath 证书地址
     * @param mchId 商户号
     * @return
     */
    public static SSLContext getSSLContext(String certPath, String mchId) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(certPath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            char[] partnerId2charArray = mchId.toCharArray();
            keystore.load(inputStream, partnerId2charArray);
            return SSLContexts.custom().loadKeyMaterial(keystore, partnerId2charArray).build();
        } catch (Exception var9) {
            throw new RuntimeException("证书文件有问题，请核实！", var9);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
