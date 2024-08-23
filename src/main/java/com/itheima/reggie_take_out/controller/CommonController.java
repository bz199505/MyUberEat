package com.itheima.reggie_take_out.controller;

import com.itheima.reggie_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * file upload/download
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        // file is a temp file
        log.info("basepath {}", basePath);

        // get extension name
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));

        // use uuid to deduplication
        String fileName = UUID.randomUUID().toString() + suffix;
        // check whether the dir really exist
        File dir = new File(basePath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                // Handle the case where the directory was not created
                throw new IOException("Failed to create directory: " + dir.getAbsolutePath());
            }
        }

        file.transferTo(new File(basePath + fileName));
        return R.success(fileName);
    }

    /**
     * @param response
     * @param fileName
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) throws IOException {
        // input stream for reading file
        log.info("fileName in download {}", name);
        FileInputStream fis = new FileInputStream(new File(basePath + name));
        // output stream for write into browser, show the picture on browser
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fis.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
            outputStream.flush();
        }

        outputStream.close();
        fis.close();
    }
}
