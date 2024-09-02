package ru.egartech.documentflow.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("s3")
public class S3Properties {

    private final String endpoint;

    private final String accessKey;

    private final String secretKey;

    private final String commonBucket;

    private final String draftBucket;

    private final Long minContentLength;

    private final Long maxContentLength;

    private final Integer draftExpirationDays;

    private final Integer uploadExpirationMinutes;

    private final Integer downloadExpirationMinutes;

}
