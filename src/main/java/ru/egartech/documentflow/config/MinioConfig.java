package ru.egartech.documentflow.config;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.egartech.documentflow.properties.S3Properties;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class MinioConfig {

    private final S3Properties s3Properties;

    @Bean
    public MinioClient minioClient() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(s3Properties.getEndpoint())
                .credentials(s3Properties.getAccessKey(), s3Properties.getSecretKey()).build();

        if(!minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(s3Properties.getCommonBucket())
                .build())) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(s3Properties.getCommonBucket())
                    .build());
        }
        if(!minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(s3Properties.getDraftBucket())
                    .build())) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(s3Properties.getDraftBucket())
                    .build());
        }
        LifecycleRule commonExpirationRule = new LifecycleRule(Status.ENABLED, null,
                new Expiration((ZonedDateTime) null, s3Properties.getDraftExpirationDays(), null),
                new RuleFilter(""), "draft-common-expiration", null,
                null, null);
        minioClient.setBucketLifecycle(SetBucketLifecycleArgs.builder()
                .bucket(s3Properties.getDraftBucket())
                .config(new LifecycleConfiguration(List.of(commonExpirationRule)))
                .build());

        return minioClient;
    }

}
