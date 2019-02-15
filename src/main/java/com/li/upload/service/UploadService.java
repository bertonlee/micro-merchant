package com.li.upload.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传
 */
public interface UploadService {
    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    String uploadFile(MultipartFile multipartFile);
}
