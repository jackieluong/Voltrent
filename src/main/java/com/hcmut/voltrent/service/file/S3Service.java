package com.hcmut.voltrent.service.file;

import com.hcmut.voltrent.annotations.S3;
import com.hcmut.voltrent.config.aws.AWSConfig;
import com.hcmut.voltrent.utils.FileUtils;
import com.hcmut.voltrent.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@S3
@Slf4j
public class S3Service implements IFileService<String> {

    private final String bucketName;
    private final S3Client s3Client;
    private final AWSConfig awsConfig;
    private final S3Presigner presigner;

    public S3Service(S3Client s3Client, AWSConfig awsConfig, S3Presigner presigner) {
        this.awsConfig = awsConfig;
        this.bucketName = awsConfig.getBucketName();
        this.s3Client = s3Client;
        this.presigner = presigner;
    }


    @Override
    public String upload(byte[] bytes, String fileName) {
        return upload(bytes, fileName, "" );
    }

    @Override
    public String upload(MultipartFile file, String fileName) {

        try {
            return upload(file.getBytes(), fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String upload(String filePath, String fileName) {
        return "";
    }

    @Override
    public String upload(byte[] files, String fileName, String folder) {

        return upload(files, fileName, folder, new HashMap<>());
    }

    @Override
    public String upload(byte[] files, String fileName, String folder, Map<String, String> metadata) {
        if (files == null) {
            log.error("File to upload is null" );
            throw new RuntimeException("File to upload is null" );
        }

        String key = generateFileName(folder, fileName);
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .metadata(metadata)
                    .contentType(FileUtils.getContentType(fileName))
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(files));

            String uploadedUrl = getS3Url(key).toString();
            log.info("File uploaded to S3 bucket successfully: [Key={}], [Url={}]", key, uploadedUrl);
            return uploadedUrl;
        } catch (S3Exception exception) {
            log.error("S3 error uploading file: [ErrorCode={}] - [ErrorMessage={}]",
                    exception.awsErrorDetails().errorCode(), exception.awsErrorDetails().errorMessage());
            throw new RuntimeException("Failed to upload file to S3", exception);
        } catch (Exception e) {
            log.error("Error uploading file to S3 bucket", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public byte[] download(String fileName) {
        return new byte[0];
    }

    @Override
    public void delete(String fileName) {

    }

    @Override
    public boolean exists(String fileName) {
        return false;
    }

    private String generateFileName(String folderName, String originalFileName) {
        StringBuilder fileName = new StringBuilder();
        if (!StringUtils.isNullOrEmpty(folderName)) {
            fileName.append(folderName).append("/" );
        }

        fileName.append(StringUtils.nullToDefaultString(originalFileName,
                UUID.randomUUID().toString()));
        return fileName.toString();
    }

    public String getPresignedUrl(String fileName, int expirationMinutes) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(expirationMinutes))
                        .getObjectRequest(getObjectRequest)
                        .build()
        );
        return presignedRequest.url().toString();
    }

    private URL getS3Url(String key) {
        return s3Client.utilities().getUrl(builder ->
                builder.bucket(bucketName)
                        .key(key)
                        .region(Region.of(awsConfig.getRegion()))
                        .build());
    }
}
