package com.li.upload.service.impl;

import com.li.upload.service.UploadService;
import com.li.utils.SSLContextUtils;
import com.li.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class UploadServiceImpl implements UploadService{
    @Override
    public String uploadFile(MultipartFile multipartFile) {
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/secapi/mch/uploadmedia");
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.MULTIPART_FORM_DATA.getMimeType());
        CloseableHttpClient client = null;
        File excelFile = null;
        String error = null;
        try {
            client = HttpClients.custom().setSSLContext(SSLContextUtils.getSSLContext("证书地址","商户号")).build();
            // 生成签名和图片md5加密
            String hash = DigestUtils.md5Hex(multipartFile.getBytes());
            Map<String, String> param = new HashMap<>(3);
            param.put("media_hash", hash);
            param.put("mch_id", "商户号");
            param.put("sign_type", "HMAC-SHA256");
            // 配置post图片上传
            // 用uuid作为文件名，防止生成的临时文件重复
            excelFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg");
            multipartFile.transferTo(excelFile);
            FileBody bin = new FileBody(excelFile, ContentType.create("image/jpg", Consts.UTF_8));
            HttpEntity build = MultipartEntityBuilder.create().setCharset(Charset.forName("utf-8"))
                    .addTextBody("media_hash", hash)
                    .addTextBody("mch_id", "商户号")
                    .addTextBody("sign_type", "HMAC-SHA256")
                    .addTextBody("sign", SignUtil.wechatCertficatesSignBySHA256(param, "api密钥"))
                    .addPart("media", bin)
                    .build();
            httpPost.setEntity(build);
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
                String responseEntity = EntityUtils.toString(httpResponse.getEntity());
                log.info("upload response {}", responseEntity);
                Document document = DocumentHelper.parseText(responseEntity);
                if ("SUCCESS".equalsIgnoreCase(document.selectSingleNode("//return_code").getStringValue())) {
                    if ("SUCCESS".equalsIgnoreCase(document.selectSingleNode("//result_code").getStringValue())) {
                        return document.selectSingleNode("//media_id").getStringValue();
                    }
                }
                log.error("上传图片失败，异常信息 code ={} des = {}", document.selectSingleNode("//err_code").getStringValue(), document.selectSingleNode("//err_code_de").getStringValue());
                error = document.selectSingleNode("//err_code_de").getStringValue();
            }
        } catch (Exception e) {
            log.error("微信图片上传异常 ， e={}", e);
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    log.warn("关闭资源httpclient失败 {}", e);
                }
            }
            if (excelFile != null) {
                deleteFile(excelFile);
            }
        }
        return error;
    }
    /**
     * 删除临时文件
     *
     * @param files
     */
    private void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
