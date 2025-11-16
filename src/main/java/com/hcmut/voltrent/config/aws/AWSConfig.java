package com.hcmut.voltrent.config.aws;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Getter
public class AWSConfig {

    private final AwsBasicCredentials awsCredentials;
    private final String region;
    private final String bucketName;

    public AWSConfig(@Value("${aws.s3.region}" ) String region,
                     @Value("${aws.credentials.access-key}" ) String accessKey,
                     @Value("${aws.credentials.secret-key}" ) String secretKey,
                     @Value("${aws.s3.bucket-name}" ) String bucketName
    ) {
        this.region = region;
        this.awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.bucketName = bucketName;
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(() -> awsCredentials)
                .build();
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(() -> awsCredentials)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(() -> awsCredentials)
                .s3Client(s3Client())
                .build();
    }
}
