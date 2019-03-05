package com.li.getcertficates.service.impl;

import com.li.getcertficates.service.CertFicatesService;
import com.li.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CertFicatesServiceImpl implements CertFicatesService {
    @Override
    public String getCertFicates() {
        // 初始化一个HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // Post请求
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/risk/getcertficates");
        /**
         * 这边需要您提供微信分配的商户号跟API密钥
         */
        Map<String, String> param = new HashMap<>(4);
        param.put("mch_id", "微信分配的商户号");
        param.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
        // 暂只支持HMAC-SHA256 加密
        param.put("sign_type", "HMAC-SHA256");
        // 对你的参数进行加密处理
        param.put("sign", SignUtil.wechatCertficatesSignBySHA256(param, "API密钥(mch_key)"));
        httpPost.setEntity(new StringEntity(map2Xml(param), "UTF-8"));
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.getMimeType());
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            log.info("获取平台证书响应 {}", httpResponse);
            if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
                String responseEntity = EntityUtils.toString(httpResponse.getEntity());
                Document document = DocumentHelper.parseText(responseEntity);
                if ("SUCCESS".equalsIgnoreCase(document.selectSingleNode("//return_code").getStringValue())
                        && "SUCCESS".equalsIgnoreCase(document.selectSingleNode("//result_code").getStringValue())) {
                    return document.selectSingleNode("//certificates").getStringValue();
                }
                log.error("请求平台证书序号响应异常 {}", document.selectSingleNode("//return_msg").getStringValue());
            }
        } catch (Exception e) {
            log.error("执行httpclient请求平台证书序号错误 {}", e);
        }
        return null;
    }

    @Override
    public String decryptCertSN(String associatedData, String nonce, String cipherText, String apiv3Key) throws Exception{
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(apiv3Key.getBytes(), "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, nonce.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        cipher.updateAAD(associatedData.getBytes());
        return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
    }

    /**
     * map对象转xml
     *
     * @param map
     * @return
     */
    private String map2Xml(Map<String, String> map) {
        StringBuilder result = new StringBuilder();
        result.append("<xml>");
        if (map != null && map.keySet().size() > 0) {
            map.forEach((key, value) -> {
                result.append("<" + key + "><![CDATA[");
                result.append(value);
                result.append("]]></" + key + ">");
            });
        }
        result.append("</xml>");
        return result.toString();
    }
}
