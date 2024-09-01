package com.phoenix_sat.phoenix_sat_backend.service;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    void uploadFile(String keyName, MultipartFile file);

    S3Object getFile(String keyName);
}
