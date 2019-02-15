package com.li.upload.controller;

import com.li.upload.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 小微商户
 */
@Slf4j
@RequestMapping("/microMerchant")
@RestController
public class MicroMerchantUploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 上传小微商户图片
     *
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload")
    public String upload(@RequestParam(value = "file", required = false) MultipartFile multipartFile) {
        log.info("图片大小 {}", multipartFile.getSize());
        return uploadService.uploadFile(multipartFile);
    }
}
