package com.hcmut.voltrent.service.file;

import com.hcmut.voltrent.annotations.S3Async;
import com.hcmut.voltrent.config.aws.AWSConfig;
import com.hcmut.voltrent.utils.FileUtils;
import com.hcmut.voltrent.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@S3Async
@Slf4j
public class AsyncS3Service implements IFileService<Mono<String>> {

    private final String bucketName;
    private final S3AsyncClient s3AsyncClient;
    private final AWSConfig awsConfig;
    private final S3Presigner presigner;

    public AsyncS3Service(S3AsyncClient s3AsyncClient, AWSConfig awsConfig, S3Presigner presigner) {
        this.awsConfig = awsConfig;
        this.bucketName = awsConfig.getBucketName();
        this.s3AsyncClient = s3AsyncClient;
        this.presigner = presigner;
    }

    @Override
    public Mono<String> upload(byte[] bytes, String fileName) {
        return upload(bytes, fileName, "" );
    }

    @Override
    public Mono<String> upload(MultipartFile file, String fileName) {
        try {
            return upload(file.getBytes(), fileName);
        } catch (Exception e) {
            return Mono.error(new RuntimeException(e));
        }
    }

    @Override
    public Mono<String> upload(String filePath, String fileName) {
        return Mono.just("" );
    }

    @Override
    public Mono<String> upload(byte[] files, String fileName, String folder) {
        return upload(files, fileName, folder, new HashMap<>());
    }

    @Override
    public Mono<String> upload(byte[] files, String fileName, String folder, Map<String, String> metadata) {
        if (files == null) {
            log.error("File to upload is null" );
            return Mono.error(new RuntimeException("File to upload is null" ));
        }

        String key = generateFileName(folder, fileName);
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .metadata(metadata)
                    .contentType(FileUtils.getContentType(fileName))
                    .build();

            return Mono.fromFuture(s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(files)))
                    .map(response -> {
                        String uploadedUrl = getS3Url(key);
                        log.info("File uploaded to S3 bucket successfully: [Key={}], [Url={}]", key, uploadedUrl);
                        return uploadedUrl;
                    })
                    .doOnError(S3Exception.class, exception -> log.error("S3 error uploading file: [ErrorCode={}] - [ErrorMessage={}]",
                            exception.awsErrorDetails().errorCode(), exception.awsErrorDetails().errorMessage()))
                    .doOnError(Exception.class, e -> log.error("Error uploading file to S3 bucket", e))
                    .onErrorMap(e -> new RuntimeException("Failed to upload file to S3", e));
        } catch (Exception e) {
            log.error("Error preparing file for S3 upload", e);
            return Mono.error(new RuntimeException("Failed to upload file to S3", e));
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

    private String getS3Url(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, awsConfig.getRegion(), key);
    }
}