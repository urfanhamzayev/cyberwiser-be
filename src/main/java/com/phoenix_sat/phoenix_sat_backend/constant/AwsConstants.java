package com.phoenix_sat.phoenix_sat_backend.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AwsConstants {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKeyForEmail;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKeyForEmail;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${aws.s3.access-key}")
    private String accessKeyForS3;

    @Value("${aws.s3.secret-key}")
    private String secretKeyForS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

}
