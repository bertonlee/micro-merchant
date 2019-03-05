package com.li.upload;


import com.li.getcertficates.service.CertFicatesService;
import com.li.utils.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CertFicatesServiceImplTest {
    @Autowired
    private CertFicatesService certFicatesService;

    @Test
    public void decryptCertSNTest() {
        try {
            String content = certFicatesService.decryptCertSN("associatedData", "nonce", "cipherText", "apiv3Key");
            String encrypt = EncryptionUtils.rsaEncryptByCert("我的身份证", content);
            log.info("身份证的密文了 {}",encrypt);
        } catch (Exception e) {
            log.error("解密异常啦 {}", e);
        }
    }

}
