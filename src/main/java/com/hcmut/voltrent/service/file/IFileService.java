package com.hcmut.voltrent.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IFileService <T> {
    T upload(byte[] bytes, String fileName);

    T upload(MultipartFile file, String fileName) throws IOException;

    T upload(String filePath, String fileName);

    T upload(byte[] files, String fileName, String folder);

    T upload(byte[] files, String fileName, String folder, Map<String, String> metadata);

    byte[] download(String fileName);

    void delete(String fileName);

    boolean exists(String fileName);

}
