package com.hcmut.voltrent.utils;

import org.springframework.http.MediaType;

import java.util.Base64;

public class FileUtils {

    public static byte[] decodeQRBase64(String dataUri) {
        // Remove prefix: "data:image/png;base64,"
        String base64Data = dataUri.substring(dataUri.indexOf("," ) + 1);
        return Base64.getDecoder().decode(base64Data);
    }

    public static String getContentType(String fileName) {

        if (fileName.endsWith(".png" )) {
            return MediaType.IMAGE_PNG_VALUE;
        } else if (fileName.endsWith(".jpg" ) || fileName.endsWith(".jpeg" )) {
            return MediaType.IMAGE_JPEG_VALUE;
        } else if (fileName.endsWith(".gif" )) {
            return MediaType.IMAGE_GIF_VALUE;
        } else if (fileName.endsWith(".pdf" )) {
            return MediaType.APPLICATION_PDF_VALUE;
        }

        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
