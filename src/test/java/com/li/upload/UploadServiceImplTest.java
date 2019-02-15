package com.li.upload;

import com.li.upload.service.UploadService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UploadServiceImplTest {
    @Autowired
    private UploadService uploadService;

    @Test
    public void uploadFile() throws Exception {
        File file = new File("图片路径");
        MultipartFile multipartFile = new MockMultipartFile("file", new FileInputStream(file));
        String s = uploadService.uploadFile(multipartFile);
        System.out.println(s);
    }
}
