package com.phoenix_sat.phoenix_sat_backend.constant;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

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
