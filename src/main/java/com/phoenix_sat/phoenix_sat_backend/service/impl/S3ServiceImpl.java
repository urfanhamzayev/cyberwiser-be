package com.phoenix_sat.phoenix_sat_backend.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.phoenix_sat.phoenix_sat_backend.constant.AwsConstants;
import com.phoenix_sat.phoenix_sat_backend.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Log4j2
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3client;
    private final AwsConstants awsConstants;

    @SneakyThrows
    public void uploadFile(String keyName, MultipartFile file) {
        var putObjectResult = s3client.putObject(awsConstants.getBucketName(),
                keyName, file.getInputStream(), null);
        log.info(putObjectResult.getMetadata());
    }

    public S3Object getFile(String keyName) {
        return s3client.getObject(awsConstants.getBucketName(), keyName);
    }

}
