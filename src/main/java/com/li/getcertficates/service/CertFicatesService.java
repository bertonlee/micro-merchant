package com.li.getcertficates.service;

/**
 * 平台序证书获取
 */
public interface CertFicatesService {
    String getCertFicates();

    /**
     * 平台证书解密
     * @param associatedData
     * @param nonce
     * @param cipherText
     * @param apiv3Key
     * @return
     */
    String decryptCertSN(String associatedData, String nonce, String cipherText, String apiv3Key)throws Exception;
}
